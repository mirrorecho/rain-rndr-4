package rain.language.patterns.nodes


import language.patterns.ExtendHelper
import rain.language.*
import rain.language.fields.field
import rain.language.fields.fieldOfNode
import rain.language.patterns.*
import rain.language.patterns.relationships.*
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

    abstract class EventLabel<T:Event>: Label<T>() {
        val dur = field("dur", 0.0, false)
        val simultaneous = field("simultaneous", false, false)
        val gate = field("gate", Gate.NONE, false)
        val bumps = fieldOfNode("bumps", BUMPS, Machine, null)
        val bumping = field("bumping", true, false)
    }

    companion object : EventLabel<Event>() {
        override val labelName:String = "Event"
        override fun factory(key:String) = Event(key)
        init { registerMe() }
    }

    override val label: Label<out Event> = Event

    var dur by attach(Event.dur)
    var simultaneous by attach(Event.simultaneous)
    var gate by attach(Event.gate)
    var bumps by attach(Event.bumps)
    var bumping by attach(Event.bumping)

    // TODO: is this by lazy effective enough for "caching"?
    val treePattern: CuedChildrenPattern<Event> by lazy { CuedChildrenPattern(this) }

    // TODO maybe: implement caching? Done?
    // or TODO: even better, consider children a field on Event? (would need a new type of field)
    open val children get() = treePattern.asPatterns { s,p -> CuedChildrenPattern(s as Event, p) }

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

    fun play() = EventPlayer(treePattern).play()

    fun <NT:Event, LT:EventLabel<NT>>extend(label:LT, block:ExtendHelper<NT, LT>.()->Unit) {
        val helper = ExtendHelper(this, label)
        block.invoke(helper)
        helper.extendEvent()
    }

}

// ------------------------------------------

fun <E:Event, L:Event.EventLabel<E>>L.par(
    key:String = autoKey(),
    properties:Map<String, Any?>?=null,
    vararg children:Event,
    block:(E.()->Unit)?=null,
):E = this.create(key, properties) {
    simultaneous = true
    bumping = false
    block?.invoke(this)
    treePattern.extend(*children)
}

fun <E:Event, L:Event.EventLabel<E>>L.par(
    properties:Map<String, Any?>?=null,
    vararg children:Event,
    block:(E.()->Unit)?=null,
):E =
    this.par(autoKey(), properties, *children) { block?.invoke(this) }

fun <E:Event, L:Event.EventLabel<E>>L.par(
    vararg children:Event,
    block:(E.()->Unit)?=null,
):E =
    this.par(autoKey(), null, *children) { block?.invoke(this) }

// ------------------------------------------

fun par(
    key:String = autoKey(),
    properties:Map<String, Any?>?=null,
    vararg children:Event,
    block:(Event.()->Unit)?=null,
):Event =
    Event.par(key, properties, *children) { block?.invoke(this) }

fun par(
    properties:Map<String, Any?>?=null,
    vararg children:Event,
    block:(Event.()->Unit)?=null,
):Event =
    par(autoKey(), properties, *children) { block?.invoke(this) }

fun par(
    vararg children:Event,
    block:(Event.()->Unit)?=null
):Event =
    par(autoKey(), null, *children) { block?.invoke(this) }


// ------------------------------------------
// ------------------------------------------

fun <E:Event, L:Event.EventLabel<E>>L.seq(
    key:String = autoKey(),
    properties:Map<String, Any?>?=null,
    vararg children:Event,
    block:(E.()->Unit)?=null,
):E = this.create(key, properties) {
    simultaneous = false
    bumping = false
    block?.invoke(this)
    treePattern.extend(*children)
}

fun <E:Event, L:Event.EventLabel<E>>L.seq(
    properties:Map<String, Any?>?=null,
    vararg children:Event,
    block:(E.()->Unit)?=null,
):E =
    this.seq(autoKey(), properties, *children) { block?.invoke(this) }

fun <E:Event, L:Event.EventLabel<E>>L.seq(
    vararg children:Event,
    block:(E.()->Unit)?=null,
):E =
    this.seq(autoKey(), null, *children) { block?.invoke(this) }

// ------------------------------------------

fun seq(
    key:String = autoKey(),
    properties:Map<String, Any?>?=null,
    vararg children:Event,
    block:(Event.()->Unit)?=null,
):Event =
    Event.seq(key, properties, *children) { block?.invoke(this) }

fun seq(
    properties:Map<String, Any?>?=null,
    vararg children:Event,
    block:(Event.()->Unit)?=null,
):Event =
    seq(autoKey(), properties, *children) { block?.invoke(this) }

fun seq(
    vararg children:Event,
    block:(Event.()->Unit)?=null
):Event =
    seq(autoKey(), null, *children) { block?.invoke(this) }



// TODO: bring back the below?
//    val triggers = cachedTarget(TRIGGERS, machine!!)
//
//    fun makeTrigger(machine: Machine?=null, makeAutoTargets:Boolean=true): Event {
//        (machine ?: this.machine?.create {
//            if (makeAutoTargets) autoTarget()
//        } )?.let {
//            this.relate(TRIGGERS, it)
//        }
//        return this
//    }
//    fun makeTrigger(machineKey: String): Event {
//        this.relate(TRIGGERS, machineKey)
//        return this
//    }


