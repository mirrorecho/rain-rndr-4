package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.draw.*
import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.DEFAULT_SCORE
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

        var strokeWeight = 0.02

        // TODO: control font size

    }

    val drawStyleAnimation = DrawStyleAnimation()
    override val machineAnimation = drawStyleAnimation

    var lineCap by DataSlot("lineCap", LineCap.ROUND)
    var lineJoin by DataSlot("lineJoin", LineJoin.ROUND)
    var strokeWeight by PropertySlot(drawStyleAnimation::strokeWeight) // TODO maybe: consider whether this should be SummingPropertySlot
    var fontMap by DataSlot<FontMap?>("fontMap", null)

    var stroke by RelatedNodeSlot("stroke", +STROKE_COLOR, Color, null)
    var fill by RelatedNodeSlot("fill", +FILL_COLOR, Color, null)

    fun fill(key:String? = null,  block:(Color.()->Unit)?=null) {
        mergeRelated("fill", key, block)
    }

    fun stroke(key:String? = null,  block:(Color.()->Unit)?=null) {
        mergeRelated("stroke", key, block)
    }

    private var myRndrDrawStyle = org.openrndr.draw.DrawStyle()

    override fun refresh() {
        myRndrDrawStyle.strokeWeight = strokeWeight * (scoreContext?.unitLength ?: DEFAULT_SCORE.unitLength)
        myRndrDrawStyle.lineCap = lineCap
        myRndrDrawStyle.lineJoin = lineJoin
        myRndrDrawStyle.fontMap = fontMap
        myRndrDrawStyle.stroke = stroke?.colorRGBa
        myRndrDrawStyle.fill = fill?.colorRGBa
    }

    val rndrDrawStyle get() = myRndrDrawStyle

    override fun updateAnimation(context: ScoreContext) {
        // not calling super since we only need to update strokeWeight with animation
        //  (because strokeWeight is the only thing that can be animated)
        myRndrDrawStyle.strokeWeight = strokeWeight * (scoreContext?.unitLength ?: DEFAULT_SCORE.unitLength)
    }

}

