package rain.score.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import rain.graph.Node
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.quieries.Pattern
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

interface Positionable {
    var x: Double
    var y: Double

    fun vector(program: Program): Vector2 = Vector2(
        x * program.width,
        y * program.height,
    )

}


abstract class MachineAnimation: Animatable() {

    fun updateAnimation(
        property: KMutableProperty0<Double>,
        animateEventValue: Event.AnimateEventValue,
        // preDelay: Long, // TODO: implement this?
    ) {

        // NEED TO CALL THIS IN ORDER FOR ANIMATION TO WORK CORRECTLY
        // TODO: WHY?????
        updateAnimation()

        animateEventValue.apply {

            fromValue?.let { property.set(it) }

            if (durMs > 0 || offsetDurMs > 0) {
                if (offsetDurMs > 0) {
                    // TODO, a better way to keep current value for the duration instead of "animating" it?
                    property.animate(property.get(), offsetDurMs)
                    property.complete()
                }
                property.animate(toValue, durMs, easing)
                property.complete()
            } else {
                property.set(toValue)
            }
        }

    }

}

open class Machine protected constructor(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<Node, Machine>(
        null, Machine::class, { k-> Machine(k) }
    )
    override val label: Label<out Node, out Machine> = Machine


    override fun <T:Any?>updateSlotFrom(name:String, fromSlot: DataSlot<T>) {
        dataSlot<T>(name)?.let { slot->
            if (fromSlot.node.slotNames.contains("$name:animate")) {
                val animateEventValue = fromSlot.node.dataSlot<Event.AnimateEventValue>("$name:animate"))
            } else
                slot.value = fromSlot.value
        }
    }

    // TODO is this used?
    open fun gate(onOff: Boolean) {
        isRunning = onOff;
    }

    open fun render(program: Program) { println("render not implemented for $this") }

    // TODO: is this even used?
    protected var isRunning = false

    // TODO: make this universal?
    open fun bump(pattern: Pattern<Event>) {
        // TODO: implement?
        println("Bumping $key: $properties - WARNING: no bump defined")
    }

}

// =======================================================================

