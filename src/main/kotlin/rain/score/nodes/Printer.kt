package rain.score.nodes

import org.openrndr.Program
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.queries.Pattern
import rain.score.Score


// a simple machine for demo/testing purposes..
open class Printer(
    key: String = rain.utils.autoKey(),
) : Machine(key) {
    companion object : NodeLabel<Machine, Printer>(
        Machine, Printer::class, { k -> Printer(k) }
    )

    override val label: Label<out Machine, out Printer> = Printer

    var msg by DataSlot("msg", "Default Message")
    var renderMe by DataSlot("renderMe", false)



    override fun bump(pattern: Pattern<Event>) {
//        println("bumping: $this with $pattern")

        // updates all fields:
//        updateAllSlotsFrom(pattern.source)
        updateSlotFrom<String>("msg", pattern.source)
        updateSlotFrom<Boolean>("renderMe", pattern.source)

//        println("Slot msg from event pattern: ${ pattern.source.slot<String>("msg")?.value }")

        // or, this is how to update field by field (if not updating all):
//        updateFieldsFrom(pattern, ::msg, ::renderMe)
    }

    override fun render(score: Score) {
//        println(msg)
        if (renderMe) {
            println("${this.key} PRINTING: $msg")
            renderMe = false // set back to false to prevent endless messages
        }
    }

}
