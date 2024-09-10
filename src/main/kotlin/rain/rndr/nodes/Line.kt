package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.LineCap
import org.openrndr.math.Vector2
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.queries.Pattern
import rain.score.Score
import rain.score.nodes.*

open class Line protected constructor(
    key:String = autoKey(),
) : Colorable, Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Line>(
        Machine, Line::class, { k -> Line(k) }
    )

    override val label: Label<out Machine, out Line> = Line

    class LineAnimation: MachineAnimation() {

        var x = 0.0
        var y = 0.0

        // for strokeColor
        var h = 0.0
        var s = 0.9
        var v = 0.9
        var a = 0.0 // set to 0.0 to avoid "flashes" // TODO: more elegant way to solve this?

        var strokeWeight = 0.1 // as a fraction of score unites

    }

    val lineAnimation = LineAnimation()
    override val machineAnimation = lineAnimation

    // TODO: allow this to cascade
    override var fromPosition by RelatedNodeSlot("fromPosition", +FROM_POSITION, Position, null)

    var lineTo by RelatedNodeSlot("lineToPosition", +LINE_TO, LinePoint, null)

    var lineCap by DataSlot("lineCap", LineCap.ROUND)

    override var x by LinkablePropertySlot(lineAnimation::x, +X)
    override var y by LinkablePropertySlot(lineAnimation::y, +Y)

    override var h by LinkablePropertySlot(lineAnimation::h, +H)
    override var s by LinkablePropertySlot(lineAnimation::s, +S)
    override var v by LinkablePropertySlot(lineAnimation::v, +V)
    override var a by LinkablePropertySlot(lineAnimation::a, +A)

    // TODO: relate to score unites (make a Strokable interface?)
    var strokeWeight by LinkablePropertySlot(lineAnimation::strokeWeight, +STROKE_WEIGHT)
//    val strokeColor by RelatedNodeSlot("strokeColor", +STROKE_COLOR, Color, null)

    val vectors: List<Vector2> get() = listOf(this.vector()) + lineTo?.vectors.orEmpty()

    //    // TODO: implement if needed (or remove)
    override fun bump(pattern: Pattern<Event>) {
        updateAllSlotsFrom(pattern.source)
    }

    override fun render(score: Score) {
        lineAnimation.updateAnimation()
        score.applyProgram {
            drawer.stroke = colorRGBa()
            drawer.strokeWeight = strokeWeight * score.unitLength
            drawer.lineCap = lineCap
            drawer.lineStrip(vectors.map { it * score.unitLength })
        }
    }

}

