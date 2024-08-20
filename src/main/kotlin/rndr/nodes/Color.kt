package rain.rndr.nodes

import rain.utils.*

import org.openrndr.color.ColorHSVa
import org.openrndr.color.ColorRGBa
import rain.language.*
import rain.language.fields.field
import rain.language.patterns.nodes.Machine
import rain.rndr.relationships.*

open class Color(
    key:String = autoKey(),
): Machine(key) {

    abstract class ColorLabel<T:Color>: MachineLabel<T>() {
        val h = field("h", H, 0.0)
        val s = field("s", S, 0.9)
        val v = field("v", V, 0.9)
        val a = field("a", A, 0.9)
    }

    companion object : ColorLabel<Color>() {
        override val labelName:String = "Color"
        override fun factory(key:String) = Color(key)

        val WHITE = Color.create("COLOR_WHITE")
    }

    override val label: NodeLabel<out Color> = Color

    val h by attach(Color.h)
    val s by attach(Color.s)
    val v by attach(Color.v)
    val a by attach(Color.a)

    fun colorHSVa() = ColorHSVa(h, s, v, a)

    fun colorRGBa(): ColorRGBa = colorHSVa().toRGBa()

}


