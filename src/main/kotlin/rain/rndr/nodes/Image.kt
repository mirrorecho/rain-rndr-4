package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.color.ColorHSVa
import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.LineCap
import org.openrndr.draw.loadImage
import org.openrndr.math.Vector2
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.queries.Pattern
import rain.score.Score
import rain.score.nodes.*

open class Image protected constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Image>(
        Machine, Image::class, { k -> Image(k) }
    )

    override val label: Label<out Machine, out Image> = Image

    class ImageAnimation: MachineAnimation() {

        var x = 0.0
        var y = 0.0

        // TODO: implement scaling, cropping

    }

    val imageAnimation = ImageAnimation()
    override val machineAnimation = imageAnimation

    // TODO: allow this to cascade
    override var fromPosition by RelatedNodeSlot("fromPosition", +FROM_POSITION, Position, null)

    override var x by LinkablePropertySlot(imageAnimation::x, +X)
    override var y by LinkablePropertySlot(imageAnimation::y, +Y)

    var imagePath by DataSlot("imagePath", "data/images/cheeta.jpg")

    private var myBuffer: ColorBuffer? = null

    fun load() {
        myBuffer = loadImage(imagePath)
    }

    //    // TODO: implement if needed (or remove)
    override fun bump(pattern: Pattern<Event>) {
        updateAllSlotsFrom(pattern.source)
    }

    override fun render(score: Score) {
        imageAnimation.updateAnimation()
        score.applyProgram {
            myBuffer?.let { drawer.image(it) }
        }
    }

}

