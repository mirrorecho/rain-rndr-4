package rain.language

import graph.Item
import rain.graph.interfacing.*
import rain.utils.Caching

typealias Filter = (Item)->Boolean

enum class QueryMethod(val directionIsRight: Boolean) {
    SELECT(true), // selects by label and/or keys, with an optional filter
    FILTER(true), // filters only
    CONCAT(true), // concatenates two queries, with an optional filter
    RELATED_RIGHT(true), // queries nodes connected as targets via relationships, optionally filtering the relationships
    RELATED_LEFT(false), // queries nodes connected as sources via relationships, optionally filtering the relationships
    GRAPHABLE(true); // selects by graphable interface (i.e. sub-queries must re-query by key)

    companion object {
        fun related(directionRight: Boolean=true) =
            if (directionRight) RELATED_RIGHT else RELATED_LEFT
    }

}

abstract class Query<T: Item> {
    abstract val label: Label<*, T>

    open operator fun <T: Item>invoke(label: Label<*, T>): Sequence<T>
            = invoke().map { label.from(it)!! }

    abstract operator fun invoke(): Sequence<T>


class SelectQuery<T: Item>(
    override val label: Label<*, T>,
    vararg val keys: String,
): Query<T>() {
    abstract operator fun invoke(): Sequence<T>
}


// TODO: consider re-making a QueryInterface
// TODO: is this simply a sub-class of (or extensions to), sequences of items?
open class QueryOld<T: Item>(
    val method: QueryMethod = QueryMethod.SELECT,
    val label: Label<*, T>,
    val selectKeys: Array<out String> = arrayOf(), // used only for SELECT queries
    val predicate: Filter? = null,

    var queryFrom: Query? = null,

    // for SELECT queries, this is not defined
    // for CONCAT queries, this is the 2nd of the queries to concat
    // for all other queries, this is an extension of the query // TODO: implement
    val queryFrom2: Query? = null,

//    override val context: Context = LocalContext,

    ): Queryable {

    override fun toString():String = "QUERY:$method - selectLabelName:$selectLabelName, selectKeys:$selectKeys, predicate?:${predicate!=null}, queryFrom?:${queryFrom!=null}, "

    override val queryMe get() = this

    // NOTE: this should be the only point of access with the actual graph...

    open operator fun <T: Item>invoke(label: Label<*, T>): Sequence<T>
        = invoke().map { label.from(it)!! }

    open operator fun invoke(): Sequence<T> =
        when (method) {
            QueryMethod.SELECT -> sequence {
                    if (selectKeys.isEmpty()) yieldAll(label.registry.values)
                    else yieldAll(selectKeys.mapNotNull { k-> label.registry[k] })
            }.filterBy(this)

            QueryMethod.GRAPHABLE -> sequence {
                yieldAll(graphableNodes.mapNotNull { nodeIndex[it.key] })
            }.filterBy(this) // TODO: worth keeping this filterBy here? (probably yes, for consistency, but not used for Patterns)

            QueryMethod.FILTER -> sequence {
                queryFrom?.let { q ->
                    yieldAll(queryNodes(q).filterBy(this))
                }
            }

            QueryMethod.RELATED_RIGHT, QueryMethod.RELATED_LEFT -> sequence {
                queryFrom?.let { q ->
                    queryNodes(q).forEach { n ->
                        n.getLabelsToRelationships(query.method.directionIsRight).let { ltr ->
                            if (query.selectLabelName == null)
                                ltr.values.forEach { rs -> yieldAll(rs) }
                            else
                                ltr[selectLabelName]?.let { rs -> yieldAll(rs) }
                        }
                    }
                }
            }.filterBy(this).map { it.directedTarget(method.directionIsRight) }

            QueryMethod.CONCAT -> sequence {
                query.queryFrom?.let { q -> yieldAll(queryNodes(q)) }
                query.queryFrom2?.let { q -> yieldAll(queryNodes(q)) }
            }.filterBy(this)
        }


    private fun <T:Item>Sequence<T>.filterBy(query: Query<T>):Sequence<T> {
        query.predicate?.let { p-> return this.filter(p) }
        return this
    }

    inline fun forEach(block: (Node)->Unit) = invoke().forEach(block)

    fun asKeys(): Sequence<String> = graphableNodes.map { it.key }

    fun indexOfFirst(key:String): Int = graphableNodes.indexOfFirst {it.key==key}

    fun contains(key: String): Boolean = this.indexOfFirst(key) > -1

    fun <T: Node>first(label: Label<T>): T? = this(label).firstOrNull()

    val first: Node? get() = this().firstOrNull()

    operator fun plus(concatQuery: Query) =
        Query(QueryMethod.CONCAT, queryFrom=this, queryFrom2 = concatQuery)

    var rootQuery: Query
        get() = queryFrom?.rootQuery ?: this
        set(query) { rootQuery.queryFrom = query }


    open inner class TypedCached<T: Node>(
        val label: Label<T>? = null
    ) {
        open fun getSequence(): Sequence<T> = invoke(label!!)

        private val cachingNodes: Caching<Sequence<T>> = Caching(::getSequence)

        val nodes: Sequence<T> by cachingNodes

        val first: T? get() = nodes.firstOrNull()

        fun reset() {
            cachingNodes.reset()
        }
    }

//    open inner class CachedTarget<T: Node>(
//        val label: NodeLabel<T>? = null
//    ) {
//        open fun getSequence(): Sequence<T> = invoke(label!!)
//
//        private val cachingNodes: Caching<Sequence<T>> = Caching(::getSequence)
//
//        val nodes: Sequence<T> by cachingNodes
//
//        val first: T? get() = nodes.firstOrNull()
//
//        fun reset() {
//            cachingNodes.reset()
//        }
//    }


    // TODO: is this even used? (and does the override work out correctly?)
    inner class Cached(): TypedCached<Node>() {
        override fun getSequence(): Sequence<Node> = invoke()
    }


}

// TODO: implement ability to create  query from any sequence of Nodes (and Patterns?)

