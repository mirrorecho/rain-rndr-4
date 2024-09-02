package rain.graph.quieries

import rain.graph.Item
import rain.graph.Label
import rain.graph.Node
import rain.graph.NodeLabel

open class QueryExecution<FT: Item, T: Item>(
    val queryable: Queryable<FT>,
    open val query: Query<FT, T>,
): QueryableMulti<T> {

    override fun asSequence(): Sequence<T>
            = query.asSequence(queryable.asSequence())

    operator fun invoke(): Sequence<T> = asSequence()

    operator fun <ET: Item>invoke(label: Label<*, ET>): Sequence<ET>
            = asSequence().map { label.from(it)!! }

    // TODO maybe: use this? (assume just as easy to + the sequences)
//    operator fun plus(queryExecution2:QueryExecution<*, T>) =
//        invoke() + queryExecution2()

    // TODO maybe: caching? (only worthwhile for more complex queries/filters)
    //  ... (simple relationship queries have to iterate over very few or no rain.score.nodes)
//        private val cachingItems: Caching<List<T>> = Caching { invoke().toList() }

}

// ========================================================================

open class UpdatingQueryExecution<FT: Item, T: Item>(
    queryable: Queryable<FT>,
    override val query: UpdatingQuery<FT, T>,
): QueryExecution<FT, T>(queryable, query) {

    fun extend(vararg items:T) {
        query.extend(queryable.asSequence(), *items)
    }

    fun clear() {
        query.clear(queryable.asSequence())
    }

    fun deleteAll() {
        query.deleteAll(queryable.asSequence())
    }

}

// TODO: bring back only if needed

//// ========================================================================
//// NOTE: complements (doesn't inherit from) QueryExecution
//// due to generics inheritance problems with query val,
//open class QueriedNode<T: Node>(
//    source: Node,
//    query: Query<Node, Node>,
//    val label: Label<*, T>,
//) {
//
//    private val myQueryExecution = source[ query ]
//
//    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? =
//        myQueryExecution(label).firstOrNull()
//}
//
//// ========================================================================
//class RelatedNode<T: Node>(
//    val source: Node,
//    val query: RelatedQuery,
//    label: Label<*, T>,
//): QueriedNode<T>(source, query, label) {
//
//    operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {
//        query.clear(source.asSequence())
//        query.extend(source.asSequence(), value)
//    }
//}

// ========================================================================

// TODO: naming?
open class Pattern<FT: Node>(
    val source: FT,
    override val query: UpdatingQuery<Node, Node>,
    val previous: Pattern<*>? = null
): UpdatingQueryExecution<Node, Node>(source, query) {

    open fun asPatterns(query: UpdatingQuery<Node, Node>): Sequence<Pattern<*>> =
        asSequence().map { Pattern(it, query, this) }

    open fun <T: Node> asPatterns(
        label: NodeLabel<*, out T>,
        query: UpdatingQuery<Node, Node>,
    ): Sequence<Pattern<T>> =
        asSequence().map { Pattern(label.from(it)!!, query, this) }

    fun <T:Any?> cascadingDataSlot(name:String): Node.DataSlot<T>? =
        source.slot(name) ?: previous?.source?.slot(name)


}