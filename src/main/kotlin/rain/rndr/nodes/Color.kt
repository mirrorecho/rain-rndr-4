package rain.rndr.nodes

import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import rain.graph.Label
import rain.graph.NodeLabel
import rain.rndr.relationships.*
import rain.utils.*

import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation
import rain.score.nodes.Score


open class Color(
    key:String = autoKey(),
) : Machine(key) {
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

    var h by SummingPropertySlot(colorAnimation::h, +H, true)
    var s by SummingPropertySlot(colorAnimation::s, +S, true)
    var v by SummingPropertySlot(colorAnimation::v, +V, true)
    var a by SummingPropertySlot(colorAnimation::a, +A, true)

    fun colorHSVa() = ColorHSVa(h, s, v, a)

    override fun refresh() {
        // we need to update the cached myColorRGBa
        // and similarly any related draw styles
        // (if they don't already have animations)
        myColorRGBa = colorHSVa().toRGBa()
//        this[-FILL_COLOR](DrawStyle).forEach { ds ->
//            ds.rndrDrawStyle.fill = myColorRGBa
//        }
//        this[-STROKE_COLOR](DrawStyle).forEach { ds ->
//            ds.rndrDrawStyle.stroke = myColorRGBa
//        }
    }

    override val hasPlaybackCaching: Boolean = true

    override fun refresh(context: Score.ScoreContext) {
        refresh()
    }

    private var myColorRGBa: ColorRGBa = colorHSVa().toRGBa()
    val colorRGBa: ColorRGBa get() = myColorRGBa

}


