package rain.score

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import org.openrndr.Program
import org.openrndr.draw.Drawer
import org.openrndr.launch
import rain.graph.Node
import rain.rndr.nodes.DrawStyle
import rain.score.nodes.Event
import rain.score.nodes.Machine
import kotlin.reflect.KMutableProperty0
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


//        override fun <T:Any?>updateSlotFrom(name:String, fromSlot: Node.DataSlot<T>) {
//            slot<T>(name)?.let { slot->
//                if (fromSlot.node.slotNames.contains("$name:animate")) {
//                    slot.property?.let { property ->
//                        machineAnimation.updateAnimation(
//                            property as KMutableProperty0<Double>,
//                            fromSlot.node.slot<Event.AnimateEventValue>("$name:animate")!!.value
//                        )
//
//                    }
//                } else
//                    slot.value = fromSlot.value
//            }
//        }

//        private fun animateMachine(
//            machine:Machine?,
//            names:Array<out String>,
//            block: Event.AnimateEventValue.()->Unit
//        ) {
//            var myMachine: Machine? = machine
//            names.apply {
//                take(size-1).forEach {relatedName->
//                    println("taking")
//                    myMachine = myMachine?.slot<Machine?>(relatedName)?.value
//                }
//                last().let {name->
//                    println("Animating machine '$myMachine' slot '$name'")
//                    myMachine?.slot("$name:animate", AnimateEventValue(name).also(block))
//                }
//            }
//        }

//        fun animate(vararg names:String, block: Event.AnimateEventValue.()->Unit) =
//            animateMachine(bumps, names, block)
//
//        fun animateStyle(vararg names:String, block: Event.AnimateEventValue.()->Unit) =
//            animateMachine(drawStyle, names, block)


        fun updateMachines(
            startingMachine: Machine,
            eventSlots: Map<String, Node.DataSlot<*>>,
            prefix: String,
        ) {
            // update machine slot values based on slot event values,
            // following the path
            // animating where animation defined in the slot name/value
            eventSlots.forEach { (fullName, slot) ->

                var myMachine: Machine? = startingMachine

                fullName.substringAfter(prefix).split(".").apply {
                    take(size-1).forEach {relatedName->
                        myMachine = myMachine?.slot<Machine?>(relatedName)?.value
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
        }

        machine?.let { m ->
            m.setContext(this)
            event.gate.startGate?.let { score.gateMachine(m, it) }
            if (event.bumping) {
                updateMachines(m, event.machineSlots, "machine.")
                m.bump(this)
            }
        }

        event.styleSlots.let {ss ->
            if (ss.isNotEmpty()) {
                drawStyle?.let {ds->
                    updateMachines(ds, ss, "style.")
                    ds.updateRndrDrawStyle()
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