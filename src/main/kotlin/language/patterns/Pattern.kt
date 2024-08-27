package rain.language.patterns

import graph.quieries.Query
import graph.quieries.QueryMethod
import rain.graph.interfacing.GraphableNode
import rain.language.*
import rain.language.fields.Field

// patterns are abstractions of queries
abstract class Pattern<T:Node>(

    // TODO (DONE): consider making this a var to allow for patterns in the abstract
    // TODO: also, is source worthwhile here, or just override query's directly, OR, just make this a arg, not a var
    var source: T,
    val previous: Pattern<*>? = null,
//    val dimension: String? = null // TODO: consider whether to use these abstract dimensions (could be an enum)
    // val cascades: Bool = true // TODO: consider whether to implement to turn off cascading on a pattern by pattern basis
): Query( QueryMethod.GRAPHABLE) {
    // TODO: maybe timecodes (or other additive values)

    fun warningNotImplemented(attributeName: String) =
        println("WARNING: '$attributeName' not implemented for {$this}")

    // ------------------------------------------------------------------

    // graphableNodes MUST be overridden, as it defines the logic of the query
    override abstract val graphableNodes: Sequence<GraphableNode>

    // extend MAY be overridden
    open fun extend(vararg nodes: Node) = warningNotImplemented("extend")

    // deletes relationships and potentially intermediary nodes (and destination nodes if deleteNodes=true)
    open fun clear(deleteNodes: Boolean = false) = warningNotImplemented("clear")

    // see Node.get(fieldName:String) for more details
    // ... same as that, but with cascading values from previous
    operator fun <T:Any?>get(fieldName:String):T? =
        source[fieldName] ?: previous?.source?.get(fieldName)

    operator fun <T:Any?>get(field: Field<T>):T? = get(field.name)

    override var queryFrom: Query? = source.queryMe

    // TODO: this works great... so make sure I understand EXACTLY what's going on
    //  ... ALSO, consider moving to Query to use on non-patterns?
    // TODO: IMPORTANT: caching! (using Query TypedCache)
    open fun <P : Pattern<*>> asPatterns(
        factory: (source: Node, previous: Pattern<*>) -> P
    ): Sequence<P> = this().map { factory.invoke(it, this) }

    open fun <CT : Node, P : Pattern<CT>> asPatterns(
        label: Label<out CT>,
        factory: (source: CT, previous: Pattern<*>) -> P,
    ): Sequence<P> = this(label).map { factory.invoke(it, this) }

    // ------------------------------------------------------------------

    // TODO: Node Type OK here?
//    val history: HistoryPattern<ST, PT, *> get() = HistoryPattern<ST, PT, Node>(source, )

    val history: Sequence<Pattern<*>> = sequence {
        previous?.let { yield(it); yieldAll(it.history) }
    }

    open fun stream(name: String, nodesLabel: Label<*>, vararg values: Any?) {
        val dimensionIterator = this().iterator()
        val valuesIterator = values.iterator()
        while (valuesIterator.hasNext()) {
            if (dimensionIterator.hasNext()) {
                dimensionIterator.next().apply {
                    properties[name] = valuesIterator.next()
                    save()
                }
            } else {
                extend(
                    nodesLabel.create(properties = mapOf(name to valuesIterator.next()))
                )
            }
        }
    }

    // TODO: does this work??? Is it used? Naming?
    open fun setStream(name: String, vararg values: Any) {
        this().zip(values.asSequence()).forEach { it.first.properties[name] = it.second }
    }


    // TODO: review and remove (assume no longer worth it, now with field implementation)

//    val cachedTarget get() = CachedTarget()
//    inner class CachedTarget: TypedCached<NT>() {
//
//        private var cachedNode = this.first
//
//        val sourcePattern get() = this@Pattern
//        val sourceNode get() = sourcePattern.source
//
//        var target: NT?
//            get() = cachedNode
//            set(node) {
//                cachedNode = node
//                clear()
//                node?.let { extend(it) }
//            }
//
//        // TODO: is this used?
//        fun createIfMissing(key:String = autoKey()) {
//            if (cachedNode==null) {
//                cachedNode = destinationLabel.create(key).also { extend(it) }
//            }
//        }


    // replaced with ConnectedField class defined alongside Field...
    // TODO eventually: review and remove
//        inner class FieldValue<T:Any>(
//            val name:String,
//        ) {
//
//            var value: T? get() = this@CachedTarget.target?.properties?.get(name) as T?
//                set(value: T?) {
//                    this@CachedTarget.target!!.properties[name] = value
//                }
//
////            operator fun getValue(thisRef: Any?, property: KProperty<*>): T? =
////                this@CachedTarget.target?.properties?.get(name) as T?
////
////            operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {
////                this@CachedTarget.target!!.properties[name] = value
////            }
//
//            fun wireup(source:Node) {
//                this@CachedTarget.sourcePattern.source = source
//            }
//
//        }
//    }

}




