package _bak.rndr
//
//import org.openrndr.Program
//import org.openrndr.animatable.Animatable
//import org.openrndr.animatable.easing.Easing
//import rain.language.*
//import rain._bak.patterns.Pattern
//import rain._bak.patterns.rain.score.nodes.Event
//import rain.utils.autoKey
//import kotlin.math.absoluteValue
//
//
//
//
//open class ValueAnimate(
//    key:String = autoKey(),
//): Value(key) {
//    abstract class ValueAnimateLabel<T:ValueAnimate>: ValueLabel<T>() {
//        val initValue = field<Double?>("initValue", null)
//        val easing = field("easing", Easing.None)
//        val animateDur = field<Double?>("animateDur", null)
//
//        // TODO: would this be used? Or just add to the confusion (for now, KISS)
//        //  ... could be used to animates a field on ANOTHER node somewhere
////        val animates = field<Double?>("animates", ANIMATES)
//    }
//
//    companion object : ValueAnimateLabel<ValueAnimate>() {
//        override val parent = Value
//        override val labelName:String = "ValueAnimate"
//        override fun factory(key:String) = ValueAnimate(key)
//        init { registerMe() }
//    }
//
//    override val label: Label<out ValueAnimate>  = ValueAnimate
//
//    private inner class AnimationValue(
//        var controlValue:Double = 0.0
//    ): Animatable()
//
//    // NOTE that base field "value" is used as the value to animate to
//    var initValue by attach(ValueAnimate.initValue)
//    var easing by attach(ValueAnimate.easing)
//    var animateDur by attach(ValueAnimate.animateDur)
//
//    private val animationValue = AnimationValue()
//
//
//    override fun bump(pattern: Pattern<Event>) {
//        // TODO: address this!
//
//        // needed for first time bump to start from correct default
//        animationValue.controlValue = value
//
//        updateFieldsFrom(pattern, ::value, ::initValue, ::easing, ::animateDur)
//
//        println("Animating from $initValue to: $value")
//
//        val durMs: Long = ((pattern[Event.dur] ?: 0.0) * 1000).toLong()
//        val animateDurMs: Long = ((animateDur ?: 0.0) * 1000).toLong().let {
//            if (it == (0).toLong() || it.absoluteValue > durMs) durMs else it
//        }
//        // NEED TO CALL THIS IN ORDER FOR ANIMATION TO WORK CORRECTLY
//        // TODO: WHY?????
//        animationValue.updateAnimation()
//
//        if (animateDur != null) { // TODO: is this the best way to test for animation?
//
//
//            initValue?.let { animationValue.controlValue = it }
//
//            animationValue.apply {
//                if (animateDurMs >= 0) {
//                    println("ANIMATING for $animateDurMs ms")
//                    ::controlValue.animate(value, animateDurMs, easing)
//                    ::controlValue.complete()
//                } else {
//                    // TODO, a better way to keep current value for the duration instead of "animating" it?
//                    ::controlValue.animate(controlValue, durMs + animateDurMs)
//                    ::controlValue.complete()
//                    ::controlValue.animate(value, animateDurMs.absoluteValue, easing)
//                    ::controlValue.complete()
//                }
//            }
//        } else animationValue.controlValue = value
//    }
//
//
//    override fun render(program: Program) {
//        animationValue.updateAnimation()
//        value = animationValue.controlValue
//    }
//
//
//}