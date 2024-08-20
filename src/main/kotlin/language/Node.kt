package rain.language

import rain.graph.interfacing.*
import rain.language.fields.Field
import rain.language.fields.field
import rain.language.patterns.Pattern
import rain.utils.autoKey

// ===========================================================================================================

abstract class Node protected constructor(
    key:String = autoKey(),
): Queryable, GraphableNode, Item(key) {
    //    companion object : NodeLabel<Node>(Node::class, null, { k->Node(k) })
    abstract override val label: NodeLabel<out Node>

    override val context get() = label.context

    override val queryMe get() = Query(selectKeys = arrayOf(this.key))

    // TODO: are these needed at the node level? or only the machine level?
    // (bump esp. might be useful globally)
//    open fun bump(node: Node) { println("render not implemented for $this") }

//    open fun gate(onOff:Boolean=true)  { println("gate not implemented for $this") }
//
//    open fun render(program: Program) { println("render not implemented for $this") }


    fun save() { storeAllFields(); context.graph.save(this); }

    fun read() { context.graph.read(this); retrieveAllFields() }

    fun delete() {
        context.graph.deleteNode(this.key)
        label.registry.remove(this.key)
    }

    fun relate(
        rLabel: RelationshipLabel,
        targetKey:String,
        key:String = autoKey()
    ): Relationship = rLabel.create(this.key, targetKey, key)

    fun relate(
        rLabel: RelationshipLabel,
        targetNode: Node,
        key:String = autoKey()
    ): Relationship = rLabel.create(this.key, targetNode.key, key)

    fun getGraphableRelationships(relationshipLabelName:String, directionIsRight:Boolean=true) =
        context.graph.getRelationships(this.key, relationshipLabelName, directionIsRight)

    fun getRelationships(relationshipLabel:RelationshipLabel, directionIsRight:Boolean=true) =
        getGraphableRelationships(relationshipLabel.labelName, directionIsRight).map { relationshipLabel.from(it) }

    // a managed map of attached ContectedField objects, for mass connecting them
    // TODO maybe: should this just be a list? do we ever need to look up by field name?
    private val attachedFields: MutableMap<String, Field<Any?>.Attached> = mutableMapOf()

    fun storeAllFields() {
        attachedFields.forEach { (_, v) -> v.store() }
    }

    fun retrieveAllFields() {
        attachedFields.forEach { (_, v) -> v.retrieve() }
    }

    fun <T:Any?>attach(field: Field<T>): Field<T>.Attached =
        field.attach(this).also { af ->
            attachedFields[field.name] = af as Field<Any?>.Attached // TODO: why is this cast necessary????
        }

    fun <T:Any?>attached(name:String): Field<T?>.Attached? =
        attachedFields[name] as Field<T?>.Attached?

    fun <T:Any?>attached(field: Field<T>) = attached<T>(field.name)

    // returns value associated with a field name... note that the field does
    // not have to be a field associated with this type (label) of node
    // (facilitates things like getting values from Events for the fields they update)
    // ... note it's always nullable since even if the field is required on another node type
    // ... it can't be guaranteed to exist on this node type or in its properties
    operator fun <T:Any?>get(field: Field<T>):T =
        (attached(field)?.value ?: properties[field.name]) as T? ?: field.default

    operator fun <T:Any?>set(field: Field<T>, value:T) {
        attached(field)?.let { it.value = value; return }
        properties[field.name] = value
    }

    fun updateAllFieldsFrom(node:Node) {
        node.attachedFields.forEach {
            attachedFields[it.key]?.let { af ->
                // TODO maybe: only update non-node fields?
                af.value = node[af.field]
            }
        }
    }

    fun updateAllFieldsFrom(pattern:Pattern<*>) {
        pattern.source.attachedFields.forEach {
            attachedFields[it.key]?.let { af ->
                // TODO maybe: only update non-node fields?
                af.value = pattern[af.field]
            }
        }
    }

    // TODO: consider implementing
//    open fun bump(vararg fromPatterns: Pattern) { println("invoke not implemented for $this") }


    // TODO: consider re-implementing
//    fun <T: Node>cachedTarget(rLabel: RelationshipLabel, nLabel: NodeLabel<T>): Pattern.CachedTarget =
//        CachedTarget(this, rLabel, nLabel)

    // TODO: maybe implement this...?
//    fun <T:Node>targetsOrMake(
//        rLabel:RelationshipLabel, // TODO: add default label
//        nLabel:NodeLabel<T>,
//        targetKey: String = autoKey()
//    ): T {
//        this[rLabel()](nLabel).firstOrNull()?.let { return it }
//        return nLabel.merge(targetKey).also { this.relate(rLabel, it) }
//    }

    // TODO: maybe implement this...?
//    fun invoke()

}

// ================================================================

// just for fiddling around purposes...
open class Thingy protected constructor(
    key:String = autoKey(),
): Node(key) {
    abstract class ThingyLabel<T: Thingy>: NodeLabel<T>() {
        // add fields here:
        val thing = field("thing", "One and Two")
    }

    companion object : ThingyLabel<Thingy>() {
        // override val parent = ThingyParent // only use if parent label exists
        override val labelName:String = "Thingy"
        override fun factory(key:String) = Thingy(key)
    }

    // note that the NodeLabel<out T> type declaration here is needed so that inheritance works OK
    override val label: NodeLabel<out Thingy> = Thingy

    // attach fields here:
    val thing = attach(Thingy.thing)     // TODO: maybe... eventually use delegation here

}

// ================================================================

// just for fiddling around purposes...
open class SpecialThingy protected constructor(
    key:String = autoKey(),
): Thingy(key) {
    abstract class SpecialThingyLabel<T: SpecialThingy>: ThingyLabel<T>() {
        // add fields here:
        val specialThing = field("specialThing", "Three")
    }

    companion object : SpecialThingyLabel<SpecialThingy>() {
        // override val parent = ThingyParent // only use if parent label exists
        override val labelName:String = "SpecialThingy"
        override fun factory(key:String) = SpecialThingy(key)
    }

    // note that the NodeLabel<out T> type declaration here is needed so that inheritance works OK
    override val label: NodeLabel<out SpecialThingy> = SpecialThingy

    // attach fields here:
    val specialThing = attach(SpecialThingy.specialThing)

}