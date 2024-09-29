package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.nodes.*

class Rectangle private constructor(
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

    override val animation: RectangleAnimation = RectangleAnimation()

    override var x by RespondingPropertySlot(animation::x, +X)
    override var y by RespondingPropertySlot(animation::y, +Y)

    var width by RespondingPropertySlot(animation::width, +WIDTH)
    var height by RespondingPropertySlot(animation::height, +WIDTH)

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

