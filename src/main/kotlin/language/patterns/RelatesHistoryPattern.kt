package rain.language.patterns

// TODO: is this even used any more???? assume NOT
//open class RelatesHistoryPattern<T: Node>(
//    source: T,
//    previous: Pattern<*>? = null,
//    relationshipLabel: RelationshipLabel,
//    // an optional extended list of relationships, beyond the primary relationship, for querying (but not extending):
//    vararg extendedRelationships: RelationshipLabel):
//    RelatesPattern<T>(source, previous, relationshipLabel, *extendedRelationships) {
//
////    override fun copy(anotherPattern: Pattern): Dimension = RelatesHistoryDimension(anotherPattern, relationshipLabel, *extendedRelationships)
//
//    override val graphableNodes = sequence<GraphableNode> {
//        history.forEach { hn ->
//            yieldAll(
//                hn.get(
//                    relationshipLabel(),
//                    *(extendedRelationships.map { it() }.toTypedArray())
//                ).graphableNodes
//            )
//        }
//    }
//
//}