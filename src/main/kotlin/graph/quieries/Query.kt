package graph.quieries

import graph.Item
import rain.graph.interfacing.*
import rain.language.Label
import rain.language.Node
import rain.language.Relationship
import rain.language.RelationshipLabel
import rain.utils.Caching
import javax.management.relation.Relation

typealias FilterPredicate = (Item)->Boolean


// ========================================================================

interface Query<FT: Item, T: Item> {

    fun asSequence(queryFrom: Sequence<FT>): Sequence<T>

    fun filter(predicate: FilterPredicate) = ConnectQuery(this, Filter(predicate))

    operator fun <QT:Item> times(query2:Query<T, QT>) = ConnectQuery(this, query2)

    operator fun plus(query2:Query<*, T>) = ConcatQuery(this, query2)

}

// ========================================================================

interface UpdatingQuery<FT: Item, T: Item>: Query<FT, T> {

    fun extend(queryFrom: Sequence<FT>, vararg items:T) {
        println("extend not implemented on $this")
    }

    fun clear(queryFrom: Sequence<FT>) {
        println("clear not implemented on $this")
    }

    fun deleteAll(queryFrom: Sequence<FT>) {
        println("deleteAll not implemented on $this")
    }
}

// ========================================================================

open class ConnectQuery<FT:Item, IT:Item,  T:Item>(
    val query1: Query<FT, IT>,
    val query2: Query<IT, T>,
): Query<FT, T> {
    override fun asSequence(queryFrom: Sequence<FT>): Sequence<T>
        = query2.asSequence(query1.asSequence(queryFrom))
}

// ========================================================================

open class ConcatQuery<FT:Item, FT2:Item,  T:Item>(
    val query1: Query<FT, T>,
    val query2: Query<FT2, T>,
): Query<FT, T> {
    override fun asSequence(queryFrom: Sequence<FT>): Sequence<T>
            = query1.asSequence(queryFrom) + query1.asSequence(queryFrom)
}


// ========================================================================
// TODO: is this used???
open class RelationshipQuery(
    val label: RelationshipLabel,
    val directionIsRight: Boolean = true
): Query<Node, Relationship> {

    override fun asSequence(queryFrom: Sequence<Node>): Sequence<Relationship> = sequence {
        queryFrom.forEach {
            yieldAll(it.getRelationships(label.labelName, directionIsRight))
        }
    }

    fun extendByNode(queryFrom: Sequence<Node>, vararg items:Node) {
        queryFrom.forEach { sourceNode->
            items.forEach {
                    targetNode ->
                sourceNode.relate(label, targetNode, directionIsRight)
            }
        }
    }

    fun clear(queryFrom: Sequence<Node>) {
        queryFrom.forEach {n->
            n.getRelationships(label.labelName, directionIsRight).forEach { r->
                r.delete()
            }
        }
    }


}

// ========================================================================

open class RelatedQuery(
    val label: RelationshipLabel,
    val directionIsRight: Boolean = true
): UpdatingQuery<Node, Node> {

    fun getRelationships(queryFrom: Sequence<Node>): Sequence<Relationship> = sequence {
        queryFrom.forEach {
            yieldAll(it.getRelationships(label.labelName, directionIsRight))
        }
    }

    override fun asSequence(queryFrom: Sequence<Node>): Sequence<Node> =
        getRelationships(queryFrom).map { it.directedTarget(directionIsRight) }

    override fun extend(queryFrom: Sequence<Node>, vararg items:Node) {
        queryFrom.forEach { sourceNode->
            items.forEach {
                targetNode ->
                sourceNode.relate(label, targetNode, directionIsRight)
            }
        }
    }

    override fun clear(queryFrom: Sequence<Node>) {
        queryFrom.forEach {n->
            n.getRelationships(label.labelName, directionIsRight).forEach { r->
                r.delete()
            }
        }
    }

    override fun deleteAll(queryFrom: Sequence<Node>) {
        asSequence(queryFrom).forEach { it.delete() }
    }
}

// ========================================================================

open class Filter<T: Item>(
    val predicate: FilterPredicate,
): Query<T, T> {

//    fun asSequence(queryFrom: Queryable<*>): Sequence<T>
    override fun asSequence(queryFrom: Sequence<T>): Sequence<T> =
        queryFrom.filter(predicate)
}

// ========================================================================



