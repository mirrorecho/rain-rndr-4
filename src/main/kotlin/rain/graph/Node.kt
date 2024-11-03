package rain.graph


import org.openrndr.animatable.Animatable
import rain.graph.queries.Queryable
import rain.graph.queries.RelatedQuery
import rain.graph.queries.UpdatingQueryExecution
import rain.language.patterns.relationships.DIRTIES

import rain.utils.autoKey
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

// ===========================================================================================================

typealias RelationshipRegistry =  MutableMap<String, MutableSet<Relationship>>

// TODO: consider making Node an interface (while still implementing label registry)
//  and so NOT inheriting from Animatable (and then Machine would inherit from Animatable)
open class Node protected constructor(
    override val key:String,
): Item, Queryable<Node>, Animatable() {
    companion object : NodeLabel<Node, Node>(
        null, Node::class, { k-> Node(k) }
    )

    override val label: Label<*, out Node> = Node

    override fun toString():String = "${label.labelName}($key)"

    private val sourcesForRelationships: RelationshipRegistry = mutableMapOf()
    private val targetsForRelationships: RelationshipRegistry = mutableMapOf()

    // TODO maybe: consider allowing null direction to get labels on both directions
    private fun getRelationshipRegistry(directionIsRight: Boolean): RelationshipRegistry =
        if (directionIsRight) sourcesForRelationships else targetsForRelationships

    fun registerRelationship(relationship: Relationship, directionIsRight: Boolean) {
        getRelationshipRegistry(directionIsRight).getOrPut(relationship.label.labelName) { mutableSetOf() }.add(relationship)
    }

    fun unregisterRelationship(relationship: Relationship, directionIsRight: Boolean) {
        getRelationshipRegistry(directionIsRight)[relationship.label.labelName]?.remove(relationship)
    }

    fun getRelationships(relationshipLabel: String, directionIsRight: Boolean): Set<Relationship> =
        getRelationshipRegistry(directionIsRight)[relationshipLabel].orEmpty()

    override fun asSequence(): Sequence<Node> = sequenceOf(this)

    fun clean() {
        refresh()
        dataSlots.values.forEach { it.clean() }
    }

    open fun refresh() {
        // for nodes with caching, hook for executing node-type specific caching logic
        // after slot data is updated (outside of playback)
    }

    override fun delete() {
        label.unregisterFromLabel(this.key)
        sourcesForRelationships.values.forEach { rs-> rs.forEach {
            it.delete()
        } }
        targetsForRelationships.values.forEach { rs-> rs.forEach {
            it.delete()
        } }
    }

//    // TODO maybe: bring back if needed (related notes outside of slots)
//    fun <T: Node> related(relatedQuery: RelatedQuery, label: NodeLabel<*, T>): RelatedNode<T> =
//        RelatedNode(this, relatedQuery, label)

    fun relate(
        label: RelationshipLabel,
        targetKey:String,
        directionIsRight: Boolean = true,
        key:String = autoKey()
    ): Relationship? = Node[targetKey]?.let {
            relate(label, it, directionIsRight, key)
        }

    fun relate(
        label: RelationshipLabel,
        target: Node,
        directionIsRight: Boolean = true,
        key:String = autoKey()
    ): Relationship =
        if (directionIsRight)
            label.create(this, target, key)
        else
            label.create(target, this, key)

    // =================================================================================
    // =================================================================================

    open val dirty: Boolean
        get() = dataSlots.values.any { it.dirty }

    private val dataSlots = mutableMapOf<String, DataSlot<*>>()

    fun slotsFilteredByName(predicate:(String)->Boolean) = dataSlots.filterKeys(predicate)

    val slotNames: Set<String> get() = dataSlots.keys

    operator fun <T:Any?> get(name:String): T? = slot<T>(name)?.value

    operator fun <T:Any?> set(name:String, value:T) {
        slot(name, value)
    }

    // updates or creates slot with the given value, and returns the slot
    open fun  <T:Any?> slot(name: String, value: T): DataSlot<T> {
        dataSlots[name]?.let { existingSlot ->
            return (existingSlot as DataSlot<T>).also { it.value = value }
        }
        return DataSlot(name, value)
    }

    // gets slot by name (if it exists, otherwise null)
    open fun <T:Any?> slot(name:String): DataSlot<T>? =
        dataSlots[name] as DataSlot<T>?

    // TODO: would these be used?
//    fun slotsFromProperties(vararg properties:KMutableProperty0<Double>) {
//        properties.forEach { PropertySlot(it) }
//    }

//    fun slotsToMap(): Map<String, Any?> {
//        return dataSlots.mapValues { (_, s)-> s.value }
//    }
//
//    fun updateSlotsFrom(map: Map<String, Any?>) {
//        map.forEach { (n, v) -> dataSlots[n]?.updateLocalValue(v) }
//    }

    private fun registerSlot(dataSlot: DataSlot<*>) {
        dataSlots[dataSlot.name] = dataSlot
    }

//    fun wireUpSlots() {
//        dataSlots.values.forEach{ it.wireUp() }
//    }

    open fun <T:Any?>updateSlotFrom(name:String, fromSlot: DataSlot<T>) {
        slot<T>(name)?.let { slot->
//            println("setting slot value to ${fromSlot.value}")
            slot.value = fromSlot.value
        }
    }

    fun <T:Any?>updateSlotFrom(name:String, node:Node) {
        node.slot<T>(name)?.let { fromSlot->
            updateSlotFrom(name, fromSlot)
        }
    }

    // TODO maybe: implement this?
//    fun <T:Any?>updateSlotFrom(name:String, pattern:Pattern<*>)  {
//    }

    fun updateAllSlotsFrom(node:Node) {
        node.dataSlots.values.forEach { fromSlot->
            updateSlotFrom(fromSlot.name, fromSlot)
        }
    }

    fun <T:Node>mergeRelated(slotName: String, key:String? = null,  block:(T.()->Unit)?=null) =
        (slot<T?>(slotName) as RelatedNodeSlot<T?>).merge(key, block)

    // responds with a double value based on a slot name and source value...
    // used for querying values across relationships;
    // update value to add custom logic
    open var respondBlock: (name:String, sourceValue:Double)->Double = { n, _->
        this.slot<Double>(n)!!.value
    }

    // ==================================================

    open inner class DataSlot<T:Any?>(
        val name:String, // TODO: needed?
        default: T
    ) {

        val node get() = this@Node

        protected open var localValue: T = default

        open val property: KMutableProperty0<T>? = null // included here for interoperability

        protected var iAmDirty = false

        open val dirty = iAmDirty // included here for interoperability

        fun clean() {
            iAmDirty = false
        }

        // same idea as setting the value, but only updates local value
        // (not relationships for related node slots), and does type casting
        fun updateLocalValue(fromAny: Any?) {
            localValue = fromAny as T
            iAmDirty = true
        }

        open var value: T
            get() = localValue
            set(value) { localValue = value; iAmDirty = true }

//        open fun wireUp() { } // included here for interoperability

        operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
            value

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {
            this.value = value
        }

        private fun registerMe() { node.registerSlot(this) }

        init { registerMe() }

    }

    // ==================================================

    open inner class RelatedNodeSlot<T:Node?>(
        name:String, // TODO: needed?
        val query: RelatedQuery,
        val label: NodeLabel<*, T & Any>,
        default: T,
        val dirties: Boolean = false, // TODO: consider whether this should default to true
        // true if the related node becoming dirty causes this slot (and slot's Node) to become dirty
    ): DataSlot<T>(name, default) {

        override val dirty
            get() = iAmDirty || (dirties && (value?.dirty ?: false))

        private val myQueryExecution = UpdatingQueryExecution(node, query)

        // if related node is null, or a new key provided, then replaces related node with merged node
        // otherwise, updates the related node
        fun merge(
            key:String? = null,
            block:((T & Any).()->Unit)?=null
        ) {
            if (value==null || key!=null) {
                value = label.merge(key ?: autoKey()) { block?.invoke(this) }
            } else {
                value?.apply { block?.invoke(this) }
            }
        }

        override var value: T
            get() = myQueryExecution.first(label) ?: localValue // still using localValue here for defaults
            set(value) {
                myQueryExecution.clear()
                value?.let {
                    myQueryExecution.extend(it)
                }
                if (dirties) {
                    // clear any existing dirties relationships
                    this@Node.getRelationships("DIRTIES", false).firstOrNull {
                        it.target == value
                    }?.delete()
                    // add new dirties relationship:
                    value?.relate(DIRTIES, this@Node)
                }
                iAmDirty = true
            }

    }

    // ==================================================

    // TODO maybe: consider operations other than sum... for now KISS and keep it at summation
    open inner class RespondingValueSlot(
        name:String, // TODO: needed?
        val linkQuery: RelatedQuery,
        default: Double,
        val dirties: Boolean = false, // TODO: consider whether this should default to true
    ): DataSlot<Double>(name, default) {

        // TODO: consider whether to cache the link node and/or slot ...
        //  for now, KISS, and also keep flexible to be able to dynamically re-link
        //  during playback. Consider caching if performance seems to be an issue.

        private val linkedNode get() = this@Node[linkQuery]().firstOrNull()

        private val linkedSlot: DataSlot<Double>? get() =
            linkedNode?.slot(name)

        override val dirty
            get() = iAmDirty || (dirties && (linkedNode?.dirty ?: false))

        override var value: Double
            // TODO maybe: also add a lambda/hook here for logic specific to this slot?
            //  (so that multiple nodes/slots could all access the same RespondingValue
            //  node, with different logic applied)?
            get() = linkedNode?.respondBlock?.invoke(name, localValue) ?: localValue

            set(value) {
                localValue = value
                if (dirties) {
                    // clear any existing dirties relationships
                    this@Node.getRelationships("DIRTIES", false).firstOrNull {
                        it.target == linkedNode
                    }?.delete()
                    // add new dirties relationship:
                    linkedNode?.relate(DIRTIES, this@Node)
                }
                iAmDirty = true
            }


//        // would use something like this if slot caching implemented:
//        override fun wireUp() {
//            this@Node[linkQuery]().firstOrNull()?.let {relatedNode->
////                val slotName: String = linkQuery.firstRelationshipProperty(this@Node.asSequence(), "slot") ?: name
//                linkedSlot = relatedNode.slot(name)
//            }
//        }

    }

    // ==================================================

    inner class PropertySlot<T:Any?>(
        name:String, // TODO: needed?
        override val property: KMutableProperty0<T>,
    ): DataSlot<T>(name, property.get()) {

        override var localValue: T
            get() = property.get()
            set(value) { property.set(value) }
    }

    // ==================================================

    inner class RespondingPropertySlot(
        name:String, // TODO: needed?
        override val property: KMutableProperty0<Double>,
        linkQuery: RelatedQuery,
        dirties: Boolean = false,
    ): RespondingValueSlot(name, linkQuery, property.get(), dirties) {

        override var localValue
            get() = property.get()
            set(value) { property.set(value) }
    }

    // TODO: review and re-implement or remove
//    // ==================================================
//
//    open inner class CascadingPatternSlot<T:Any?>(
//        name:String,
//        val pattern: Pattern<*>,
//        default: T
//    ): DataSlot<T>(name, default) {
//
//        override var value: T
//            get() = applicableSlot.value ?: localValue
//            set(value) { applicableSlot.value = value }
//
//        private var linkedSlot: DataSlot<T>? = null
//
//        private val applicableSlot: DataSlot<T>
//            get() = linkedSlot ?: this
//
//        override fun wireUp() {
//            // TODO: set linkedSlot based on pattern history
//        }
//    }
//
//    // ==================================================
//
//    inner class CascadingPatternPropertySlot<T:Any?>(
//        private val property: KMutableProperty0<T>,
//        pattern: Pattern<*>,
//    ): CascadingPatternSlot<T>(property.name, pattern, property.get()) {
//
//        override var localValue
//            get() = property.get()
//            set(value) { property.set(value) }
//    }

    // =================================================================================

    // TODO: bring fields back:
    // FIELD STUFF

//    // a managed map of attached ContectedField objects, for mass connecting them
//    // TODO maybe: should this just be a list? do we ever need to look up by field name?
//    // TODO: make this private once done debugging
//    val attachedFields: MutableMap<String, Field<Any?>.Attached> = mutableMapOf()
//
//    fun storeAllFields() {
//        attachedFields.forEach { (_, v) -> v.store() }
//    }
//
//    fun retrieveAllFields() {
//        attachedFields.forEach { (_, v) -> v.retrieve() }
//    }
//
//    fun <T:Any?>attach(field: Field<T>): Field<T>.Attached =
//        field.attach(this).also { af ->
//            attachedFields[field.name] = af as Field<Any?>.Attached // TODO: why is this cast necessary????
//        }
//
//    fun <T:Any?>attached(name:String): Field<T?>.Attached? =
//        attachedFields[name] as Field<T?>.Attached?
//
//    fun <T:Any?>attached(field: Field<T>) = attached<T>(field.name)
//
//
//    // returns value associated with a field name... note that the field does
//    // not have to be a field associated with this type (label) of node
//    // (facilitates things like getting values from Events for the fields they update)
//    // ... note it's always nullable since even if the field is required on another node type
//    // ... it can't be guaranteed to exist on this node type or in its properties
//    operator fun <T:Any?>get(fieldName:String):T? =
//        (attachedFields[fieldName]?.value ?: properties[fieldName]) as T?
//
//    operator fun <T:Any?>get(field: Field<T>):T? = get(field.name)
//
//    operator fun <T:Any?>set(field: Field<T>, value:T) {
//        attached(field)?.let { it.value = value; return }
//        properties[field.name] = value
//    }
//
//    // WARNING: this requires that the node property name matches the field name in order to work!
//    fun <T:Any?> KMutableProperty0<T>.updateFrom(node:Node) {
//        node.get<T>(this.name)?.let { this.set(it) }
//    }
//    fun <T:Any?> KMutableProperty0<T>.updateFrom(pattern:Pattern<*>) {
//        pattern.get<T>(this.name)?.let { this.set(it) }
//    }
//    fun <T:Any?> KMutableProperty0<T>.connect(
//        node:Node?=null,
//        connectFieldName:String?=null
//    ) {
//        // TODO: warning: problems will ensue if field is not FieldConnected type
//        (attached(Circle.radius) as FieldConnected.Attached?)?.connect(node, connectFieldName)
//    }
//
////    fun <T:Any?>updateFieldFrom(field:Field<T>, node:Node) {
////        node[field]?.let {this[field] = it}
////    }
////    fun <T:Any?>updateFieldFrom(field:Field<T>, pattern:Pattern<*>) {
////        pattern[field]?.let {this[field] = it}
////    }
//
//    fun updateAllFieldsFrom(node:Node) {
//        attachedFields.forEach { (_, af) ->
//            node[af.field]?.let { af.value = it}
//        }
//    }
//
//    fun updateAllFieldsFrom(pattern:Pattern<*>) {
//        attachedFields.forEach { (_, af) ->
//            pattern[af.field]?.let { af.value = it}
//        }
//    }
//
//    fun updateFieldsFrom(node:Node, vararg attachedFields:KMutableProperty0<*>) {
//        attachedFields.forEach { it.updateFrom(node) }
//    }
//    fun updateFieldsFrom(pattern:Pattern<*>, vararg attachedFields:KMutableProperty0<*>) {
//        attachedFields.forEach { it.updateFrom(pattern) }
//    }
//
//
////    fun save() { storeAllFields(); context.graph.save(this); }
//
////    fun read() { context.graph.read(this); retrieveAllFields() }
//
//    // TODO: consider implementing
////    open fun bump(vararg fromPatterns: Pattern) { println("invoke not implemented for $this") }
//
//
//    // TODO: consider re-implementing
////    fun <T: Node>cachedTarget(rLabel: RelationshipLabel, nLabel: NodeLabel<T>): Pattern.CachedTarget =
////        CachedTarget(this, rLabel, nLabel)
//
//    // TODO: maybe implement this...?
////    fun <T:Node>targetsOrMake(
////        rLabel:RelationshipLabel, // TODO: add default label
////        nLabel:NodeLabel<T>,
////        targetKey: String = autoKey()
////    ): T {
////        this[rLabel()](nLabel).firstOrNull()?.let { return it }
////        return nLabel.merge(targetKey).also { this.relate(rLabel, it) }
////    }

    // END FIELD STUFF

    // =================================================================================
    // =================================================================================

    // TODO: are these needed at the node level? or only the machine level?
    // (bump esp. might be useful globally)
//    open fun bump(node: Node) { println("render not implemented for $this") }

//    open fun gate(onOff:Boolean=true)  { println("gate not implemented for $this") }
//
//    open fun render(program: Program) { println("render not implemented for $this") }

}

// ================================================================

// just for fiddling around purposes...
open class Thingy protected constructor(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<Node, Thingy>(
        null, Thingy::class, { k-> Thingy(k) }
    )
    override val label: Label<out Node, out Thingy> = Thingy

    // attach fields here:
//    val thing = attach(Thingy.thing)     // TODO: maybe... eventually use delegation here

}

// ================================================================

// just for fiddling around purposes...
open class SpecialThingy protected constructor(
    key:String = autoKey(),
): Thingy(key) {
    companion object : NodeLabel<Thingy, SpecialThingy>(
        null, SpecialThingy::class, { k-> SpecialThingy(k) }
    )
    override val label: Label<out Thingy, out SpecialThingy> = SpecialThingy

    // attach fields here:
//    val specialThing = attach(SpecialThingy.specialThing)

}