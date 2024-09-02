package rain.score.nodes


import rain.graph.Node
import rain.graph.quieries.Pattern
import rain.score.EventPlayer
import rain.score.ExtendHelper
import org.openrndr.animatable.easing.Easing
import rain.graph.Label
import rain.graph.NodeLabel
import rain.language.patterns.*
import rain.language.patterns.relationships.BUMPS
import rain.rndr.relationships.STROKE_COLOR
import rain.utils.*

enum class Gate(val startGate: Boolean?, val endGate:Boolean?) {
    ON(true, null),
    OFF(null, false),
    ON_OFF(true, false),
    NONE(null, null);

    val hasGate = startGate!= null || endGate != null

}

// TODO: make this an interface?
open class Event protected constructor(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<Node, Event>(
        null, Event::class, { k -> Event(k) }
    )

    override val label: Label<out Node, out Event> = Event

    var dur by DataSlot<Double?>("dur", null)
    var simultaneous by DataSlot("simultaneous", false)
    var gate by DataSlot("gate", Gate.NONE)

    var bumping by DataSlot("bumping", false)

    // TODO: implement by history....
    val bumps by RelatedNodeSlot("bumps", +BUMPS, Machine, null)

    open val childrenPattern: Pattern<Event> by lazy { Pattern(this, CUED_CHILDREN_QUERY) }

    open val children get() = childrenPattern.asPatterns(Event, CUED_CHILDREN_QUERY)

    // TODO: implement if useful
//    val sumDur: Double get() { throw NotImplementedError()
//        if (isLeaf) getUp("dur")
//        else {
//            children.map { sumDur }.run {
//                if (simultaneous) { max() } else { sum() }
//            }
//    }

    // TODO: should this be a node?
    inner class AnimateEventValue(
        val name:String, // TODO: needed?
        var toValue: Double = 0.0,
        val easing: Easing = Easing.None,
        dur: Double? = null,
        offsetDur: Double = 0.0,
        val fromValue: Double? = null,
    ) {

        val event = this@Event

        val durMs: Long = ((dur ?: event.dur ?: 0.0) * 1000).toLong()
        val offsetDurMs: Long =
            if (offsetDur >= 0.0) (offsetDur * 1000).toLong()
            else (((event.dur ?: 0.0) - offsetDur) * 1000).toLong()

    }

    fun animate(name:String, block: AnimateEventValue.()->Unit) {
        putSlot("$name:animate", AnimateEventValue(name).also(block))
    }

//    fun <R:Node, RL:NodeLabel<R>>bumps(
//        receiverLabel:RL,
//        key:String=autoKey(),
//        messageBlock: (RL.(Message<R, RL>)->Unit)?=null,
//        receiverBlock: (R)->Unit
//    ) {
//        val receiver = receiverLabel.merge(key, messageBlock)
//        relate(TARGETS, receiver) // TODO: replace with BUMPS
//        receiverBlock.invoke(receiver)
//    }

    fun play() = EventPlayer(childrenPattern).play()

    fun <NT: Event>extend(label:NodeLabel<*, NT>, block: ExtendHelper<NT>.()->Unit) {
        val helper = ExtendHelper(this, label)
        block.invoke(helper)
        helper.extendEvent()
    }

}

// ------------------------------------------

fun <E: Event, L: NodeLabel<*, E>>L.par(
    key:String = autoKey(),
    properties:Map<String, Any?>?=null,
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E = this.create(key) {
    simultaneous = true
    bumping = false
    block?.invoke(this)
    childrenPattern.extend(*children)
}

fun <E: Event, L: NodeLabel<*, E>>L.par(
    properties:Map<String, Any?>?=null,
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E =
    this.par(autoKey(), properties, *children) { block?.invoke(this) }

fun <E: Event, L: NodeLabel<*, E>>L.par(
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E =
    this.par(autoKey(), null, *children) { block?.invoke(this) }

// ------------------------------------------

fun par(
    key:String = autoKey(),
    properties:Map<String, Any?>?=null,
    vararg children: Event,
    block:(Event.()->Unit)?=null,
): Event =
    Event.par(key, properties, *children) { block?.invoke(this) }

fun par(
    properties:Map<String, Any?>?=null,
    vararg children: Event,
    block:(Event.()->Unit)?=null,
): Event =
    par(autoKey(), properties, *children) { block?.invoke(this) }

fun par(
    vararg children: Event,
    block:(Event.()->Unit)?=null
): Event =
    par(autoKey(), null, *children) { block?.invoke(this) }


// ------------------------------------------
// ------------------------------------------

fun <E: Event, L: NodeLabel<*, E>>L.seq(
    key:String = autoKey(),
    properties:Map<String, Any?>?=null,
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E = this.create(key) {
    simultaneous = false
    bumping = false
    block?.invoke(this)
    childrenPattern.extend(*children)
}

fun <E: Event, L: NodeLabel<*, E>>L.seq(
    properties:Map<String, Any?>?=null,
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E =
    this.seq(autoKey(), properties, *children) { block?.invoke(this) }

fun <E: Event, L: NodeLabel<*, E>>L.seq(
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E =
    this.seq(autoKey(), null, *children) { block?.invoke(this) }

// ------------------------------------------

fun seq(
    key:String = autoKey(),
    properties:Map<String, Any?>?=null,
    vararg children: Event,
    block:(Event.()->Unit)?=null,
): Event =
    Event.seq(key, properties, *children) { block?.invoke(this) }

fun seq(
    properties:Map<String, Any?>?=null,
    vararg children: Event,
    block:(Event.()->Unit)?=null,
): Event =
    seq(autoKey(), properties, *children) { block?.invoke(this) }

fun seq(
    vararg children: Event,
    block:(Event.()->Unit)?=null
): Event =
    seq(autoKey(), null, *children) { block?.invoke(this) }


