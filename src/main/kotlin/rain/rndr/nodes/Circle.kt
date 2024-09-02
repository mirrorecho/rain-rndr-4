package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.color.ColorHSVa
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.quieries.Pattern
import rain.score.nodes.*

open class Circle protected constructor(
    key:String = autoKey(),
) : Colorable, Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Circle>(
        Machine, Circle::class, { k -> Circle(k) }
    )

    override val label: Label<out Machine, out Circle> = Circle

    class CircleAnimation: MachineAnimation() {
        var radius = 90.0

        var strokeWeight = 0.9

        var x = 0.5
        var y = 0.5

        // for fillColor
        var h = 0.0
        var s = 0.0
        var v = 0.0
        var a = 1.0
    }

    val circleAnimation = CircleAnimation()
    override val machineAnimation = circleAnimation

    var radius by LinkablePropertySlot(circleAnimation::radius, +RADIUS)

    var strokeWeight by LinkablePropertySlot(circleAnimation::strokeWeight, +STROKE_WEIGHT)

    override var x by LinkablePropertySlot(circleAnimation::x, +X)
    override var y by LinkablePropertySlot(circleAnimation::y, +Y)

    override var h by LinkablePropertySlot(circleAnimation::h, +H)
    override var s by LinkablePropertySlot(circleAnimation::s, +S)
    override var v by LinkablePropertySlot(circleAnimation::v, +V)
    override var a by LinkablePropertySlot(circleAnimation::a, +A)

    val strokeColor by RelatedNodeSlot("strokeColor", +STROKE_COLOR, Color, null)


    //    // TODO: implement if needed (or remove)
    override fun bump(pattern: Pattern<Event>) {
//        updateAllFieldsFrom(pattern)
    }

    override fun render(program: Program) {
//        println("circle with x position " + position.x().toString())
        program.apply {
            drawer.fill = ColorHSVa(h, s, v, a).toRGBa()
            drawer.stroke = strokeColor?.colorRGBa()
            drawer.strokeWeight = strokeWeight
            drawer.circle(
                position = vector(this),
                radius
            )
        }
    }

}

