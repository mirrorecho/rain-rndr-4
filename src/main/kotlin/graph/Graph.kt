package rain.graph
//
//import rain.graph.interfacing.*
//import rain.language.Node
//import rain.language.Query
//import rain.language.QueryMethod
//import rain.language.Relationship
//import kotlin.Exception
//
//class Graph {
//    // TODO: make private after debugging:
//
//    val nodeIndex: MutableMap<String, Node> = mutableMapOf()
//    val nodeLabelIndex: MutableMap<String, MutableMap<String, Node>> = mutableMapOf()
//
//    val relationshipIndex: MutableMap<String, GraphRelationship> = mutableMapOf()
//
//
//
//    // TODO: used?
//    val relationshipLabelIndex: MutableMap<String, MutableMap<String, GraphRelationship>> = mutableMapOf()
//
//    fun contains(key: String) = key in nodeIndex || key in relationshipIndex
//
//    fun contains(node: Node) = node.key in nodeIndex
//    fun contains(relationship: Relationship) = relationship.key in relationshipIndex
//
//    // =================================================================================
//
//    private fun addLabelIndex(label: String, node: Node) {
//        nodeLabelIndex.getOrPut(label) { mutableMapOf() }[node.key] = node
//    }
//
//
//    private fun addLabelIndex(label: String, graphRelationship: GraphRelationship) {
//        relationshipLabelIndex.getOrPut(label) { mutableMapOf() }[graphRelationship.key] = graphRelationship
//    }
//
//    // =================================================================================
//
//    internal fun discardLabelIndex(label: String, graphNode: GraphNode) {
//        nodeLabelIndex[label]?.remove(graphNode.key)
//    }
//
//    internal fun discardLabelIndex(label: String, graphRelationship: GraphRelationship) {
//        relationshipLabelIndex[label]?.remove(graphRelationship.key)
//    }
//
//    // =================================================================================
//
////    override fun create(node: GraphableNode) {
////        allNodes.putIfAbsent(node.key,
////            GraphNode(node.key, node.labels, node.properties).also {
////                node.labels.forEach { label -> addLabelIndex(label, it) }
////            }
////        )?.let {
////            throw Exception("Node ${node.key} already exists in graph. So it can not be created.")
////        }
////    }
//
//    override fun create(relationship: GraphableRelationship) {
//        nodeIndex[relationship.source.key]?.let { source ->
//            nodeIndex[relationship.target.key]?.let { target ->
//
//                relationshipIndex.putIfAbsent(relationship.key,
//                    GraphRelationship(
//                        relationship.key,
//                        relationship.labelName,
//                        source,
//                        target,
//                        relationship.properties
//                    ).also {
//                        source.connectRelationship(it, true)
//                        target.connectRelationship(it, false)
//                        addLabelIndex(it.labelName, it)
//                    }
//                )?.let {
//                    throw Exception("Relationship ${relationship.key} already exists in graph. So it can not be created.")
//                }
//                return // OK!
//            }
//            throw Exception("Target ${relationship.target.key} not found in graph. Relationship not created.")
//        }
//        throw Exception("Source ${relationship.source.key} not found in graph. Relationship not created.")
//    }
//
//    // =================================================================================
//
//    override fun merge(node: GraphableNode) {
//        nodeIndex[node.key]?.let { it.updatePropertiesFrom(node); return }
//        create(node)
//    }
//
//    override fun merge(relationship: GraphableRelationship) {
//        relationshipIndex[relationship.key]?.let { it.updatePropertiesFrom(relationship); return }
//        create(relationship)
//    }
//
//    // =================================================================================
//
//    override fun read(node: GraphableNode) {
//        nodeIndex[node.key]?.let { node.updatePropertiesFrom(it); return }
//        throw Exception("Node ${node.key} not found in graph; could not be read.")
//    }
//
//    override fun read(relationship: GraphableRelationship) {
//        relationshipIndex[relationship.key]?.let { relationship.updatePropertiesFrom(it); return }
//        throw Exception("Relationship ${relationship.key} not found in graph; could not be read.")
//    }
//
//    // =================================================================================
//
//    override fun getNode(key: String): GraphableNode {
//        nodeIndex[key]?.let { return it; }
//        throw Exception("Node ${key} not found in graph; could not get.")
//    }
//
//    override fun getRelationship(key: String): GraphableRelationship {
//        relationshipIndex[key]?.let { return it; }
//        throw Exception("Relationship ${key} not found in graph; could not get.")
//    }
//
//    override fun getRelationships(
//        nodeKey:String,
//        relationshipLabel:String,
//        directionIsRight:Boolean
//    ): Set<GraphableRelationship> =
//        this.nodeIndex[nodeKey]?.getRelationships(relationshipLabel, directionIsRight).orEmpty()
//
////    fun getRelationships(node:GraphableNode, relationshipLabel:String, directionIsRight:Boolean=true): Set<GraphableRelationship>
//    // =================================================================================
//
//    override fun save(node: GraphableNode) {
//        nodeIndex[node.key]?.let { it.updatePropertiesFrom(node); return }
//        throw Exception("Node ${node.key} not found in graph; could not save.")
//    }
//
//    override fun save(relationship: GraphableRelationship) {
//        relationshipIndex[relationship.key]?.let { it.updatePropertiesFrom(relationship); return }
//        throw Exception("Relationship ${relationship.key} not found in graph; could not save.")
//    }
//
//    // =================================================================================
//
//    override fun deleteNode(key: String) {
//        this.nodeIndex[key]?.cleanup(this)
//        this.nodeIndex.remove(key)
//    }
//
//    override fun deleteRelationship(key: String) {
//        this.relationshipIndex[key]?.cleanup(this)
//        this.relationshipIndex.remove(key)
//    }
//
//    // =================================================================================
//
//    fun clear() {
//        nodeIndex.clear()
//        relationshipIndex.clear()
//        nodeLabelIndex.clear()
//        relationshipLabelIndex.clear()
//    }
//
//    val nodeSize get() = nodeIndex.size
//    val relationshipSize get() = relationshipIndex.size
//
//    val size get() = nodeSize + relationshipSize
//
//    // =================================================================================
//    // =================================================================================
//
//    fun queryNodes(query: Query): Sequence<GraphNode> =
//        when (query.method) {
//            QueryMethod.SELECT -> sequence {
//                (query.selectLabelName?.let { nodeLabelIndex[it].orEmpty() } ?: nodeIndex).let {
//                    if (query.selectKeys.isEmpty()) yieldAll(it.values)
//                    else yieldAll(query.selectKeys.mapNotNull { k-> it[k] })
//                }
//            }.filterBy(query)
//
//            QueryMethod.GRAPHABLE -> sequence {
//                yieldAll(query.graphableNodes.mapNotNull { nodeIndex[it.key] })
//            }.filterBy(query) // TODO: worth keeping this filterBy here? (probably yes, for consistency, but not used for Patterns)
//
//            QueryMethod.FILTER -> sequence {
//                query.queryFrom?.let { q ->
//                    yieldAll(queryNodes(q).filterBy(query))
//                }
//            }
//
//            QueryMethod.RELATED_RIGHT, QueryMethod.RELATED_LEFT -> sequence {
//                query.queryFrom?.let { q ->
//                    queryNodes(q).forEach { n ->
//                        n.getLabelsToRelationships(query.method.directionIsRight).let { ltr ->
//                            if (query.selectLabelName == null)
//                                ltr.values.forEach { rs -> yieldAll(rs) }
//                            else
//                                ltr[query.selectLabelName]?.let { rs -> yieldAll(rs) }
//                        }
//                    }
//                }
//            }.filterBy(query).map { it.directedTarget(query.method.directionIsRight) }
//
//            QueryMethod.CONCAT -> sequence {
//                query.queryFrom?.let { q -> yieldAll(queryNodes(q)) }
//                query.queryFrom2?.let { q -> yieldAll(queryNodes(q)) }
//            }.filterBy(query)
//        }
//
//
//    private fun <T:GraphableItem>Sequence<T>.filterBy(query: QueryInterface):Sequence<T> {
//        query.predicate?.let { p-> return this.filter(p) }
//        return this
//    }
//
//}
//
//
//
//
//
////fun <T:GraphItem>Sequence<T>.filterLabel(labelName:String?):Sequence<T> {
////    labelName?.let { return this.filter { it.labels.contains(labelName) } }
////    return this
////}
////
////fun <T:GraphItem>Sequence<T>.filterKeys(keys:List<String>?=null):Sequence<T> {
////    keys?.let { ks-> return this.filter { ks.contains(it.key) } }
////    return this
////}
////
////fun <T:GraphItem>Sequence<T>.filterProperties(properties:Map<String, Any?>?=null):Sequence<T> {
////    properties?.let { ps-> return this.filter { it.anyPropertyMatches(ps) } }
////    return this
////}
//
////fun Sequence<GraphRelationship>.filterProperties(properties:Map<String, Any?>?=null):Sequence<GraphRelationship> {
////    properties?.let { ps-> return this.filter { it.anyPropertyMatches(ps) } }
////    return this
////}
