package rain.rndr.nodes

import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.ScoreContext
import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation
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
    override val machineAnimation = valueAnimation

    var value by PropertySlot(valueAnimation::value)


}

