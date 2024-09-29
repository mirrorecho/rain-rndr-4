package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.draw.*
import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.nodes.*

class DrawStyle private constructor(
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

    override val animation: DrawStyleAnimation = DrawStyleAnimation()

    var lineCap by DataSlot("lineCap", LineCap.ROUND)
    var lineJoin by DataSlot("lineJoin", LineJoin.ROUND)
    var strokeWeight by RespondingPropertySlot(animation::strokeWeight, +STROKE_WEIGHT, true)
    var fontMap by DataSlot<FontMap?>("fontMap", null)

    var stroke by RelatedNodeSlot("stroke", +STROKE_COLOR, Color, null, true)
    var fill by RelatedNodeSlot("fill", +FILL_COLOR, Color, null, true)

    fun fill(key:String? = null,  block:(Color.()->Unit)?=null) {
        mergeRelated("fill", key, block)
    }

    fun stroke(key:String? = null,  block:(Color.()->Unit)?=null) {
        mergeRelated("stroke", key, block)
    }

    private var myRndrDrawStyle = org.openrndr.draw.DrawStyle()

    // NOTE: even though caching is implemented here per se,
    // keeping hasPlaybackCaching = false to avoid resetting everything on the drawStyle
    // with every play iteration. Instead, handling specific cases for strokeWeight or color updates/animations

    override fun refresh() {
        myRndrDrawStyle.strokeWeight = strokeWeight
        myRndrDrawStyle.lineCap = lineCap
        myRndrDrawStyle.lineJoin = lineJoin
        myRndrDrawStyle.fontMap = fontMap
        myRndrDrawStyle.stroke = stroke?.colorRGBa
        myRndrDrawStyle.fill = fill?.colorRGBa
    }


    override fun playbackRefresh(context: Score.ScoreContext) {
        myRndrDrawStyle.strokeWeight = strokeWeight
        myRndrDrawStyle.stroke = stroke?.colorRGBa
        myRndrDrawStyle.fill = fill?.colorRGBa
    }

    val rndrDrawStyle get() = myRndrDrawStyle

    override val hasPlaybackRefresh: Boolean = true

    override fun updateAnimation(context: Score.ScoreContext) {
        // not calling super since we only need to update strokeWeight with animation
        //  (because strokeWeight is the only thing that can be animated)
        myRndrDrawStyle.strokeWeight = strokeWeight
    }

}

