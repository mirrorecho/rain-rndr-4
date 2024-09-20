package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.ScoreContext
import rain.score.nodes.*

open class Circle protected constructor(
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

    val circleAnimation = CircleAnimation()
    override val machineAnimation = circleAnimation

    var radius by SummingPropertySlot(circleAnimation::radius, +RADIUS)

    override var x by SummingPropertySlot(circleAnimation::x, +X)
    override var y by SummingPropertySlot(circleAnimation::y, +Y)

    override fun render(context: ScoreContext) {
        context.applyDrawing {
            circle(
                position = vector(context),
                radius * context.score.unitLength)
        }
    }

}

