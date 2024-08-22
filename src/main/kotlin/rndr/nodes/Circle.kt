package rain.rndr.nodes

import rain.language.patterns.nodes.*
import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.color.ColorHSVa
import rain.language.fields.field
import rain.language.fields.fieldOfNode
import rain.language.patterns.Pattern

open class Circle protected constructor(
    key:String = autoKey(),
): Machine(key) {

    abstract class CircleLabel<T:Circle>(): MachineLabel<T>() {
        val radius = field("radius", RADIUS, 90.0)
        val position = fieldOfNode("position", POSITION, Position, Position.CENTER)

        // TODO: an easy copy when all the params for the relationship, etc., are the same for the field
        val x = field("x", X, 0.5)
        val y = field("y", Y, 0.5)

        val strokeColor = fieldOfNode("strokeColor", STROKE_COLOR, Color, null)
        val strokeWeight = field("strokeWeight", STROKE_WEIGHT, 0.9)
//        val fillColor = field("fillColor", FILL_COLOR, Color)
//        TODO: maybe: implement these

        // hue would be proxy for whether entire color is null or not
        val h = field<Double?>("h", H, null)
        val s = field("s", S, 0.9)
        val v = field("v", V, 0.9)
        val a = field("a", A, 0.8)
    }

    companion object : CircleLabel<Circle>() {
        override val parent = Machine
        override val labelName:String = "Circle"
        override fun factory(key:String): Circle = Circle(key)
        init { registerMe() }
    }

    override val label = Circle

    var radius by attach(Circle.radius)
    var position by attach(Circle.position)
    var strokeColor by attach(Circle.strokeColor)
    var strokeWeight by attach(Circle.strokeWeight)
//    val fillColor by attachField(Circle.fillColor) // NOTE: not needed since we're also using h,s,v,a explicitly

    var h by attach(Circle.h)
    var s by attach(Circle.s)
    var v by attach(Circle.v)
    var a by attach(Circle.a)

    //    // TODO: implement if needed (or remove)
    override fun bump(pattern: Pattern<Event>) {
        updateAllFieldsFrom(pattern)
    }

    override fun render(program: Program) {
//        println("circle with x position " + position.x().toString())
        program.apply {
            drawer.fill = h?.let { ColorHSVa(it, s, v, a) }?.toRGBa()
            drawer.stroke = strokeColor?.colorRGBa()
            drawer.strokeWeight = strokeWeight
            drawer.circle(
                position = position.vector(program),
                radius
            )
        }
    }

}

