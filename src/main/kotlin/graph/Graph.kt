package rain.graph

import rain.graph.interfacing.*
import kotlin.Exception

class Graph: GraphInterface {
    // TODO: make private after debugging:
    val graphNodes: MutableMap<String, GraphNode> = mutableMapOf()
    val graphRelationships: MutableMap<String, GraphRelationship> = mutableMapOf()

    val nodeLabelIndex: MutableMap<String, MutableMap<String, GraphNode>> = mutableMapOf()

    // TODO: used?
    val relationshipLabelIndex: MutableMap<String, MutableMap<String, GraphRelationship>> = mutableMapOf()

    override fun contains(key: String) = key in graphNodes || key in graphRelationships

    override fun contains(node: GraphableNode) = node.key in graphNodes
    override fun contains(relationship: GraphableRelationship) = relationship.key in graphRelationships

    // =================================================================================

    private fun addLabelIndex(label: String, graphNode: GraphNode) {
        nodeLabelIndex.getOrPut(label) { mutableMapOf() }[graphNode.key] = graphNode
    }

    private fun addLabelIndex(label: String, graphRelationship: GraphRelationship) {
        relationshipLabelIndex.getOrPut(label) { mutableMapOf() }[graphRelationship.key] = graphRelationship
    }

    // =================================================================================

    internal fun discardLabelIndex(label: String, graphNode: GraphNode) {
        nodeLabelIndex[label]?.remove(graphNode.key)
    }

    internal fun discardLabelIndex(label: String, graphRelationship: GraphRelationship) {
        relationshipLabelIndex[label]?.remove(graphRelationship.key)
    }

    // =================================================================================

    override fun create(node: GraphableNode) {
        graphNodes.putIfAbsent(node.key,
            GraphNode(node.key, node.labels, node.properties).also {
                node.labels.forEach { label -> addLabelIndex(label, it) }
            }
        )?.let {
            throw Exception("Node ${node.key} already exists in graph. So it can not be created.")
        }
    }

    override fun create(relationship: GraphableRelationship) {
        graphNodes[relationship.source.key]?.let { source ->
            graphNodes[relationship.target.key]?.let { target ->

                graphRelationships.putIfAbsent(relationship.key,
                    GraphRelationship(
                        relationship.key,
                        relationship.labelName,
                        source,
                        target,
                        relationship.properties
                    ).also {
                        source.connectRelationship(it, true)
                        target.connectRelationship(it, false)
                        addLabelIndex(it.labelName, it)
                    }
                )?.let {
                    throw Exception("Relationship ${relationship.key} already exists in graph. So it can not be created.")
                }
                return // OK!
            }
            throw Exception("Target ${relationship.target.key} not found in graph. Relationship not created.")
        }
        throw Exception("Source ${relationship.source.key} not found in graph. Relationship not created.")
    }

    // =================================================================================

    override fun merge(node: GraphableNode) {
        graphNodes[node.key]?.let { it.updatePropertiesFrom(node); return }
        create(node)
    }

    override fun merge(relationship: GraphableRelationship) {
        graphRelationships[relationship.key]?.let { it.updatePropertiesFrom(relationship); return }
        create(relationship)
    }

    // =================================================================================

    override fun read(node: GraphableNode) {
        graphNodes[node.key]?.let { node.updatePropertiesFrom(it); return }
        throw Exception("Node ${node.key} not found in graph; could not be read.")
    }

    override fun read(relationship: GraphableRelationship) {
        graphRelationships[relationship.key]?.let { relationship.updatePropertiesFrom(it); return }
        throw Exception("Relationship ${relationship.key} not found in graph; could not be read.")
    }

    // =================================================================================

    override fun getNode(key: String): GraphableNode {
        graphNodes[key]?.let { return it; }
        throw Exception("Node ${key} not found in graph; could not get.")
    }

    override fun getRelationship(key: String): GraphableRelationship {
        graphRelationships[key]?.let { return it; }
        throw Exception("Relationship ${key} not found in graph; could not get.")
    }

