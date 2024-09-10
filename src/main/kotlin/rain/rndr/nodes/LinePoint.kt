package rain.rndr.nodes

import rain.utils.*

import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.queries.Pattern
import rain.rndr.relationships.*
import rain.score.Score
import rain.score.nodes.Event
import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation


open class LinePoint protected constructor(
    key:String = autoKey(),
) : Position(key) {
    companion object : NodeLabel<Position, LinePoint>(
        Position, LinePoint::class, { k -> LinePoint(k) }
    )

    override val label: Label<out Machine, out LinePoint> = LinePoint

    var lineTo by RelatedNodeSlot("lineToPosition", +LINE_TO, LinePoint, null)

    val vectors: List<Vector2> get() = listOf(this.vector()) + lineTo?.vectors.orEmpty()

}


