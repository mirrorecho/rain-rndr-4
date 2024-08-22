package rain.rndr.nodes

import rain.rndr.relationships.*
import rain.utils.*

import org.openrndr.Program
import org.openrndr.math.Vector2
import rain.language.fields.field
import rain.language.patterns.nodes.Event
import rain.language.patterns.nodes.Machine


open class Position protected constructor(
    key:String = autoKey(),
): Machine(key) {

    abstract class PositionLabel<T:Position>: MachineLabel<T>() {
        val x = field("x", X, 0.5)
        val y = field("y", Y, 0.5)
    }

    companion object : PositionLabel<Position>() {
        override val parent = Machine
        override val labelName:String = "Position"
        override fun factory(key:String) = Position(key)
        init { registerMe() }

        val CENTER: Position = Position.create("POSITION_CENTER")
    }

    override val label = Position

    val x by attach(Position.x)
    val y by attach(Position.y)

    fun vector(program: Program): Vector2 = Vector2(
        x * program.width,
        y * program.height,
    )
}


