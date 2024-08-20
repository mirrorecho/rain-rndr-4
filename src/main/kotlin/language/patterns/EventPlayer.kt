package rain.language.patterns

import kotlinx.coroutines.*
import kotlin.time.DurationUnit
import kotlin.time.toDuration

import org.openrndr.Program
import org.openrndr.application
import org.openrndr.launch
import rain.language.patterns.nodes.*


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

    private suspend fun playPattern(pattern: Pattern<Event>, program: Program) {
        val threads: MutableList<Job> = mutableListOf()
        val event = pattern.source
        val gate = event.gate
        // access the bumps machine only if necessary:
        val machine = if (event.bumping || gate.hasGate) pattern[Event.bumps] else null

        machine?.let { m ->
            gate.startGate?.let { gateMachine(m, it) }
            if (event.bumping) m.bump(pattern)
        }

//        println("adding delay: $addDelay")
        event.dur.let { dur -> if (dur > 0.0) delay(dur.toDuration(DurationUnit.SECONDS))  }

        event.children.forEach { childPattern ->
            if (childPattern.source.simultaneous)
                threads.add(program.launch { playPattern(childPattern, program) })
            else
                playPattern(childPattern, program)
        }

        threads.joinAll()

        machine?.let { m -> gate.endGate?.let { gateMachine(m, it) } }

    }

    fun play(): EventPlayer {
        application {
            program {
                launch { playPattern(rootPattern, this@program) }

                extend {
                    // TODO: consider ... machines are executed in no particular order, is that OK?
                    runningMachines.forEach { it.value.render(this@program) }
                }
            }
        }
        return this
    }
}