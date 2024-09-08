package rain.rndr.nodes

import rain.utils.*

import org.openrndr.math.Polar
import org.openrndr.math.Vector2
import rain.graph.Label
import rain.graph.NodeLabel
import rain.graph.queries.Pattern
import rain.rndr.relationships.*
import rain.score.Score
import rain.score.nodes.Event
import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation

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

    fun vector(): Vector2 = fromPosition?.let { it.vector() + Vector2(x, y)} ?: Vector2(x, y)

    val fromPosition: Position? get() = null

    // TODO maybe: consider whether initial vector starts at center (as opposed 0,0 = top left)
    fun vector(score: Score): Vector2 =
        vector() * score.unitLength

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

    override var fromPosition by RelatedNodeSlot("fromPosition", +FROM_POSITION, Position, null)

    class PositionAnimation: MachineAnimation() {
        var x = 0.0
        var y = 0.0
    }

    val positionAnimation = PositionAnimation()
    override val machineAnimation = positionAnimation

    override var x by LinkablePropertySlot(positionAnimation::x, +X)
    override var y by LinkablePropertySlot(positionAnimation::y, +Y)

    operator fun plusAssign(vector: Vector2) { move(vector) }

    override fun bump(pattern: Pattern<Event>) {
        updateAllSlotsFrom(pattern.source)
    }

    override fun render(score: Score) {
        positionAnimation.updateAnimation()
    }


}


