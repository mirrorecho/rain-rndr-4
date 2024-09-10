package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.color.ColorHSVa
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.queries.Pattern
import rain.score.Score
import rain.score.nodes.*

open class Rectangle protected constructor(
    key:String = autoKey(),
) : Colorable, Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Rectangle>(
        Machine, Rectangle::class, { k -> Rectangle(k) }
    )

    override val label: Label<out Machine, out Rectangle> = Rectangle

    class RectangleAnimation: MachineAnimation() {

        var x = 0.0
        var y = 0.0

        var width = 4.0
        var height = 4.0

        // for fillColor
        var h = 0.0
        var s = 0.9
        var v = 0.9
        var a = 0.0 // set to 0.0 to avoid "flashes" // TODO: more elegant way to solve this?

        var strokeWeight = 0.0

    }

    val rectangleAnimation = RectangleAnimation()
    override val machineAnimation = rectangleAnimation

    // TODO: allow this to cascade
    override var fromPosition by RelatedNodeSlot("fromPosition", +FROM_POSITION, Position, null)

    override var x by LinkablePropertySlot(rectangleAnimation::x, +X)
    override var y by LinkablePropertySlot(rectangleAnimation::y, +Y)

    var width by LinkablePropertySlot(rectangleAnimation::width, +WIDTH)
    var height by LinkablePropertySlot(rectangleAnimation::height, +HEIGHT)

    override var h by LinkablePropertySlot(rectangleAnimation::h, +H)
    override var s by LinkablePropertySlot(rectangleAnimation::s, +S)
    override var v by LinkablePropertySlot(rectangleAnimation::v, +V)
    override var a by LinkablePropertySlot(rectangleAnimation::a, +A)

    // TODO: relate to score unites (make a Strokable interface?)
    var strokeWeight by LinkablePropertySlot(rectangleAnimation::strokeWeight, +STROKE_WEIGHT)
    val strokeColor by RelatedNodeSlot("strokeColor", +STROKE_COLOR, Color, null)


    //    // TODO: implement if needed (or remove)
    override fun bump(pattern: Pattern<Event>) {
        updateAllSlotsFrom(pattern.source)
    }

    override fun render(score: Score) {
//        println("circle with x position " + position.x().toString())
        rectangleAnimation.updateAnimation()
        score.applyProgram {
            drawer.fill = colorRGBa()
            drawer.stroke = strokeColor?.colorRGBa()
            drawer.strokeWeight = strokeWeight
            drawer.rectangle(
                vector(score),
                this@Rectangle.width * score.unitLength,
                this@Rectangle.height * score.unitLength
            )
        }
    }

}

