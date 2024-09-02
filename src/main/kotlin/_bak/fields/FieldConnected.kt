package rain.language.fields

//import graph.Node
//import rain.graph.RelationshipLabel
//import rain._bak.patterns.Pattern
//import rain._bak.patterns.RelatesPattern
//import kotlin.reflect.KProperty
//
//// TODO: review naming and logic for confusion
//open class FieldConnected<T:Any?>(
//    name: String,
//    val patternFactory: (source: Node)-> Pattern<*>,
////    val relatedFieldOfNode: FieldOfNode<*>? = null, // TODO: consider this, but for now, KISS!
//    default: T,
//    cascade: Boolean = true,
//    val defaultToSelf:Boolean = true
//): Field<T>(name, default, cascade) {
//
//    override val isNode: Boolean = true
//
//    inner class Attached(
//        node: Node,
//        val pattern: Pattern<*>,
//    ): Field<T>.Attached(node) {
//
//        // TODO: is this a better implementation for connectingField
//        var connectedNodeFieldName: String
//            get() =
//                node.properties.getOrDefault("$name:connectedField", name ) as String
//            set(value) {
//                node.properties["$name:connectedField"] = value
//            }
//
//        // TODO: make private after debugging
//        var connectedAttached: Field<T?>.Attached? = null
//
//        override fun resetValue() { connectedAttached?.resetValue() } // TODO: is this OK?
//
//        override fun retrieve() {
//            pattern().firstOrNull()?.let { connectedNode ->
//                connectedAttached = connectedNode.attached(connectedNodeFieldName)
//                return
//            }
//            // if no connectedAttached and defaulting to self,
//            // then calls super to set local value based on properties
//            if (this@FieldConnected.defaultToSelf) super.retrieve()
//
//        }
//
//        override fun store()  {
//            // if no connectedAttached and defaulting to self,
//            // then calls super to store local value based in the properties
//            if (connectedAttached==null && this@FieldConnected.defaultToSelf) super.store()
//        }
//
//        // null node disconnects
//        fun connect(node:Node?=null, connectFieldName:String?=null) {
//            println(node?.key)
//            println(node?.attached<Field<T?>.Attached>(connectedNodeFieldName)?.value)
//            pattern.clear()
//            node?.let { pattern.extend(it) }
//            connectFieldName?.let { connectedNodeFieldName = it }
//            connectedAttached = node?.attached(connectedNodeFieldName)
//            connectedAttached?.let {
//                it.retrieve()
//                println("Attached field name: ${it.field.name}")
//                println("Attached value: ${it.value}")
//                println("===")
//            }
//            // similar to retrieve, if no connectedAttached (e.g. after disconnecting) and defaulting to self,
//            // then calls super retrieve to set local value based on properties
//            if (connectedAttached==null && this@FieldConnected.defaultToSelf) super.retrieve()
//        }
//
//        override operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
//            connectedAttached?.value ?: this.value ?: default
//
//        override operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {
//            connectedAttached?.let {
//                it.value = value
//                return
//            }
//            if (this@FieldConnected.defaultToSelf) {
//                this.value = value
//                return
//            }
//            println("WARNING: attempted to set field '${this.field.name}' value on connected node, but node not connected, and not defaulting to self.")
//        }
//
//    }
//
//
//    override fun attach(node: Node) = Attached(node, patternFactory(node))
//
//}
//
////// ======================================================================
//
//fun <T:Any?> field(
//    name: String,
//    relationshipLabel: RelationshipLabel,
//    default: T,
//    cascade: Boolean = true,
//    defaultToSelf: Boolean=true
//) =
//    FieldConnected(
//        name,
//        {s-> RelatesPattern(s, null, relationshipLabel) },
//        default,
//        cascade,
//        defaultToSelf
//    )
//
