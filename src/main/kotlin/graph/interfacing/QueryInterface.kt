package rain.graph.interfacing

typealias Filter = (GraphableItem)->Boolean



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

interface QueryInterface {
    val method: QueryMethod
    val selectLabelName: String? // used only for SELECT or RELATED_ queries
    val selectKeys: Array<out String> // used only for SELECT queries
    val predicate: Filter?
    val queryFrom: QueryInterface?
    val queryFrom2: QueryInterface?

    val graphableNodes: Sequence<GraphableNode>

}