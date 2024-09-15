package rain.graph

import rain.graph.queries.*
import rain.utils.autoKey
import kotlin.reflect.KClass



abstract class Label<PT: Item, T:PT>(
    val parent: Label<*, PT>? = null,
    val kClass: KClass<T>
): QueryableMulti<T> {

    open val labelName: String = kClass.simpleName ?: "Node"

    internal val registry: MutableMap<String, T> = mutableMapOf()

    val ancestorLabels: Array<Label<*, *>> =
        if (parent==null) arrayOf()
        else arrayOf(parent, *parent.ancestorLabels)

//    // TODO maybe: consider whether implementing childLabels would be useful, for now, KISS
//        would be needed if wanted a clear() method to clear the entire registry graph
//    val childLabels: MutableMap<String, Label<*, *>> = mutableMapOf()
//
//    fun registerChildLabel(label:Label<*, *>) {
//        childLabels[label.labelName] = label
//    }

    val size: Int get() = registry.size

    protected fun register(item: T) {
        registry.putIfAbsent(item.key, item)?.let {
            throw Exception("Can not register item with key '${item.key} as a $labelName because it already exists.")
        }
//        registry[item.key] = item
        parent?.register(item)
    }

    operator fun get(key: String): T? = registry[key]

    operator fun get(vararg keys: String): Select<T> =
        keys.asSequence().mapNotNull { registry[it] }.asSelect()

    override fun asSequence(): Sequence<T> = registry.values.asSequence()

    // =================================
    // overriding these indexing-related methods to perform better by accessing base label's registry map directly

    override fun asKeys(): Sequence<String> = registry.keys.asSequence()

    override fun indexOf(item: T): Int = registry.values.indexOf(item)

    override fun indexOf(key:String): Int = registry.keys.indexOf(key)

    override operator fun contains(item: T) = registry.containsValue(item)

    override operator fun contains(key: String) = registry.containsKey(key)

    // =================================

    // note: only makes sense for rain.score.nodes (since rain.score.relationships don't have parent/child labels)
    fun from(item: Item): T? {
        registry[item.key]?.let { return it }
        println("WARNING: cannot get node of type associated with label '$labelName', since node with key ${item.key} is not in the label registry. Returning null instead.")
        return null
    }

    // warning, should only be called from the item delete() method
    // to guarantee that item is deleted from most specific child label
    internal abstract fun unregisterFromLabel(key:String): Unit

    override fun toString() = "NodeLabel(name=$labelName)"

}

// ==========================================================================

abstract class NodeLabel<PT: Node, T:PT>(
    parent: Label<*, PT>? = null,
    kClass: KClass<T>,
    val factory: (String) -> T,
): Label<PT, T>(parent, kClass) {

    fun create(
        key:String = autoKey(),
        block: (T.() -> Unit)? = null
    ):T = factory(key).also { node->
        register(node)
        block?.invoke(node)
//        node.wireUpSlots()
    }

    fun merge(
        key: String,
        block: (T.() -> Unit)? = null,
    ): T = registry.getOrElse(key) { factory(key).also { register(it) } }.also { node->
        block?.invoke(node)
//        node.wireUpSlots()
    }


    // TODO maybe: implement merge()

    override fun unregisterFromLabel(key: String) {
        parent?.unregisterFromLabel(key)
        registry.remove(key)
    }

}

// ==========================================================================

class RelationshipLabel(
    override val labelName:String,
): Label<Relationship, Relationship>(
    null,
    Relationship::class
) {

    private val myRightQuery = RelatedQuery(this)
    operator fun unaryPlus() = myRightQuery

    private val myLeftQuery = RelatedQuery(this)
    operator fun unaryMinus() = myLeftQuery

    override fun unregisterFromLabel(key:String) {
        registry.remove(key)?.also {
            // TODO: test to make sure registration removed
            it.source.unregisterRelationship(it, true)
            it.target.unregisterRelationship(it, false)
        }
    }


    fun create(source: Node, target: Node, key:String= autoKey()): Relationship =
        Relationship(this, source, target, key).also {
            register(it)
            source.registerRelationship(it, true)
            target.registerRelationship(it, false)
        }

    // TODO maybe: implement merge()

}





