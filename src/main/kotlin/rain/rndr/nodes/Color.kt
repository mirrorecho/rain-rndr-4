package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import rain.graph.Label
import rain.graph.NodeLabel
import rain.utils.*

import rain.rndr.relationships.A
import rain.rndr.relationships.H
import rain.rndr.relationships.S
import rain.rndr.relationships.V
import rain.score.nodes.Colorable
import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation

open class Color(
    key:String = autoKey(),
) : Colorable, Machine(key) {
    companion object : NodeLabel<Machine, Color>(
        Machine, Color::class, { k -> Color(k) }
    )

    override val label: Label<out Machine, out Color> = Color

    class ColorAnimation: MachineAnimation() {
        var h = 0.0
        var s = 0.0
        var v = 0.0
        var a = 1.0
    }

    val colorAnimation = ColorAnimation()
    override val machineAnimation = colorAnimation

    override var h by LinkablePropertySlot(colorAnimation::h, +H)
    override var s by LinkablePropertySlot(colorAnimation::s, +S)
    override var v by LinkablePropertySlot(colorAnimation::v, +V)
    override var a by LinkablePropertySlot(colorAnimation::a, +A)


    override fun render(program: Program) {
        colorAnimation.updateAnimation()
    }

}


