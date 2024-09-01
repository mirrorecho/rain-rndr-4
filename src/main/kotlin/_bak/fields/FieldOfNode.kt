package rain.language.fields

//import rain.language.Node
//import rain.language.Label
//import rain.language.RelationshipLabel
//import rain.language.patterns.Pattern
//import rain.language.patterns.RelatesPattern
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