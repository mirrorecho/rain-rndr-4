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

    private var localX: Double = 0.0
    private var localY: Double = 0.0
    private var localWidth: Double = 0.0
    private var localHeight: Double = 0.0

    override var x by RespondingPropertySlot("x", ::localX, +X)
    override var y by RespondingPropertySlot("y", ::localY, +Y)

    var width by RespondingPropertySlot("width", ::localWidth, +WIDTH)
    var height by RespondingPropertySlot("height", ::localHeight, +WIDTH)

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

