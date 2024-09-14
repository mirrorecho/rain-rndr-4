package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import rain.graph.Label
import rain.graph.NodeLabel
import rain.utils.*

import rain.rndr.relationships.A
import rain.rndr.relationships.H
import rain.rndr.relationships.S
import rain.rndr.relationships.V
import rain.score.Score
import rain.score.ScoreContext
import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation

interface Colorable {
    var h: Double
    var s: Double
    var v: Double
    var a: Double

    fun colorHSVa() = ColorHSVa(h, s, v, a)

    fun colorRGBa(): ColorRGBa = colorHSVa().toRGBa()

}

open class Color(
    key:String = autoKey(),
) : Colorable, Machine(key) {
    companion object : NodeLabel<Machine, Color>(
        Machine, Color::class, { k -> Color(k) }
    ) {
        fun black() = Color.create {
            h = 0.0;
            s = 0.0;
            v = 0.0;
            a = 1.0;
        }
        fun white() = Color.create {
            h = 0.0;
            s = 0.0;
            v = 1.0;
            a = 1.0;
        }
        fun fromRGBa(color:ColorRGBa) = Color.create {
            color.toHSVa().let {
                h = it.h
                s = it.s
                v = it.v
                a = it.alpha
            }
        }
    }

    override val label: Label<out Machine, out Color> = Color

    class ColorAnimation: MachineAnimation() {
        var h = 220.0
        var s = 0.2
        var v = 0.9
        var a = 0.0 // starting at 0 to avoid "flashes" at startup. Can that be avoided by some other means?
    }

    val colorAnimation = ColorAnimation()
    override val machineAnimation = colorAnimation

    override var h by LinkablePropertySlot(colorAnimation::h, +H)
    override var s by LinkablePropertySlot(colorAnimation::s, +S)
    override var v by LinkablePropertySlot(colorAnimation::v, +V)
    override var a by LinkablePropertySlot(colorAnimation::a, +H)


    override fun render(context: ScoreContext) {
        colorAnimation.updateAnimation()
    }

}


