package rain.language.patterns.nodes

import org.openrndr.Program
import rain.language.Node
import rain.language.Label
import rain.language.NodeLabel
import rain.language.patterns.Pattern
import rain.utils.autoKey



open class Machine protected constructor(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<Node, Machine>(
        null, Machine::class, { k-> Machine(k) }
    )
    override val label: Label<out Node, out Machine> = Machine


    // TODO is this used?
    open fun gate(onOff: Boolean) {
        isRunning = onOff;
    }

    open fun render(program: Program) { println("render not implemented for $this") }

    // TODO: is this even used?
    protected var isRunning = false

    // TODO: make this universal?
    open fun bump(pattern:Pattern<Event>) {
        // TODO: implement?
        println("Bumping $key: $properties - WARNING: no bump defined")
    }

}

// =======================================================================

