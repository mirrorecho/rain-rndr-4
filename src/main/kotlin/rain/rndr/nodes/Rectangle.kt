package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import rain.graph.Label
import rain.graph.NodeLabel
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

    override var x by RespondingPropertySlot(rectangleAnimation::x, +X)
    override var y by RespondingPropertySlot(rectangleAnimation::y, +Y)

    var width by RespondingPropertySlot(rectangleAnimation::width, +WIDTH)
    var height by RespondingPropertySlot(rectangleAnimation::height, +WIDTH)

    override fun render(context: Score.ScoreContext) {
        context.applyDrawing {
            rectangle(
                vector(context),
                this@Rectangle.width * context.unitLength,
                this@Rectangle.height * context.unitLength
            )
        }
    }

}

