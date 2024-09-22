package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import rain.graph.Label
import rain.graph.NodeLabel

import rain.score.nodes.*

open class Text protected constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Text>(
        Machine, Text::class, { k -> Text(k) }
    )

    override val label: Label<out Machine, out Text> = Text

    class TextAnimation: MachineAnimation() {

        var x = 0.0
        var y = 0.0

    }

    val textAnimation = TextAnimation()
    override val machineAnimation = textAnimation

    override var x by SummingPropertySlot(textAnimation::x, +X)
    override var y by SummingPropertySlot(textAnimation::y, +Y)

    var text by DataSlot("text", "RAIN")

    override fun render(context: Score.ScoreContext) {
        context.applyDrawing {
            text(this@Text.text, x*context.unitLength, y*context.unitLength)
        }
    }

}

