package rain.rndr.nodes

import rain.utils.*

import org.openrndr.Program
import rain.graph.Label
import rain.graph.NodeLabel
import rain.rndr.relationships.X
import rain.rndr.relationships.Y
import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation
import rain.score.nodes.Positionable


open class Position protected constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Position>(
        Machine, Position::class, { k -> Position(k) }
    )

    override val label: Label<out Machine, out Position> = Position

    class PositionAnimation: MachineAnimation() {
        var x = 0.5
        var y = 0.5
    }

    val positionAnimation = PositionAnimation()
    override val machineAnimation = positionAnimation

    override var x by LinkablePropertySlot(positionAnimation::x, +X)
    override var y by LinkablePropertySlot(positionAnimation::y, +Y)

    override fun render(program: Program) {
        positionAnimation.updateAnimation()
    }


}


