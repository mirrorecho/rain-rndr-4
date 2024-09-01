package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import rain.utils.*

import rain.language.*
import rain.language.patterns.nodes.Colorable
import rain.language.patterns.nodes.Machine
import rain.rndr.relationships.*

open class Color(
    key:String = autoKey(),
) : Colorable, Machine(key) {
    companion object : NodeLabel<Machine, Color>(
        Machine, Color::class, { k -> Color(k) }
    )

    override val label: Label<out Machine, out Color> = Color

    class ColorAnimation: Animatable() {
        var h = 0.0
        var s = 0.0
        var v = 0.0
        var a = 1.0
    }

    val colorAnimation = ColorAnimation()

    override var h by LinkablePropertySlot(colorAnimation::h, +H)
    override var s by LinkablePropertySlot(colorAnimation::s, +S)
    override var v by LinkablePropertySlot(colorAnimation::v, +V)
    override var a by LinkablePropertySlot(colorAnimation::a, +A)


    override fun render(program: Program) {
        colorAnimation.updateAnimation()
    }

}


