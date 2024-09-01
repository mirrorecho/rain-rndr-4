package rain.language.patterns.nodes

import org.openrndr.Program
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import org.openrndr.math.Vector2
import rain.language.Node
import rain.language.Label
import rain.language.NodeLabel
import rain.language.patterns.Pattern
import rain.utils.autoKey


interface Colorable {
    var h: Double
    var s: Double
    var v: Double
    var a: Double

    fun colorHSVa() = ColorHSVa(h, s, v, a)

    fun colorRGBa(): ColorRGBa = colorHSVa().toRGBa()

}

interface Positionable {
    var x: Double
    var y: Double

    fun vector(program: Program): Vector2 = Vector2(
        x * program.width,
        y * program.height,
    )

}

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

