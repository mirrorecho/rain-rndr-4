package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.color.ColorHSVa
import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.ScoreContext
import rain.score.nodes.*

open class Rectangle protected constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Rectangle>(
        Machine, Rectangle::class, { k -> Rectangle(k) }
    )

    override val label: Label<out Machine, out Rectangle> = Rectangle

    class RectangleAnimation: MachineAnimation() {

        var x = 0.0
        var y = 0.0

        var width = 4.0
        var height = 4.0

    }

    val rectangleAnimation = RectangleAnimation()
    override val machineAnimation = rectangleAnimation

    override var x by SummingPropertySlot(rectangleAnimation::x, +X)
    override var y by SummingPropertySlot(rectangleAnimation::y, +Y)

    var width by SummingPropertySlot(rectangleAnimation::width, +WIDTH)
    var height by SummingPropertySlot(rectangleAnimation::height, +WIDTH)

    override fun render(context: ScoreContext) {
        context.applyDrawing {
            rectangle(
                vector(context),
                this@Rectangle.width * context.unitLength,
                this@Rectangle.height * context.unitLength
            )
        }
    }

}

