package rain.language.patterns

import rain.language.RelationshipLabel
import rain.language.Node

// base dimension for simple relationship-based items
open class RelatesPattern<T:Node>(
    source: T,
    previous: Pattern<*>? = null,
    val relationshipLabel: RelationshipLabel,
    // an optional extended list of relationships, beyond the primary relationship, for querying (but not extending):
    vararg val extendedRelationships: RelationshipLabel // TODO: is this used????
): Pattern<T>(source, previous) {

    private val relatesQuery = this.source.get(
        relationshipLabel(),
        *(extendedRelationships.map { it() }.toTypedArray())
    )

    override val graphableNodes get() = relatesQuery.graphableNodes.orEmpty()

    override fun extend(vararg nodes: Node) {
        nodes.forEach { n -> source.relate(relationshipLabel, n) }
    }

    // TODO: implement...
    override fun clear(deleteNodes:Boolean) = warningNotImplemented("clear")

}