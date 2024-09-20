package rain.score.nodes

import org.openrndr.animatable.Animatable
import rain.graph.Node
import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.ScoreContext
import rain.utils.autoKey
import kotlin.reflect.KMutableProperty0


open class MachineAnimation: Animatable() {

    fun bumpAnimation(
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
//            property.complete()
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

    // TODO is this used?
    open fun gate(onOff: Boolean) {
        isRunning = onOff;
    }

    // TODO: is this even used?
    protected var isRunning = false

    protected var scoreContext: ScoreContext? = null

    fun setContext(context: ScoreContext) {scoreContext = context}

    open fun bump(context:ScoreContext) {
        // hook that can be overridden for machine-specific implementations
    }

    fun render() {
        scoreContext?.let {
            render(it)
            return
        }
        println("WARNING - no score context for $this, unable to render")
    }

    open fun render(context: ScoreContext) { println("render not implemented for $this") }

    override val dirty: Boolean
        get() = super.dirty || hasAnimations

    open val hasAnimations get() = machineAnimation.hasAnimations()

    fun updateAnimation(): Boolean = if (hasAnimations) {
        scoreContext?.let {
            machineAnimation.updateAnimation()
            updateAnimation(it)
            return true
        }
        println("WARNING - no score context for $this, unable to update animation")
        false
    } else false

    open fun updateAnimation(context: ScoreContext) {
        // hook to be overriden to add additional logic (or prevent updating ALL data from slots)
        refresh()
    }
}

// =======================================================================

