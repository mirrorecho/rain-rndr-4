package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.nodes.*

open class Circle protected constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Circle>(
        Machine, Circle::class, { k -> Circle(k) }
    )

    override val label: Label<out Machine, out Circle> = Circle

    private var localX: Double = 0.0
    private var localY: Double = 0.0
    private var localRadius: Double = 1.0

    override var x by RespondingPropertySlot("x", ::localX, +X)
    override var y by RespondingPropertySlot("y", ::localY, +Y)
    var radius by RespondingPropertySlot("radius", ::localRadius, +RADIUS)

    override fun render(context: Score.ScoreContext) {

        context.applyDrawing {
            circle(
                position = vector(context),
                radius * context.score.unitLength)
        }
    }

}

