package rain.rndr.nodes

import rain.utils.*

import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import rain.graph.Label
import rain.graph.NodeLabel
import rain.rndr.relationships.*
import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation


class LinePoint private constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, LinePoint>(
        Machine, LinePoint::class, { k -> LinePoint(k) }
    )

    override val label: Label<out Machine, out LinePoint> = LinePoint

    class LinePointAnimation: MachineAnimation() {
        var x = 0.0
        var y = 0.0
    }

    override val animation: LinePointAnimation = LinePointAnimation()

    override var x by RespondingPropertySlot(animation::x, +X)
    override var y by RespondingPropertySlot(animation::y, +Y)

    var lineTo by RelatedNodeSlot("lineToPosition", +LINE_TO, LinePoint, null)

    val vectors: List<Vector2> get() = listOf(this.vector()) + lineTo?.vectors.orEmpty()

}


