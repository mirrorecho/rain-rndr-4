package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.animatable.Animatable
import org.openrndr.math.Vector2
import rain.language.Label
import rain.language.NodeLabel
import rain.language.fields.field
import rain.language.patterns.nodes.Event
import rain.language.patterns.nodes.Machine
import rain.language.patterns.nodes.Positionable


open class Position protected constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Position>(
        Machine, Position::class, { k -> Position(k) }
    )

    override val label: Label<out Machine, out Position> = Position

    class PositionAnimation: Animatable() {
        var x = 0.5
        var y = 0.5
    }

    val positionAnimation = PositionAnimation()

    override var x by LinkablePropertySlot(positionAnimation::x, +X)
    override var y by LinkablePropertySlot(positionAnimation::y, +Y)

    override fun render(program: Program) {
        positionAnimation.updateAnimation()
    }


}


