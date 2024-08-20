package rain.language

import rain.graph.interfacing.*

// TODO: is this interface even worth it?
interface Queryable {
    val context: Context
    val queryMe: Query

    fun filter(predicate: Filter) = Query(QueryMethod.FILTER, predicate=predicate, queryFrom=queryMe)

    // TODO: re-test!
    operator fun get(vararg queries: Query): Query {
        var returnQuery: Query = queryMe
        queries.forEach { it.rootQuery = returnQuery; returnQuery = it }
        return returnQuery
    }
}

