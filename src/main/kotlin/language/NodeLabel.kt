package rain.language

import rain.graph.interfacing.*
import rain.utils.autoKey
import kotlin.reflect.KClass



abstract class NodeLabel<T: Node>(
    parentLabel: NodeLabel<*>? = null,
    ): Queryable, Label<T>() {

    open val parent: NodeLabel<*>? = null
    abstract fun factory(key:String): T

    private fun myAllNames(): Array<String> = arrayOf(labelName, *parent?.allNames.orEmpty())
    final override val allNames by lazy { myAllNames() } // TODO: this by lazy is odd here, but static value not set correctly otherwise

    private fun myQueryMe():Query = Query(selectLabelName=labelName)
    final override val queryMe by lazy { myQueryMe() } // TODO: this by lazy is odd here, but static value not set correctly otherwise

    operator fun get(vararg keys:String) = Query(selectKeys=keys)

    // a cache of all nodes for this label
    val registry: MutableMap<String, T> = mutableMapOf()

    override fun toString() = labelName

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


    // TODO: is this OK? (factory returns instance of T, not the specific sub-type of
    //  T as would be applicable for the given T)
    fun get(key: String): T =
        registry.getOrPut(key) {
            factory(key).apply { read() }
        }

    fun from(gNode: GraphableNode): T =
        registry.getOrPut(gNode.key) {
            factory(gNode.key).apply {
                updatePropertiesFrom(gNode)
                retrieveAllFields()
            }
        }

    fun merge(
        key: String = autoKey(),
        properties: Map<String, Any?>? = null,
    ): T =
        registry.getOrPut(key) { factory(key) }.also { node ->
            properties?.let { node.updatePropertiesFrom(it) };
            node.retrieveAllFields()
            context.graph.merge(node)
        }

    fun create(
        key: String = autoKey(),
        properties: Map<String, Any?>? = null,
        block:(T.()->Unit)?=null,
    ): T =
        factory(key).apply {
            properties?.let { this.updatePropertiesFrom(it) }
            retrieveAllFields()
            context.graph.create(this)
            registry[key] = this
            block?.let {
                it.invoke(this)
                save()
            }
        }


    fun registerMe() {
        context.nodeLabels[labelName] = this
    }

//    init {
//        registerMe()
//    }

}

// TODO maybe: do above methods (sends, get, etc. ) need to be defined here globally instead?


// TODO: naming?
// TODO: bring back as needed based on solve implementation

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
//        // prevents erroneous field-based relationships from being added
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