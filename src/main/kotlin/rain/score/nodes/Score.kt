package rain.score.nodes

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
import rain.language.patterns.relationships.DIRTIES
import rain.language.patterns.relationships.PLAYS
import rain.rndr.nodes.DrawStyle
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
        machine: Machine? = null,
        // TODO: add renderTarget
    ) {

        val score = this@Score

        val machine: Machine? = machine ?: event.machine ?: parent?.machine

        val drawStyle: DrawStyle? = event.drawStyle ?: parent?.drawStyle

        val unitLength get() = score.unitLength

        val childContexts = mutableMapOf<Pair<String, String?>, ScoreContext>()

        fun childContext(
            event: Event,
            machine: Machine? = null,
        ): ScoreContext = childContexts.getOrPut(Pair(event.key, machine?.key)) {
            ScoreContext(
                event,
                program,
                this,
                machine
            )
        }

        fun addRefreshing(dirtyMachine:Machine) {
            if (dirtyMachine.hasPlaybackRefresh) {
//                println("Adding refreshing for $dirtyMachine")
                refreshingContexts.add(childContext(event, dirtyMachine))
            }
            dirtyMachine[+DIRTIES](Machine).forEach { addRefreshing(it) }
        }

        // TODO: maybe move to Machine class?
        fun bump() {

//            println("bumping ${this.machine}")

            // TODO: move this to score execute loop
            fun updateMachines(
                startingMachine: Machine,
                eventSlots: Map<String, Node.DataSlot<*>>,
                prefix: String,
            ) {
                // update machine slot values based on slot event values,
                // following the path
                // animating where animation defined in the slot name/value

//                println(eventSlots)

                eventSlots.forEach { (fullName, slot) ->

                    var myMachine: Machine? = startingMachine

                    // TODO maybe: track all contexts here and bump related machines to
                    //  update the activeContext on each machine?
                    //  (assume not necessary, since only currently using activeContext
                    //  for rendering logic, NOT animating or refreshing)

                    fullName.substringAfter(prefix).split(".").apply {
//                        println(this)
                        take(size-1).forEach {relatedName->
                            myMachine = myMachine?.slot<Machine?>(relatedName)?.value
                            myMachine ?.let { m-> addRefreshing(m) }
                        }
                        last().let {name->


                            // TODO: package up the properties to update from the same machine
                            //  and then bump them all together

                            if (name.endsWith(":animate")) {
                                name.substringBefore(":animate").let { animateName->
                                    myMachine?.let { m ->

                                        m.slot<Double>(animateName)?.property?.let { property ->

                                            // TODO: this is added repeatedly for each slot (should avoid that)
                                            animatingContexts.add(childContext(event, m))

//                                            println("Adding ANIMATION for $myMachine for slot $animateName")

                                            m.bumpAnimation(
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
            }

            // NOTE: important to update styles first for refreshing/rendering order
            // (so that style updates applied before any related refreshing/rendering)
            if (event.bumping) {
                event.styleSlots.let { ss ->
                    if (ss.isNotEmpty()) {
                        drawStyle?.let { ds ->
                            updateMachines(ds, ss, "style.")
                            ds.bump(this)
                        }
                    }
                }
            }

            // TODO: think about whether bumping applies to only machine updates, or also style updates
            // or whether to remove it altogether
            machine?.let { m ->
                if (event.bumping) {
                    updateMachines(m, event.machineSlots, "machine.")
                    // TODO: is "bumping" even worth it???
                    m.bump(this)
                }
                event.gate.startGate?.let {g->
                    m.gate(g)
                    if (g) renderingContexts.add(this)
                }
            }



        }

        suspend fun play(
            exitOnComplete:Boolean=false
        ) {

            bumpingContexts.add(this)

//        println("adding delay: $addDelay")
            event.dur?.let { dur -> if (dur > 0.0) delay(dur.toDuration(DurationUnit.SECONDS)) }
            // TODO: adjust for delta in time delay

            // TODO: maybe add startGate here

            if (event.simultaneous) {
                val threads: MutableList<Job> = mutableListOf()
                event.children.forEach {
                    threads.add(program.launch { childContext(it).play() })
                }
                threads.joinAll()
            } else
                event.children.forEach { childContext(it).play() }

            machine?.let { m -> event.gate.endGate?.let { m.gate(it) } }

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

    private val bumpingContexts = ArrayList<ScoreContext>()
    private val animatingContexts: MutableSet<ScoreContext> = HashSet()

    // using linkedHasSet for refreshing and rendering to keep order:
    private val refreshingContexts: MutableSet<ScoreContext> = LinkedHashSet()
    private val renderingContexts: MutableSet<ScoreContext> = LinkedHashSet()

    fun MutableSet<ScoreContext>.executeAll(
        keepCondition: (Machine)->Boolean,
        block: (ScoreContext, Machine)->Unit,
    ) {
        this.iterator().let {
            while (it.hasNext()) {
                it.next().let { context->
                    context.machine?.let {m->
                        if (keepCondition(m)) {
                            block(context, m)
                        } else it.remove()
                    }
                }
            }
        }
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
                        this@program,
                    ).play(true)
                }
                extend {

                    // 0 (maybe), verify that all bumps received? / time sync?
                    // ...

                    // 1 execute all "bumps"
                    bumpingContexts.forEach { it.bump() }
                    bumpingContexts.clear()

                    // 2 animate all context machines, remove from the animating set if
                    // they no longer have animations
                    animatingContexts.executeAll({it.isAnimating}) {c, m->
//                        println("animating $m with context $c")
                        m.updateAnimation(c)
                        c.addRefreshing(m)
                    }

//                    println(refreshingContexts.size)
                    // 3 update any refreshing
                    refreshingContexts.executeAll({it.dirty}) { c, m->
//                        println("refreshing $m with context $c")
                        m.playbackRefresh(c)
                    }

                    // 4 render
                    renderingContexts.executeAll({it.isRendering}) {c, m->
//                        println("rendering $m with context event ${c.event}")
//                        println(c.event)
                        m.render()
                    }
//                    println("----------------------------")

                }
            }
        }
    }

    fun play(event: Event) = play { event }

    fun play() = plays?.let {p-> play { p } }

}

val DEFAULT_SCORE = Score.create("DEFAULT_SCORE")