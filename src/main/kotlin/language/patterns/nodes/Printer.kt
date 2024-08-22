package language.patterns.nodes

import org.openrndr.Program
import rain.language.NodeLabel
import rain.language.fields.field
import rain.language.patterns.Pattern
import rain.language.patterns.nodes.Event
import rain.language.patterns.nodes.Machine


// a simple machine for demo/testing purposes..
open class Printer(
    key:String = rain.utils.autoKey(),
): Machine(key) {
    abstract class PrinterLabel<T: Printer>(): MachineLabel<T>() {
        val msg = field("msg", "NO MESSAGE DEFINED")
        val renderMe = field("renderMe", false)
    }

    companion object : PrinterLabel<Printer>() {
        override val parent = Machine
        override val labelName:String = "Printer"
        override fun factory(key:String): Printer = Printer(key)
        init { registerMe() }
    }

    override val label: NodeLabel<out Printer> = Printer

    var msg by attach(Printer.msg)
    var renderMe by attach(Printer.renderMe)

    override fun bump(pattern: Pattern<Event>) {
//        println("bumping: $this with $pattern")

        // updates all fields:
        // updateAllFieldsFrom(pattern)

        // or, this is how to update field by field (if not updating all):
        updateFieldsFrom(pattern, ::msg, ::renderMe)
    }

    override fun render(program: Program) {
//        println(msg)
        if (renderMe) {
            println(msg)
            renderMe = false // set back to false to prevent endless messages
        }
    }

}