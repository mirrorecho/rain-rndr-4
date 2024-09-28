package rain.score.nodes

import org.openrndr.animatable.Animatable
import rain.graph.Node
import rain.graph.Label
import rain.graph.NodeLabel
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
        myIsRendering = onOff;
    }

    // TODO: is this even used?
    private var myIsRendering = false

    val isRendering: Boolean get() = myIsRendering

    override val dirty: Boolean
        get() = super.dirty || hasAnimations

    open val hasAnimations get() = machineAnimation.hasAnimations()

    // indicates whether this machine should cache data during playback
    open val hasPlaybackRefresh: Boolean = false

    private var activeContext: Score.ScoreContext? = null

    open fun bump(context: Score.ScoreContext) {
        // hook that can be overridden for machine-specific implementations
        activeContext = context
    }

    fun render() {
        activeContext?.let { render(it) }
    }

    open fun render(context: Score.ScoreContext) { println("render not implemented for $this") }

    // TODO: assuming activeContext only used for rendering (and not playBackRefresh... but think through this)
//    fun playbackRefresh() {
//        activeContext?.let { playbackRefresh(it) }
//    }

    open fun playbackRefresh(context: Score.ScoreContext) {
        // hook for refreshing during playback (if hasPlaybackRefresh=true)
    }

    // TODO: assuming activeContext only used for rendering (and not updateAnimation... but think through this)
//    fun updateAnimation() {
//        activeContext?.let { updateAnimation(it) }
//    }

    open fun updateAnimation(context: Score.ScoreContext) {
        //  be overriden to add additional logic (or prevent updating ALL data from slots)
        machineAnimation.updateAnimation()
    }
}

// =======================================================================

