package rain.score

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.draw.Drawer
import org.openrndr.launch
import rain.graph.Label
import rain.graph.Node
import rain.graph.NodeLabel
import rain.language.patterns.relationships.PLAYS
import rain.rndr.nodes.DrawStyle
import rain.score.nodes.Event
import rain.score.nodes.Machine
import rain.utils.autoKey
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class Score protected constructor(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<Node, Score>(
        Node, Score::class, { k -> Score(k) }
    )

    inner class ScoreContext(
        val event: Event,
        val program: Program,
        val parent: ScoreContext? = null,
        // TODO: add renderTarget
    ) {

        val score = this@Score

        val machine: Machine? = event.machine ?: parent?.machine

        val drawStyle: DrawStyle? = event.drawStyle ?: parent?.drawStyle

        val unitLength get() = score.unitLength

        fun childContext(
            event: Event,
        ): ScoreContext = ScoreContext(
            event,
            program,
            this,
        )

        suspend fun play(
            exitOnComplete:Boolean=false
        ) {

            // TODO: move this to score execute loop
            fun updateMachines(
                startingMachine: Machine,
                eventSlots: Map<String, Node.DataSlot<*>>,
                prefix: String,
            ) {
                // update machine slot values based on slot event values,
                // following the path
                // animating where animation defined in the slot name/value

                val dirtyMachines = mutableMapOf(prefix to startingMachine)

                eventSlots.forEach { (fullName, slot) ->

                    var myMachine: Machine? = startingMachine

                    fullName.substringAfter(prefix).split(".").apply {
                        take(size-1).forEach {relatedName->
                            myMachine = myMachine?.slot<Machine?>(relatedName)?.value
                            // using full name so that we can sort by the reverse length order to
                            // call updateDataFromSlots from most specific (longest) to the least specific
                            myMachine ?.let { dirtyMachines[fullName] = it }
                        }
                        last().let {name->
                            if (name.endsWith(":animate")) {
                                name.substringBefore(":animate").let { animateName->
                                    myMachine?.let { m ->
                                        score.gateMachineAnimation(m, true)
                                        m.slot<Double>(animateName)?.property?.let { property ->
                                            m.machineAnimation.bumpAnimation(
                                                property,
                                                slot.value as Event.AnimateEventValue
                                            )
                                        }
                                    }
                                }
                            } else
                                myMachine?.updateSlotFrom(name, slot)
                        }
                    }
                }
                dirtyMachines.toSortedMap().toList().reversed().forEach {(_,m)->
                    m.setContext(this)
                    m.clean()
                }
            }

            machine?.let { m ->
//            m.setContext(this)
                event.gate.startGate?.let { score.gateMachine(m, it) }
                if (event.bumping) {
                    updateMachines(m, event.machineSlots, "machine.")
                    // TODO: is "bumping" even worth it???
                    m.bump(this)
                }
            }

            event.styleSlots.let {ss ->
                if (ss.isNotEmpty()) {
                    drawStyle?.let {ds->
//                    ds.setContext(this)
                        updateMachines(ds, ss, "style.")
                        if (ds.fill?.hasAnimations==true || ds.stroke?.hasAnimations==true) {
                            score.gateMachineAnimation(ds, true)
                        }
                    }
                }
            }


//        println("adding delay: $addDelay")
            event.dur?.let { dur -> if (dur > 0.0) delay(dur.toDuration(DurationUnit.SECONDS)) }
            // TODO: adjust for delta in time delay

            if (event.simultaneous) {
                val threads: MutableList<Job> = mutableListOf()
                event.children.forEach {
                    threads.add(program.launch { childContext(it).play() })
                }
                threads.joinAll()
                threads.
            } else
                event.children.forEach { childContext(it).play() }


            machine?.let { m -> event.gate.endGate?.let { score.gateMachine(m, it) } }

            if (exitOnComplete) exit()

        }

        fun exit() {
            println("EXITING: ==========================================================")
            println("Program complete after ${program.seconds} seconds")
            program.application.exit()
            //TODO: notice the time creep here!!! How to deal with this?
        }

        fun applyDrawing(block: Drawer.()->Unit) {
            program.drawer.apply {
                this@ScoreContext.drawStyle?.let {
                    drawStyle = it.rndrDrawStyle
                }
                block(this)
            }
        }

    }


    override val label: Label<out Node, out Score> = Score

    var width by DataSlot("width", 1920.0)
    var height by DataSlot("height", 1080.0)

    // represents a grid with 30x30 pixel squares at 1920x1080 resolution
    var unitLength  by DataSlot("unitLength", 30.0)

    // TODO: used?
    var plays by RelatedNodeSlot("plays", +PLAYS, Event, null)

    val widthUnits get() = width / unitLength // default = 64

    val heightUnits get() = height / unitLength // default = 36

    // TODO: implement preload material...

    fun asDoubleRes(): Score {
        width *= 2.0
        height *= 2.0
        unitLength *= 2
        return this
    }

    fun asHalfRes(): Score {
        width /= 2.0
        height /= 2.0
        unitLength /= 2
        return this
    }


    // TODO: needed?
//    private var myProgram: Program? = null

    // TODO maybe: combine into a more sensibly managed "runningMachines" data structure?
    private val bumpingMachines: MutableList<Machine> = mutableListOf()
    private val renderingMachines: MutableMap<String, Machine> = mutableMapOf()
    private val animatingMachines: MutableMap<String, Machine> = mutableMapOf()

    fun gateMachine(machine: Machine, gate: Boolean) {
//        println("gating $gate - $machine")
        machine.gate(gate)
        if (gate) {
            renderingMachines[machine.key] = machine
        } else {
            renderingMachines.remove(machine.key)
        }
    }

    fun gateMachineAnimation(machine: Machine, gate: Boolean) {
        if (gate) {
            animatingMachines[machine.key] = machine
        } else {
            animatingMachines.remove(machine.key)
        }
    }

    fun gateOffMachinesAnimation(keys: List<String>) {
        keys.forEach {k->  animatingMachines.remove(k)  }
    }

    fun play(block: Score.(Program)->Event) {
        application {
            configure {
                width = this@Score.width.toInt()
                height = this@Score.height.toInt()
            }
            program {
                launch {
                    ScoreContext(
                        block.invoke(this@Score, this@program),
                        this@Score,
                        this@program,
                    ).play(true)
                }
                extend {
                    // TODO: consider ... machines are executed in no particular order, is that OK?

                    // 0 (maybe), verify that all bumps received?
                    // 1 execute all "bumps" (first machine bumps, then style bumps)
                    // ... (new pass)
                    // 2 animate all machines
                    // ... (new pass?)
                    // 3 update any cached values
                    // ... (new pass)
                    // 4 render

                    val gateOffKeys = mutableListOf<String>()
                    animatingMachines.forEach { (_, machine) ->
                        machine.
                        if (!machine.updateAnimation())
                            gateOffKeys.add(machine.key)
                    }
                    if (gateOffKeys.isNotEmpty()) gateOffMachinesAnimation(gateOffKeys)

                    renderingMachines.forEach { it.value.render() }

                }
            }
        }
    }

    fun play(event: Event) = play { event }

    fun play() = plays?.let {p-> play { p } }

}

val DEFAULT_SCORE = Score.create("DEFAULT_SCORE")