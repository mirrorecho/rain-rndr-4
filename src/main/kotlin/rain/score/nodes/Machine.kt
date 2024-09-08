package rain.score.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import rain.graph.Node
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.queries.Pattern
import rain.score.Score
import rain.utils.autoKey
import kotlin.reflect.KMutableProperty0


interface Colorable {
    var h: Double
    var s: Double
    var v: Double
    var a: Double

    fun colorHSVa() = ColorHSVa(h, s, v, a)

    fun colorRGBa(): ColorRGBa = colorHSVa().toRGBa()

}

open class MachineAnimation: Animatable() {

    fun updateAnimation(
        property: KMutableProperty0<Double>,
        animateEventValue: Event.AnimateEventValue,
        // preDelay: Long, // TODO: implement this?
    ) {

        // NEED TO CALL THIS IN ORDER FOR ANIMATION TO WORK CORRECTLY
        // TODO: WHY?????
        updateAnimation()

        animateEventValue.apply {

            // TODO/NOTE: fromValue does not work on the SECOND animation unless there is a pause before the first
            //  ... why?
            fromValue?.let { property.set(it) }

//            println("ANIMATING: ${animateEventValue.name} over $durMs MS, with $offsetDurMs MS offset")

            if (durMs > 0 || offsetDurMs > 0) {
                if (offsetDurMs > 0) {
                    // TODO, a better way to keep current value for the duration instead of "animating" it?
                    property.animate(property.get(), offsetDurMs)
                    property.complete()
                }
                property.animate(value, durMs, easing)
                property.complete()
            } else {
                property.set(value)
            }
        }

    }

}

open class Machine protected constructor(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<Node, Machine>(
        Node, Machine::class, { k-> Machine(k) }
    )
    override val label: Label<out Node, out Machine> = Machine

    open val machineAnimation = MachineAnimation()

    override fun <T:Any?>updateSlotFrom(name:String, fromSlot: DataSlot<T>) {
        slot<T>(name)?.let { slot->
            if (fromSlot.node.slotNames.contains("$name:animate")) {
                slot.property?.let { property ->
                    machineAnimation.updateAnimation(
                        property as KMutableProperty0<Double>,
                        fromSlot.node.slot<Event.AnimateEventValue>("$name:animate")!!.value
                    )

                }
            } else
                slot.value = fromSlot.value
        }
    }

    // TODO is this used?
    open fun gate(onOff: Boolean) {
        isRunning = onOff;
    }

    open fun render(score: Score) { println("render not implemented for $this") }

    // TODO: is this even used?
    protected var isRunning = false

    // TODO: make this universal?
    open fun bump(pattern: Pattern<Event>) {
        // TODO: implement?
        println("Bumping $key: $properties - WARNING: no bump defined")
    }

}

// =======================================================================

