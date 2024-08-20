package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.animatable.easing.Easing
import rain.language.*
import rain.language.fields.field
import rain.language.fields.fieldOfNode
import rain.language.patterns.Pattern
import rain.language.patterns.nodes.Event
import rain.utils.autoKey
import kotlin.math.absoluteValue




open class ValueAnimate(
    key:String = autoKey(),
): Value(key) {
    abstract class ValueAnimateLabel<T:ValueAnimate>: ValueLabel<T>() {
        val initValue = field<Double?>("value", null)
        val easing = field("easing", Easing.None)
        val animateDur = field<Double?>("animateDur", null)

        // TODO: would this be used? Or just add to the confusion (for now, KISS)
        //  ... could be used to animates a field on ANOTHER node somewhere
//        val animates = field<Double?>("animates", ANIMATES)
    }

    companion object : ValueAnimateLabel<ValueAnimate>() {
        override val parent = Value
        override val labelName:String = "ValueAnimate"
        override fun factory(key:String) = ValueAnimate(key)
    }

    override val label: NodeLabel<out ValueAnimate>  = ValueAnimate

    private class AnimationValue(
        var value:Double = 0.0
    ): Animatable()

    val initValue by attach(ValueAnimate.initValue)
    val easing by attach(ValueAnimate.easing)
    val animateDur by attach(ValueAnimate.animateDur)

    private val animationValue = AnimationValue(this.value)


    override fun bump(pattern: Pattern<Event>) {
        val durMs: Long = ((pattern[Event.dur] ?: 0.0) * 1000).toLong()
        val animateDurMs: Long = ((animateDur ?: 0.0) * 1000).toLong().let {
            if (it == (0).toLong() || it.absoluteValue > durMs) durMs else it
        }
        // NEED TO CALL THIS IN ORDER FOR ANIMATION TO WORK CORRECTLY IF NOT GATED????
        if (!isRunning) animationValue.updateAnimation()

        if (animateDur != null) { // TODO: is this the best way to test for animation?

            initValue?.let { animationValue.value = it }

            animationValue.apply {
                if (animateDurMs >= 0) {
                    ::value.animate(pattern[ValueAnimate.value], animateDurMs, easing)
                    ::value.complete()
                } else {
                    // TODO, a better way to keep current value for the duration instead of "animating" it?
                    ::value.animate(value, durMs + animateDurMs)
                    ::value.complete()
                    ::value.animate(pattern[ValueAnimate.value], animateDurMs.absoluteValue, easing)
                    ::value.complete()
                }
            }
        } else animationValue.value = pattern[ValueAnimate.value]
    }


    override fun render(program: Program) {
        animationValue.updateAnimation()
        this.value = animationValue.value
    }

}