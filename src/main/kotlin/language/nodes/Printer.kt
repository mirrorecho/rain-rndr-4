package language.nodes

import org.openrndr.Program
import rain.language.Label
import rain.language.NodeLabel
import rain.language.Thingy
import rain.language.fields.field
import rain.language.patterns.Pattern
import rain.language.patterns.nodes.Event
import rain.language.patterns.nodes.Machine
import rain.utils.autoKey


// a simple machine for demo/testing purposes..
open class Printer(
    key: String = rain.utils.autoKey(),
) : Machine(key) {
    companion object : NodeLabel<Machine, Printer>(
        Machine, Printer::class, { k -> Printer(k) }
    )

    override val label: Label<out Machine, out Printer> = Printer


//    var msg by attach(Printer.msg)
//    var renderMe by attach(Printer.renderMe)

    override fun bump(pattern: Pattern<Event>) {
//        println("bumping: $this with $pattern")

        // updates all fields:
        // updateAllFieldsFrom(pattern)

        // or, this is how to update field by field (if not updating all):
//        updateFieldsFrom(pattern, ::msg, ::renderMe)
    }

    override fun render(program: Program) {
//        println(msg)
//        if (renderMe) {
//            println(msg)
//            renderMe = false // set back to false to prevent endless messages
//        }
    }

}
