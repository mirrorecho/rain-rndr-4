package rain.score

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import org.openrndr.Program
import org.openrndr.application
import org.openrndr.launch
import rain.graph.Label
import rain.graph.Node
import rain.graph.NodeLabel
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


    // TODO: needed?
//    private var myProgram: Program? = null

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
                    runningMachines.forEach { it.value.render() }
                }
            }
        }
    }

    fun play(event: Event) = play { event }

    fun play() = plays?.let {p-> play { p } }

}

val DEFAULT_SCORE = Score.create("DEFAULT_SCORE")