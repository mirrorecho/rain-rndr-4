package rain.graph.quieries

import rain.graph.Item
import rain.graph.Label
import rain.graph.Node


interface Queryable<T: Item> {

    operator fun <QT: Item>get(query: Query<T, QT>) = QueryExecution(this, query)

    fun asSequence(): Sequence<T>

}

// ========================================================================

// extends Queryable interface with methods that apply to sequences of more than one item
interface QueryableMulti<T: Item>: Queryable<T> {

    fun filter(predicate: FilterPredicate) = QueryExecution(this, Filter(predicate))

    fun asKeys(): Sequence<String> = asSequence().map { it.key }

    fun indexOf(item: T): Int = asSequence().indexOf(item)

    fun indexOf(key:String): Int = asSequence().indexOfFirst {it.key==key}

    operator fun contains(item: T): Boolean = indexOf(item) > -1

    operator fun contains(key: String): Boolean = indexOf(key) > -1

    val first: T? get() = asSequence().firstOrNull()

    fun <T: Node>first(label: Label<*, T>): T? = first?.let { label.from(it) }

}

inline fun <T: Item> QueryableMulti<T>.forEach(block: (T)->Unit) =
    asSequence().forEach(block)

// ========================================================================




// ========================================================================

open class Select<T: Item>(
    private val selectSequence: Sequence<T>
): Queryable<T> {
    override fun asSequence(): Sequence<T> = selectSequence
}

// ========================================================================

fun <T: Item>Sequence<T>.asSelect(): Select<T> = Select(this)

fun <T: Item>Array<T>.asSelect(): Select<T> = Select(this.asSequence())

fun <T: Item>Iterable<T>.asSelect(): Select<T> = Select(this.asSequence())
