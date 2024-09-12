package rain.score

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import org.openrndr.Application
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.launch
import rain.graph.Label
import rain.graph.Node
import rain.graph.NodeLabel
import rain.graph.queries.Pattern
import rain.language.patterns.relationships.PLAYS
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

    fun exit() {
        println("EXITING: ==========================================================")
        myProgram?.let {
            println("Program complete after ${it.seconds} seconds")
            it.application.exit()
            //TODO: notice the time creep here!!! How to deal with this?
        }
    }

    fun applyProgram(block: Program.()->Unit) {
        myProgram?.let { block.invoke(it) }
    }

    private var myProgram: Program? = null

    // TODO maybe, consider whether the running machines are actually just part of the graph
    //  (e.g. create/remove relationships)
    private val runningMachines: MutableMap<String, Machine> = mutableMapOf()

    fun gateMachine(machine: Machine, gate: Boolean) {
//        println("gating $gate - $machine")
        machine.gate(gate)
        if (gate) {
            runningMachines[machine.key] = machine
        } else {
            runningMachines.remove(machine.key)
        }
    }

    private suspend fun playPattern(
        pattern: Pattern<Event>,
        exitOnComplete:Boolean=false
    ) {
        myProgram?.let { program ->

            val threads: MutableList<Job> = mutableListOf()
            val event = pattern.source
            val gate = event.gate
            // access the bumps machine only if necessary:
            val machine =
                if (event.bumping || gate.hasGate)
                    pattern.cascadingSlotValue<Machine>("bumps")
                else null

//        println("PLAYING: ${pattern.source} with machine $machine")

            machine?.let { m ->
                gate.startGate?.let { gateMachine(m, it) }
                if (event.bumping) m.bump(pattern)
            }

//        println("adding delay: $addDelay")
            event.dur?.let { dur -> if (dur > 0.0) delay(dur.toDuration(DurationUnit.SECONDS)) }
            // TODO: adjust for delta in time delay


            event.children(pattern).forEach { childEventPattern ->
                // TODO: this cast below is gross
                if (pattern.source.simultaneous)
                    threads.add(program.launch { playPattern(childEventPattern) })
                else
                    playPattern(childEventPattern)
            }

            threads.joinAll()

            machine?.let { m -> gate.endGate?.let { gateMachine(m, it) } }

            if (exitOnComplete) exit()
        }

    }

    fun play(block: Score.(Program)->Event) {
        application {
            configure {
                width = this@Score.width.toInt()
                height = this@Score.height.toInt()
            }
            program {
                myProgram = this
                launch {
                    playPattern(block.invoke(this@Score, this@program).childrenPattern(), true)
                }
                extend {
                    // TODO: consider ... machines are executed in no particular order, is that OK?
                    runningMachines.forEach { it.value.render(this@Score) }
                }
            }
        }
    }

    fun play(event: Event) = play { event }

    fun play() = plays?.let {p-> play { p } }

}

val DEFAULT_SCORE = Score.create("DEFAULT_SCORE")