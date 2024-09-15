package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.*
import org.openrndr.math.Vector2
import rain.graph.Label
import rain.graph.Node
import rain.graph.NodeLabel
import rain.score.Score
import rain.score.ScoreContext
import rain.score.nodes.*

open class DrawStyle protected constructor(
    key:String = autoKey(),
) : Machine(key) {
    companion object : NodeLabel<Machine, DrawStyle>(
        Machine, DrawStyle::class, { k -> DrawStyle(k) }
    )

    override val label: Label<out Machine, out DrawStyle> = DrawStyle

    class DrawStyleAnimation: MachineAnimation() {

        var strokeWeight = 0.1

    }

    val drawStyleAnimation = DrawStyleAnimation()
    override val machineAnimation = drawStyleAnimation

    var lineCap by DataSlot("lineCap", LineCap.ROUND)
    var lineJoin by DataSlot("lineJoin", LineJoin.ROUND)
    var strokeWeight by PropertySlot(drawStyleAnimation::strokeWeight)
    var fontMap by DataSlot<FontMap?>("fontMap", null)

    var stroke by RelatedNodeSlot("stroke", +STROKE_COLOR, Color, null)
    var fill by RelatedNodeSlot("fill", +FILL_COLOR, Color, null)

    fun fill(key:String? = null,  block:(Color.()->Unit)?=null) {
        mergeRelated("fill", key, block)
        updateRndrDrawStyle()
    }

    fun stroke(key:String? = null,  block:(Color.()->Unit)?=null) {
        mergeRelated("stroke", key, block)
        updateRndrDrawStyle()
    }

    private var myRndrDrawStyle = newRndrDrawStyle()

    private fun newRndrDrawStyle(): org.openrndr.draw.DrawStyle = org.openrndr.draw.DrawStyle(
        strokeWeight = strokeWeight,
        lineCap =  lineCap,
        lineJoin = lineJoin,
        fontMap = fontMap,
        stroke = stroke?.colorRGBa(),
        fill = fill?.colorRGBa(),
    )

    fun updateRndrDrawStyle() {
        myRndrDrawStyle = newRndrDrawStyle()
    }

    val rndrDrawStyle get() = myRndrDrawStyle

    override fun bump(context: ScoreContext) {
        updateRndrDrawStyle()
    }

    override val hasAnimations: Boolean get() = (
            drawStyleAnimation.hasAnimations() ||
            stroke?.hasAnimations == true ||
            fill?.hasAnimations == true )

    override fun updateAnimation(score: Score): Boolean = if (hasAnimations) {
        drawStyleAnimation.updateAnimation()
        val unitLength = scoreContext?.unitLength ?: score.unitLength
        myRndrDrawStyle = myRndrDrawStyle.copy(
            strokeWeight = strokeWeight * unitLength,
            stroke = stroke?.colorRGBa(),
            fill = fill?.colorRGBa(),
        )
        true
    } else false


}

