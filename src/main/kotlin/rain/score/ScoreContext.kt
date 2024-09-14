package rain.score

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import org.openrndr.Program
import org.openrndr.draw.Drawer
import org.openrndr.launch
import rain.rndr.nodes.DrawStyle
import rain.score.nodes.Event
import rain.score.nodes.Machine
import kotlin.time.DurationUnit
import kotlin.time.toDuration

class ScoreContext(
    val event: Event,
    val score: Score,
    val program: Program,
    val parent: ScoreContext? = null,
    // TODO: add renderTarget
    ) {

    val machine: Machine? = event.bumps ?: parent?.machine

    val drawStyle: DrawStyle? = event.drawStyle ?: parent?.drawStyle

    val unitLength get() = score.unitLength

    fun childContext(
        event: Event,
    ): ScoreContext = ScoreContext(
        event,
        score,
        program,
        this,
    )

    suspend fun play(
        exitOnComplete:Boolean=false
    ) {


//        println("PLAYING: ${event} with machine ${context.machine}")

        machine?.let { m ->
            m.setContext(this)
            event.gate.startGate?.let { score.gateMachine(m, it) }
            if (event.bumping) m.bump()
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
        } else
            event.children.forEach { childContext(it).play() }


//        event.children.forEach { childEvent ->
//            // TODO: this cast below is gross
//            val childContext = childContext(childEvent)
//            if (event.simultaneous)
//                threads.add(program.launch { childContext.play() })
//            else
//                childContext.play()
//        }
//
//        threads.joinAll()

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
//                println(it.rndrDrawStyle)
            }
            block(this)
        }
    }

}