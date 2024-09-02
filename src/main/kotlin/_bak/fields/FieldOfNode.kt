package rain.language.fields

//import graph.Node
//import rain.graph.Label
//import rain.graph.RelationshipLabel
//import rain._bak.patterns.Pattern
//import rain._bak.patterns.RelatesPattern
//
//
//open class FieldOfNode<T:Node?>(
//    name: String,
//    val patternFactory: (source: Node)-> Pattern<*>,
//    val label: Label<T & Any>,
//    default: T,
//    cascade: Boolean = true
//): Field<T>(name, default, cascade) {
//
//    override val isNode: Boolean = true
//
//    open inner class Attached(
//        node: Node,
//        val pattern: Pattern<*>,
//    ): Field<T>.Attached(node) {
//
//        override val isLocal: Boolean = false
//
//        override fun store() {
//            pattern.clear()
//            value?.let { pattern.extend(it) }
//        }
//
//        override fun retrieve() {
//            value = pattern(this@FieldOfNode.label).firstOrNull() ?: default
//        }
//
//    }
//
//    override fun attach(node: Node): Attached = Attached(node, patternFactory(node))
//}
//
////// ======================================================================
//
//fun <T: Node?> fieldOfNode(
//    name: String,
//    relationshipLabel: RelationshipLabel,
//    label: Label<T & Any>,
//    default: T,
//    cascade: Boolean = true,
//) =
//    FieldOfNode(
//        name,
//        {s -> RelatesPattern(s, null, relationshipLabel) },
//        label,
//        default,
//        cascade,
//    )