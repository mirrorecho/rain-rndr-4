package rain.score.nodes


import rain.graph.Node
import rain.score.ExtendHelper
import org.openrndr.animatable.easing.Easing
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.queries.UpdatingQueryExecution
import rain.language.patterns.*
import rain.language.patterns.relationships.BUMPS
import rain.language.patterns.relationships.DRAW_STYLE
import rain.rndr.nodes.DrawStyle
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
        Node, Event::class, { k -> Event(k) }
    )

    override val label: Label<out Node, out Event> = Event

    var dur by DataSlot<Double?>("dur", null)
    var simultaneous by DataSlot("simultaneous", false)

    // gate for rendering
    var gate by DataSlot("gate", Gate.NONE)

    // gate for custom animations
    var gateAnimate by DataSlot("gate", Gate.NONE)
00.
    var bumping by DataSlot("bumping", true)

    // TODO: implement by history....
    var machine by RelatedNodeSlot("bumps", +BUMPS, Machine, null)

    var drawStyle by RelatedNodeSlot("drawStyle", +DRAW_STYLE, DrawStyle, null)

    open val childrenQuery get() = UpdatingQueryExecution(this, CUED_CHILDREN_QUERY)

    open val children get() = childrenQuery(Event)

    fun style(key:String? = null,  block:(DrawStyle.()->Unit)?=null) =
        mergeRelated("drawStyle", key, block)

    val machineSlots: Map<String, DataSlot<*>> get() =
        slotsFilteredByName { it.startsWith("machine.") }

    val styleSlots: Map<String, DataSlot<*>> get() =
        slotsFilteredByName { it.startsWith("style.") }

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
        var value: Double = 0.0,
        var easing: Easing = Easing.None,
        var dur: Double? = null,
        var offsetDur: Double = 0.0,
        var fromValue: Double? = null,
    ) {

        val event = this@Event

        private val calculatedOffsetDur get() =
            if (this.offsetDur >= 0.0) this.offsetDur
            else (event.dur ?: 0.0) + this.offsetDur

        val offsetDurMs: Long get() = (calculatedOffsetDur * 1000).toLong()

        val durMs: Long get() =
            ((this.dur ?: event.dur?.let { it - calculatedOffsetDur } ?: 0.0) * 1000).toLong()

    }

    fun animate(name:String, block: AnimateEventValue.()->Unit) {
        slot("$name:animate", AnimateEventValue(name).also(block))
    }

    fun play(score: Score = DEFAULT_SCORE) = score.play { this@Event }

    fun <NT: Event>extend(label:NodeLabel<*, NT>, block: ExtendHelper<NT>.()->Unit) {
        val helper = ExtendHelper(this, label)
        block.invoke(helper)
        helper.extendEvent()
    }

    fun extend(vararg children: Event) {
        childrenQuery.extend(*children)
    }

    fun extend(block: ExtendHelper<Event>.()->Unit) {
        val helper = ExtendHelper(this, Event)
        block.invoke(helper)
        helper.extendEvent()
    }

}

// ----------------------------------------------------------------------------------------------
// ----------------------------------------------------------------------------------------------

fun <E: Event, L: NodeLabel<*, E>>L.par(
    key:String = autoKey(),
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E = this.create(key) {
    simultaneous = true
    bumping = false
    block?.invoke(this)
    childrenQuery.extend(*children)
}

fun <E: Event, L: NodeLabel<*, E>>L.par(
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E =
    this.par(autoKey(), *children) { block?.invoke(this) }

// ------------------------------------------

fun par(
    key:String = autoKey(),
    vararg children: Event,
    block:(Event.()->Unit)?=null,
): Event =
    Event.par(key, *children) { block?.invoke(this) }

fun par(
    vararg children: Event,
    block:(Event.()->Unit)?=null
): Event =
    par(autoKey(),  *children) { block?.invoke(this) }


// ------------------------------------------
// ------------------------------------------

fun <E: Event, L: NodeLabel<*, E>>L.seq(
    key:String = autoKey(),
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E = this.create(key) {
    simultaneous = false
    bumping = false
    block?.invoke(this)
    childrenQuery.extend(*children)
}

fun <E: Event, L: NodeLabel<*, E>>L.seq(
    vararg children: Event,
    block:(E.()->Unit)?=null,
):E =
    this.seq(autoKey(), *children) { block?.invoke(this) }

// ------------------------------------------

fun seq(
    key:String = autoKey(),
    vararg children: Event,
    block:(Event.()->Unit)?=null,
): Event =
    Event.seq(key, *children) { block?.invoke(this) }

fun seq(
    vararg children: Event,
    block:(Event.()->Unit)?=null
): Event =
    seq(autoKey(), *children) { block?.invoke(this) }

fun pause(dur:Double=1.0) = Event.create { this.dur=dur }

fun gate(machine:Machine?, gate:Gate=Gate.ON): Event = Event.create {
    this.machine = machine
    bumping = false
    this.gate =gate
}

fun gate(key:String, gate:Gate=Gate.ON) = gate(Machine[key])