//    fun create(
//        key: String = autoKey(),
//        properties: Map<String, Any?>? = null,
//        block: (T.() -> Unit)? = null,
//    ): T =
//        factory(key).apply {
//            properties?.let { this.updatePropertiesFrom(it) }
//            retrieveAllFields()
//            context.graph.create(this)
//            registry[key] = this
//            block?.let {
//                it.invoke(this)
//                save()
//            }
//        }


//    private fun myAllNames(): Array<String> = arrayOf(labelName, *parent?.allNames.orEmpty())
//    final override val allNames by lazy { myAllNames() } // TODO: this by lazy is odd here, but static value not set correctly otherwise
//
//    private fun myQueryMe(): Query = Query(selectLabelName = labelName)
//    final override val queryMe by lazy { myQueryMe() } // TODO: this by lazy is odd here, but static value not set correctly otherwise

//    operator fun get(vararg keys: String) = Query(selectKeys = keys)

    // TODO : consider re-instituting this format to avoid double saves
    //  (but for now, saving twice to KISS)
//    fun <RL:NodeLabel<*>>sends(
//        receiving:RL,
//        key:String = autoKey(),
//        preCreate:(RL.(Message<RL>)->Unit)?=null, // TODO: naming?
//        postCreate:(RL.(T)->Unit)?=null,
//    ): T {
//        val message = LocalMessage(receiving)
//        preCreate?.invoke(receiving, message)
//        return this.create(key, message.properties).apply {
//            postCreate?.invoke(receiving, this)
//        }
//    }

    // TODO: why even bother?
//    fun <RL:NodeLabel<*>>sends(
//        receiving:RL,
//        key:String = autoKey(),
//        properties: Map<String, Any?>? = null, // TODO: keep this? (assume yes)
//        block:(RL.(T)->Unit)?=null,
//    ): T = create(key, properties).apply {
//        block?.let {
//            it.invoke(receiving, this)
//            save()
//        }
//    }




//    fun registerMe() {
//        context.nodeLabels[labelName] = this
//    }

//    init {
//        registerMe()
//    }



// TODO maybe: do above methods (sends, get, etc. ) need to be defined here globally instead?


// TODO: naming?
// TODO: bring back as needed based on rain.solve implementation

//fun <R: Node, RL:NodeLabel<R>>RL.receives(
//    sender: Node,
//    key:String=autoKey(),
//    messageBlock: (RL.(Message<RL>)->Unit)?=null,
//    receiverBlock: ((R)->Unit)?=null
//) {
//    val receiver = this.merge(key, messageBlock)
//    sender.relate(TARGETS, receiver) // TODO: replace with BUMPS
//    receiverBlock?.invoke(receiver)
//}
//
//
//fun <N:Node, R:Node, RL:NodeLabel<R>>N.relateMerge(
//    field: Field<*>,
//    relatedLabel: RL,
//    key: String = autoKey(),
//    messageBlock: (RL.(Message<RL>)->Unit)?=null,
//    postCreate:(RL.(R)->Unit)? = null,
//) {
//    // TODO: complete this...
//    val relatedNode = relatedLabel.merge(key, messageBlock)
//    // TODO: add fieldName to the relationship
//    this.relate(field.relationshipLabel!!, relatedNode) // TODO: guarantee that relationshipLabel not null
//}
//
//// TODO maybe: define this in Node base class instead of here?
//fun <N:Node>N.relate(field: Field<*>, targetKey: String) {
//    if (!label.fields.contains(field.name))
//        // prevents erroneous field-based rain.score.relationships from being added
//        throw Exception("Field '${field.name}' not found on '$label' label.")
//    // TODO: guarantee that relationshipLabel not null
//    relate(field.relationshipLabel!!, targetKey)
//}
//
//fun <N:Node>N.relate(field: Field<*>, targetNode: Node) { relate(field, targetNode.key) }
//
//
//fun <N:Node, FT:Node, RL:NodeLabel<FT>>N.fieldTo(
//    field: Field<FT>,
//    relatedLabel: RL,
//    key: String = autoKey(),
//    messageBlock: (RL.(Message<RL>)->Unit)?=null,
//    postCreate:(RL.(FT)->Unit)?=null,
//) {
//    // TODO: complete this...
//    val relatedNode = relatedLabel.merge(key, messageBlock)
//    // TODO: add fieldName to the relationship
//    this.relate(field.relationshipLabel!!, relatedNode) // TODO: guarantee that relationshipLabel not null
//}