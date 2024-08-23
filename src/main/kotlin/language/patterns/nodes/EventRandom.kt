package language.patterns.nodes

import rain.language.*
import rain.language.fields.*
import rain.language.patterns.nodes.*
import rain.utils.autoKey


open class EventRandom protected constructor(
    key:String = autoKey(),
): Event(key) {

    abstract class EventRandomLabel<T : EventRandom> : EventLabel<T>() {
        val times = field("times", 1, false)
    }

    companion object : EventRandomLabel<EventRandom>() {
        override val labelName: String = "EventRandom"
        override fun factory(key: String) = EventRandom(key)

        init {
            registerMe()
        }
    }

    override val label: NodeLabel<out EventRandom> = EventRandom

    var times by attach(EventRandom.times)

    val childrenToRandomize by lazy { super.children.toList() }

    override val children get() = sequence {
        yieldAll((0..<times).map { _ ->
            println("Getting a random child of an event with dur: ${this@EventRandom[Event.dur]}")
            childrenToRandomize.random()
        }.asSequence())
    }

}