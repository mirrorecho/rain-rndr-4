package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.LineCap
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

    class LineAnimation: MachineAnimation() {

        var x = 0.0
        var y = 0.0

    }

    val lineAnimation = LineAnimation()
    override val machineAnimation = lineAnimation

    var lineTo by RelatedNodeSlot("lineToPosition", +LINE_TO, LinePoint, null)

    override var x by SummingPropertySlot(lineAnimation::x, +X)
    override var y by SummingPropertySlot(lineAnimation::y, +Y)

    val vectors: List<Vector2> get() = listOf(this.vector()) + lineTo?.vectors.orEmpty()

    override fun render(context: Score.ScoreContext) {
        context.applyDrawing {
            lineStrip(vectors.map { it * context.unitLength })
        }
    }

}

