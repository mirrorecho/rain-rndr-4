package rain.rndr.nodes

import rain.utils.*

import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import rain.graph.Label
import rain.graph.NodeLabel
import rain.rndr.relationships.*
import rain.score.nodes.*

// TODO maybe: rename to Vector?
interface Positionable {

    var x: Double
    var y: Double

    fun move(polar: Polar) {
        move(Vector2.fromPolar(polar))
    }

    fun move(x:Double, y:Double) { this.x +=x; this.y+=y}

    fun move(vector:Vector2) {
        (vector() + vector).let { x = it.x; y = it.y}
    }

    fun vector(): Vector2 = Vector2(x, y)

    // TODO maybe: consider whether initial vector starts at center (as opposed 0,0 = top left)
    fun vector(context: Score.ScoreContext): Vector2 =
        vector() * context.unitLength

    fun center(score: Score) = this.apply {
        x = score.widthUnits/2.0 + 0.5;
        y = score.heightUnits/2.0 + 0.5;
    }
    fun left() = this.apply { x = 0.0 }
    fun right(score: Score) = this.apply { x = score.widthUnits }
    fun top() = this.apply { y = 0.0 }
    fun bottom(score: Score) = this.apply { y = score.heightUnits }

    fun randomize() {}


}


open class Position protected constructor(
    key:String = autoKey(),
) : Positionable, Machine(key) {
    companion object : NodeLabel<Machine, Position>(
        Machine, Position::class, { k -> Position(k) }
    )

    override val label: Label<out Machine, out Position> = Position

    class PositionAnimation: MachineAnimation() {
        var x = 0.0
        var y = 0.0
    }

    val positionAnimation = PositionAnimation()
    override val machineAnimation = positionAnimation

    override var x by RespondingPropertySlot(positionAnimation::x, +X)
    override var y by RespondingPropertySlot(positionAnimation::y, +Y)

    operator fun plusAssign(vector: Vector2) { move(vector) }



}


