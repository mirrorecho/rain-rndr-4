package rain.language.patterns.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import rain.language.Node
import rain.language.Label
import rain.language.NodeLabel
import rain.language.patterns.Pattern
import rain.utils.autoKey
import kotlin.math.absoluteValue
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty
import kotlin.reflect.KProperty0


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

            initValue?.let { property.set(it) }

            if (durMs > 0 || offsetDurMs > 0) {
                if (offsetDurMs > 0) {
                    // TODO, a better way to keep current value for the duration instead of "animating" it?
                    property.animate(property.get(), offsetDurMs)
                    property.complete()
                }
                property.animate(target, durMs, easing)
                property.complete()
            } else {
                property.set(target)
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


    // TODO is this used?
    open fun gate(onOff: Boolean) {
        isRunning = onOff;
    }

    open fun render(program: Program) { println("render not implemented for $this") }

    // TODO: is this even used?
    protected var isRunning = false

    // TODO: make this universal?
    open fun bump(pattern:Pattern<Event>) {
        val ae = pattern.source.AnimateEventValue("yo", 2.0)
        // TODO: implement?
        println("Bumping $key: $properties - WARNING: no bump defined")
    }

}

// =======================================================================