    override fun getRelationships(
        nodeKey:String,
        relationshipLabel:String,
        directionIsRight:Boolean
    ): Set<GraphableRelationship> =
        this.graphNodes[nodeKey]?.getRelationships(relationshipLabel, directionIsRight).orEmpty()

//    fun getRelationships(node:GraphableNode, relationshipLabel:String, directionIsRight:Boolean=true): Set<GraphableRelationship>
    // =================================================================================

    override fun save(node: GraphableNode) {
        graphNodes[node.key]?.let { it.updatePropertiesFrom(node); return }
        throw Exception("Node ${node.key} not found in graph; could not save.")
    }

    override fun save(relationship: GraphableRelationship) {
        graphRelationships[relationship.key]?.let { it.updatePropertiesFrom(relationship); return }
        throw Exception("Relationship ${relationship.key} not found in graph; could not save.")
    }

    // =================================================================================

    override fun deleteNode(key: String) {
        this.graphNodes[key]?.cleanup(this)
        this.graphNodes.remove(key)
    }

    override fun deleteRelationship(key: String) {
        this.graphRelationships[key]?.cleanup(this)
        this.graphRelationships.remove(key)
    }

    // =================================================================================

    fun clear() {
        graphNodes.clear()
        graphRelationships.clear()
        nodeLabelIndex.clear()
        relationshipLabelIndex.clear()
    }

    val nodeSize get() = graphNodes.size
    val relationshipSize get() = graphRelationships.size

    val size get() = nodeSize + relationshipSize

    // =================================================================================
    // =================================================================================

    override fun queryNodes(query: QueryInterface): Sequence<GraphNode> =
        when (query.method) {
            QueryMethod.SELECT -> sequence {
                (query.selectLabelName?.let { nodeLabelIndex[it].orEmpty() } ?: graphNodes).let {
                    if (query.selectKeys.isEmpty()) yieldAll(it.values)
                    else yieldAll(query.selectKeys.mapNotNull { k-> it[k] })
                }
            }.filterBy(query)

            QueryMethod.GRAPHABLE -> sequence {
                yieldAll(query.graphableNodes.mapNotNull { graphNodes[it.key] })
            }.filterBy(query) // TODO: worth keeping this filterBy here? (probably yes, for consistency, but not used for Patterns)

            QueryMethod.FILTER -> sequence {
                query.queryFrom?.let { q ->
                    yieldAll(queryNodes(q).filterBy(query))
                }
            }

            QueryMethod.RELATED_RIGHT, QueryMethod.RELATED_LEFT -> sequence {
                query.queryFrom?.let { q ->
                    queryNodes(q).forEach { n ->
                        n.getLabelsToRelationships(query.method.directionIsRight).let { ltr ->
                            if (query.selectLabelName == null)
                                ltr.values.forEach { rs -> yieldAll(rs) }
                            else
                                ltr[query.selectLabelName]?.let { rs -> yieldAll(rs) }
                        }
                    }
                }
            }.filterBy(query).map { it.directedTarget(query.method.directionIsRight) }

            QueryMethod.CONCAT -> sequence {
                query.queryFrom?.let { q -> yieldAll(queryNodes(q)) }
                query.queryFrom2?.let { q -> yieldAll(queryNodes(q)) }
            }.filterBy(query)
        }


    private fun <T:GraphableItem>Sequence<T>.filterBy(query: QueryInterface):Sequence<T> {
        query.predicate?.let { p-> return this.filter(p) }
        return this
    }

}





//fun <T:GraphItem>Sequence<T>.filterLabel(labelName:String?):Sequence<T> {
//    labelName?.let { return this.filter { it.labels.contains(labelName) } }
//    return this
//}
//
//fun <T:GraphItem>Sequence<T>.filterKeys(keys:List<String>?=null):Sequence<T> {
//    keys?.let { ks-> return this.filter { ks.contains(it.key) } }
//    return this
//}
//
//fun <T:GraphItem>Sequence<T>.filterProperties(properties:Map<String, Any?>?=null):Sequence<T> {
//    properties?.let { ps-> return this.filter { it.anyPropertyMatches(ps) } }
//    return this
//}

//fun Sequence<GraphRelationship>.filterProperties(properties:Map<String, Any?>?=null):Sequence<GraphRelationship> {
//    properties?.let { ps-> return this.filter { it.anyPropertyMatches(ps) } }
//    return this
//}