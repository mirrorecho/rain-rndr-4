package rain.graph

import rain.graph.interfacing.*

class GraphRelationship(
    override val key:String,
    override val labelName: String,
    override val source: GraphNode,
    override val target: GraphNode,
    properties: Map<String, Any?> = mapOf()
) : GraphableRelationship {

    override fun toString():String = "GRAPH RELATIONSHIP: (${source.key} $labelName ${target.key} | $key) $properties"

    override val properties: MutableMap<String, Any?> = properties.toMutableMap()

    // TODO: replace with label instance
    override val labels get() = arrayOf(labelName)

    override fun directedTarget(directionIsRight:Boolean): GraphNode = if (directionIsRight) target else source

    fun cleanup(graph: Graph) {
        graph.discardLabelIndex(labelName, this)
        source.disconnectRelationship(this, true)
        target.disconnectRelationship(this, false)
    }
}