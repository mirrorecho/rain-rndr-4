package rain.score.nodes

import rain.graph.Label
import rain.graph.NodeLabel
import rain.utils.autoKey


open class EventRandom protected constructor(
    key:String = autoKey(),
): Event(key)  {
    companion object : NodeLabel<Event, EventRandom>(
        Event, EventRandom::class, { k -> EventRandom(k) }
    )

    override val label: Label<out Event, out EventRandom> = EventRandom

    var times by DataSlot("times", 2)

    val childrenToRandomize by lazy { super.children.toList() }

    override val children get() = sequence {
        yieldAll((0..<times).map { _ ->
            println("Getting a random child of an event with dur: ${this@EventRandom.dur}")
            childrenToRandomize.random()
        }.asSequence())
    }

}