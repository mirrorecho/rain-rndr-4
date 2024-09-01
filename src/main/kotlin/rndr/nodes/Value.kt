package rain.rndr.nodes

import org.openrndr.Program
import rain.language.*
import rain.language.patterns.nodes.Machine
import rain.language.patterns.nodes.MachineAnimation
import rain.utils.*


open class Value(
    key:String = autoKey(),
    ) : Machine(key) {
    companion object : NodeLabel<Machine, Value>(
        Machine, Value::class, { k -> Value(k) }
    )

    override val label: Label<out Machine, out Value> = Value

    class ValueAnimation: MachineAnimation() {
        var value = 0.0
    }

    val valueAnimation = ValueAnimation()

    var value by PropertySlot(valueAnimation::value)

    override fun render(program: Program) {
        valueAnimation.updateAnimation()
    }


}