//    override val thisTyped = this

    // TODO... no, maybe this should just point to a node on the graph right away!
//    var machine:NodeLabel<out RndrMachine>? get() = getUp("machine")
//        set(value) { this["machine"]=value }

//    var machinePath: List<RelationshipLabel>? get() = getUp("machinePath")
//        set(value) { this["machinePath"]=value }

//    var machineKey: String? get() = getUp("machineKey")
//        set(value) { this["machineKey"]=value }

//    var gate: Boolean? get() = getUp("gate")
//        set(value) { this["gate"]=value }
//
//    val sumDur: Double get() =
//        if (isLeaf) getUp("dur")
//        else {
//            children.map { sumDur }.run {
//                if (simultaneous) { max() } else { sum() }
//            }
//        }



    // TODO: use something like this?
//    operator fun invoke(vararg patterns: Pattern): CellTree {
//        this.extend(*patterns)
//        return this
//    }

    // TODO: implement cell building
//    fun <BT: CellBuilder> build(callback: BT.() -> Unit): Cell {
//        val cb = CellBuilder(this)
//        cb.apply(callback)
//        return this
//    }

//}

// SHORTCUT HELPERS:

//fun <MT : ManagerInterface> event(
//    key:String,
//    receiver:MT,
//    label: NodeLabel<out Event> = Event,
//    block: (MT.() -> Unit)? = null,
//) = label.sends(key, receiver, block)
//
//
//fun <MT : ManagerInterface> event(
//    receiver:MT,
//    label: NodeLabel<out Event> = Event,
//    block: (MT.() -> Unit)? = null,
//) = event(autoKey(), receiver, label, block)
//
//
//fun event(
//    key:String = autoKey(),
//    label: NodeLabel<out Event> = Event,
//    block: (Machine.ReceivingManager.() -> Unit)? = null,
//) = event(key, Machine.receives, Event, block)
//
//fun event(
//    label: NodeLabel<out Event> = Event,
//    block: (Machine.ReceivingManager.() -> Unit)? = null,
//) = event(autoKey(), Machine.receives, Event, block)
//
//// just for testing purposes
//class SubEvent(
//    key:String = autoKey(),
//): Event(key) {
//    companion object : NodeLabel<SubEvent>(SubEvent::class, Event, { k -> SubEvent(k) })
//    override val label: NodeLabel<out SubEvent> = SubEvent
//
//}

//fun yo() {
//    Event.yoMama()
//}

// TODO: review and remove when appropriate



//// because it's used so often AND cascade doesn't make sense
//val TreeLineage<Event>.simultaneous get() = tree.simultaneous

// TODO: figure out if there is some way to cache this without needing to keep querying
//val TreeLineage<Event>.triggersMachine: Machine? get() = getAs<NodeLabel<out Machine>?>("machine") ?.let { tree[TRIGGERS()](it).firstOrNull() } ?: parent?.triggersMachine

//val TreeLineage<Event>.triggersPathMachine: Machine? get() = this.triggersMachine?.let {machine->
//    getAs<List<RelationshipLabel>?>("machinePath")?.let { rel->
//        getAs<NodeLabel<out Machine>?>("machinePathType")?.let {mpt ->
//            return machine.get( *(rel.map { it() }.toTypedArray()) )(mpt).first()
//        }
//    }
//    return machine
//}

//fun TreeLineage<Event>.trigger(): Machine? =
//    if (this.tree.isTrigger) this.triggersPathMachine?.also { machine ->
//        machine.trigger(properties)
//    } else null
//
//
//
//// TODO: put machinePath into properties
//fun <M: Machine>Event.Companion.triggering(key:String?=null, vararg machinePath: RelationshipLabel,  block:(PatternManager<Event, M>.()->Unit)?=null): Event =
//    Event.create(key ?: autoKey()).also { e->
//        Event.patternManager<M>(e.properties, e.getPattern()).let {
//            block?.invoke(it)
//        }
//        // TODO... any way to avoid this save?
//        e.save()
//    }

//fun <M: Machine>Event.Companion.triggering(vararg machinePath: RelationshipLabel,  block:(PatternManager<Event, M>.()->Unit)?=null): Event =
//    Event.triggering(null, *machinePath, block=block)

//var PatternManager<Event, *>.machineLabel:NodeLabel<out Machine>? get() = properties["machineLabel"] as NodeLabel<out Machine>?
//    set(value) {properties["machineLabel"]=value}
//
//// TODO: needed? (just could check the relationship instead)
//var PatternManager<Event, *>.isTrigger:Boolean? get() = properties["isTrigger"] as Boolean?
//    set(value) {properties["isTrigger"]=value}
//
//var PatternManager<Event, *>.simultaneous:Boolean? get() = properties["simultaneous"] as Boolean?
//    set(value) {properties["simultaneous"]=value}
//
//var PatternManager<Event, *>.dur:Double? get() = properties["dur"] as Double?
//    set(value) {properties["dur"]=value}
//
//var PatternManager<Event, Color>.h:Double? get() = properties["h"] as Double?
//    set(value) {properties["h"]=value}