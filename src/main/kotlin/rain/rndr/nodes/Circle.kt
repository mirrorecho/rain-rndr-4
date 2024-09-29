package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.nodes.*

class Circle private constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Circle>(
        Machine, Circle::class, { k -> Circle(k) }
    )

    override val label: Label<out Machine, out Circle> = Circle

    class CircleAnimation: MachineAnimation() {
        var radius = 4.0

        var x = 0.0
        var y = 0.0

    }

    override val animation: CircleAnimation = CircleAnimation()

    var radius by RespondingPropertySlot(animation::radius, +RADIUS)

    override var x by RespondingPropertySlot(animation::x, +X)
    override var y by RespondingPropertySlot(animation::y, +Y)

    override fun render(context: Score.ScoreContext) {
        context.applyDrawing {
            circle(
                position = vector(context),
                radius * context.score.unitLength)
        }
    }

}

