package rain.graph


import rain.graph.quieries.Queryable
import rain.graph.quieries.RelatedQuery
import rain.graph.quieries.UpdatingQueryExecution

import rain.utils.autoKey
import kotlin.reflect.KMutableProperty0
import kotlin.reflect.KProperty

// ===========================================================================================================

typealias RelationshipRegistry =  MutableMap<String, MutableSet<Relationship>>


open class Node protected constructor(
    key:String,
): Item(key), Queryable<Node> {
    companion object : NodeLabel<Node, Node>(
        null, Node::class, { k-> Node(k) }
    )

    override val label: Label<*, out Node> = Node

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

    private val dataSlots = mutableMapOf<String, DataSlot<*>>()

    val slotNames: Set<String> get() = dataSlots.keys

    fun putSlot(name: String, value: Any?) {
        dataSlots.putIfAbsent(name, DataSlot(name, value))?.updateLocalValue(value)
    }

    fun <T:Any?> slot(name:String): DataSlot<T>? =
        dataSlots[name] as DataSlot<T>?

    // TODO: would this be used?
//    fun slotsFromProperties(vararg properties:KMutableProperty0<Double>) {
//        properties.forEach { PropertySlot(it) }
//    }

    fun slotsToMap(): Map<String, Any?> {
        return dataSlots.mapValues { it.value }
    }

    fun updateSlotsFrom(map: Map<String, Any?>) {
        map.forEach { (n, v) -> dataSlots[n]?.updateLocalValue(v) }
    }

    private fun registerSlot(dataSlot: DataSlot<*>) {
        dataSlots[dataSlot.name] = dataSlot
    }

    fun wireUpSlots() {
        dataSlots.values.forEach{ it.wireUp() }
    }

    open fun <T:Any?>updateSlotFrom(name:String, fromSlot: DataSlot<T>) {
        slot<T>(name)?.let { slot->
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

    // ==================================================

    open inner class DataSlot<T:Any?>(
        val name:String, // TODO: needed?
        default: T
    ) {

        val node get() = this@Node

        protected open var localValue: T = default

        open val property: KMutableProperty0<T>? = null // included here for interoperability

        fun updateLocalValue(fromAny: Any?) {
            localValue = fromAny as T
        }

        open var value: T
            get() = localValue
            set(value) { localValue = value }

        open fun wireUp() { } // included here for interoperability

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
        default: T
    ): DataSlot<T>(name, default) {

        private val myQueryExecution = UpdatingQueryExecution(node, query)

        override var value: T
            get() = myQueryExecution.first(label) ?: localValue
            set(value) {
                myQueryExecution.clear()
                value?.let {
                    myQueryExecution.extend(it)
                }
            }

    }

    // ==================================================

    open inner class LinkableSlot<T:Any?>(
        name:String, // TODO: needed?
        val linkQuery: RelatedQuery,
        default: T
    ): DataSlot<T>(name, default) {

        override var value: T
            get() = applicableSlot.value ?: localValue
            set(value) { applicableSlot.value = value }

        private var linkedSlot: DataSlot<T>? = null

        private val applicableSlot: DataSlot<T>
            get() = linkedSlot ?: this

        override fun wireUp() {
            this@Node[linkQuery]().firstOrNull()?.let {relatedNode->
                val slotName: String = linkQuery.firstRelationshipProperty(this@Node.asSequence(), "slot") ?: name
                linkedSlot = relatedNode.slot(slotName)
            }
        }

    }

    // ==================================================

    inner class PropertySlot<T:Any?>(
        override val property: KMutableProperty0<T>,
    ): DataSlot<T>(property.name, property.get()) {

        override var localValue: T
            get() = property.get()
            set(value) { property.set(value) }
    }

    // ==================================================

    inner class LinkablePropertySlot<T:Any?>(
        override val property: KMutableProperty0<T>,
        linkQuery: RelatedQuery,
    ): LinkableSlot<T>(property.name, linkQuery, property.get()) {

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