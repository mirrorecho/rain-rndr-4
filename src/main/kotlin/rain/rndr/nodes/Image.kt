package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.draw.ColorBuffer
import org.openrndr.draw.loadImage
import rain.graph.Label
import rain.graph.NodeLabel
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

    override var x by RespondingPropertySlot(imageAnimation::x, +X)
    override var y by RespondingPropertySlot(imageAnimation::y, +Y)

    var imagePath by DataSlot("imagePath", "data/images/cheeta.jpg")

    private var myBuffer: ColorBuffer? = null

    fun load() {
        myBuffer = loadImage(imagePath)
    }


    override fun render(context: Score.ScoreContext) {
        context.applyDrawing {
            myBuffer?.let { image(it, x*context.unitLength, y*context.unitLength) }
        }
    }

}

