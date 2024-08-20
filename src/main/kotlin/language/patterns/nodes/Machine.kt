package rain.language.patterns.nodes

import org.openrndr.Program
import rain.language.Node
import rain.language.NodeLabel
import rain.language.fields.field
import rain.language.patterns.Pattern


open class Machine protected constructor(
    key:String = rain.utils.autoKey(),
): Node(key) {
    abstract class MachineLabel<T:Machine>: NodeLabel<T>() {

    }

    companion object : MachineLabel<Machine>() {
        override val labelName:String = "Machine"
        override fun factory(key:String): Machine = Machine(key)
    }

    override val label: NodeLabel<out Machine> = Machine

    open fun gate(onOff: Boolean) {
        isRunning = onOff;
    }

    open fun render(program: Program) { println("render not implemented for $this") }

    // TODO: is this even used?
    protected var isRunning = false

    // TODO: make this uni
    open fun bump(pattern:Pattern<Event>) {
        // TODO: implement?
        println("Bumping $key: $properties - WARNING: no bump defined")
    }

}

// =======================================================================

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
    }

    override val label: NodeLabel<out Printer> = Printer

    var msg by attach(Printer.msg)
    var renderMe by attach(Printer.renderMe)

    override fun bump(pattern:Pattern<Event>) {
        updateAllFieldsFrom(pattern.source)
        // msg = pattern[Printer.msg] // or, this is how to update field by field (if not updating all)
    }

    override fun render(program: Program) {
        if (renderMe) {
            println(msg)
            renderMe = false // set back to false to prevent endless messages
        }
    }

}

