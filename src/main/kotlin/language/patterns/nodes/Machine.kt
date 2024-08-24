package rain.language.patterns.nodes

import org.openrndr.Program
import rain.language.Node
import rain.language.Label
import rain.language.patterns.Pattern


open class Machine protected constructor(
    key:String = rain.utils.autoKey(),
): Node(key) {
    abstract class MachineLabel<T:Machine>: Label<T>() {

    }

    companion object : MachineLabel<Machine>() {
        override val labelName:String = "Machine"
        override fun factory(key:String): Machine = Machine(key)
        init { registerMe() }
    }

    override val label: Label<out Machine> = Machine

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

