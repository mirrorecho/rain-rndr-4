package rain.score

import kotlinx.coroutines.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.launch
import rain.graph.quieries.Pattern
import rain.score.nodes.Event
import rain.score.nodes.Machine


open class EventPlayer(
    val rootPattern: Pattern<Event>
) {
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
        program: Program,
        exitOnComplete:Boolean=false
    ) {
        val threads: MutableList<Job> = mutableListOf()
        val event = pattern.source
        val gate = event.gate
        // access the bumps machine only if necessary:
        val machine =
            if (event.bumping || gate.hasGate)
                pattern.cascadingDataSlot<Machine>("bumps")?.value
            else null

        println("PLAYING: ${pattern.source} with machine $machine")

        machine?.let { m ->
            gate.startGate?.let { gateMachine(m, it) }
            if (event.bumping) m.bump(pattern)
        }

//        println("adding delay: $addDelay")
        event.dur?.let { dur -> if (dur > 0.0) delay(dur.toDuration(DurationUnit.SECONDS))  }
        // TODO: adjust for delta in time delay


        event.children.forEach { childEventPattern ->
            // TODO: this cast below is gross
            if (pattern.source.simultaneous)
                threads.add(program.launch { playPattern(childEventPattern, program) })
            else
                playPattern(childEventPattern, program)
        }

        threads.joinAll()

        machine?.let { m -> gate.endGate?.let { gateMachine(m, it) } }
        if (exitOnComplete) {
            println("==========================================================")
            println("Program complete after ${program.seconds} seconds")
            program.application.exit()
            //TODO: notice the time creep here!!! How to deal with this?
        }

    }

    fun play(): EventPlayer {
        application {
            program {
                println("==========================================================")
                println("Playing ${rootPattern.source}")
                println()
                launch { playPattern(rootPattern, this@program, true) }
                extend {
                    // TODO: consider ... machines are executed in no particular order, is that OK?
                    runningMachines.forEach { it.value.render(this@program) }
                }
            }
        }
        return this
    }
}