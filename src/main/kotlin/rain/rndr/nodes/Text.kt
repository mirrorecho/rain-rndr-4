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

    private var localX: Double = 0.0
    private var localY: Double = 0.0

    override var x by RespondingPropertySlot("x", ::localX, +X)
    override var y by RespondingPropertySlot("y", ::localY, +Y)

    var text by DataSlot("text", "RAIN")

    override fun render(context: Score.ScoreContext) {
        context.applyDrawing {
            text(this@Text.text, x*context.unitLength, y*context.unitLength)
        }
    }

}

