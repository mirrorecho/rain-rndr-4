package rain.score.nodes

import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.queries.Pattern
import rain.language.patterns.CUED_CHILDREN_QUERY
import rain.utils.autoKey


open class EventRandom protected constructor(
    key:String = autoKey(),
): Event(key)  {
    companion object : NodeLabel<Event, EventRandom>(
        Event, EventRandom::class, { k -> EventRandom(k) }
    )

    override val label: Label<out Event, out EventRandom> = EventRandom

    var times by DataSlot("times", 2)

    fun childrenToRandomize(previous: Pattern<Event>?) =
        childrenPattern(previous).asPatterns(Event, CUED_CHILDREN_QUERY).toList()

    override fun children(previous: Pattern<Event>?) = sequence {
        val myChildrenToRandomize = childrenToRandomize(previous)
        yieldAll((0..<times).map { _ ->
//            println("Getting a random child of an event with dur: ${this@EventRandom.dur}")
            myChildrenToRandomize.random()
        }.asSequence())
    }

}