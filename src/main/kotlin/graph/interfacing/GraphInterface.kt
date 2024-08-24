package rain.graph.interfacing

import rain.graph.Graph
import rain.graph.GraphNode
import rain.language.Query

typealias LabelDirected = Pair<String, Boolean?>


//interface GraphInterface {
//
//    // NOTE: this was a dunder method in python implementation
//    // TODO: needed?
//    fun contains(key: String): Boolean
//
//    // NOTE: this was named exists in python implementation
//    fun contains(node:GraphableNode): Boolean
//
//    fun contains(relationship:GraphableRelationship): Boolean
//
//    fun create(node:GraphableNode)
//
//    fun create(relationship:GraphableRelationship)
//
//    fun merge(node:GraphableNode)
//
//    fun merge(relationship:GraphableRelationship)
//
//    fun read(node:GraphableNode)
//
//    fun read(relationship:GraphableRelationship)
//
//    fun getNode(key:String): GraphableNode
//
//    fun getRelationship(key:String): GraphableRelationship
//
//    fun getRelationships(nodeKey:String, relationshipLabel:String, directionIsRight:Boolean=true): Set<GraphableRelationship>
//
////    fun getRelationships(node:GraphableNode, relationshipLabel:String, directionIsRight:Boolean=true): Set<GraphableRelationship>
//
//    fun save(node:GraphableNode)
//
//    fun save(relationship:GraphableRelationship)
//
//    fun deleteNode(key: String)
//
//    fun deleteRelationship(key: String)
//
//    fun queryNodes(query: QueryInterface): Sequence<GraphNode>
//
//}
//
//fun localGraph(): GraphInterface = Graph()
//
