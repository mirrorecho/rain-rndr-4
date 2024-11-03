package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.math.Vector2
import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.nodes.*

open class Line protected constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Line>(
        Machine, Line::class, { k -> Line(k) }
    )

    override val label: Label<out Machine, out Line> = Line

    private var localX: Double = 0.0
    private var localY: Double = 0.0

    override var x by RespondingPropertySlot("x", ::localX, +X)
    override var y by RespondingPropertySlot("y", ::localY, +Y)

    var lineTo by RelatedNodeSlot("lineToPosition", +LINE_TO, LinePoint, null)

    val vectors: List<Vector2> get() = listOf(this.vector()) + lineTo?.vectors.orEmpty()

    override fun render(context: Score.ScoreContext) {
        context.applyDrawing {
            lineStrip(vectors.map { it * context.unitLength })
        }
    }

}

