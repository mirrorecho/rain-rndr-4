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
import rain.score.ScoreContext
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

    override var x by SummingPropertySlot(imageAnimation::x, +X)
    override var y by SummingPropertySlot(imageAnimation::y, +Y)

    var imagePath by DataSlot("imagePath", "data/images/cheeta.jpg")

    private var myBuffer: ColorBuffer? = null

    fun load() {
        myBuffer = loadImage(imagePath)
    }


    override fun render(context: ScoreContext) {
        context.applyDrawing {
            myBuffer?.let { image(it, x*context.unitLength, y*context.unitLength) }
        }
    }

}

