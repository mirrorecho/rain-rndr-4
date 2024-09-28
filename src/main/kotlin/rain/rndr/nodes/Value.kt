package rain.rndr.nodes

import rain.graph.Label
import rain.graph.NodeLabel
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

    var respondsTo by DataSlot<List<String>>("respondsTo", listOf())

    var respond: (requestValue:Double?)->Double = { value }

    // updates or creates slot with the given value, and returns the slot
    override fun  <T:Any?> slot(name: String, value: T): DataSlot<T> =
        if (name in respondsTo) slot("value", value)
        else super.slot(name, value)

    // gets slot by name (if it exists, otherwise null)
    override fun <T:Any?> slot(name:String): DataSlot<T>? =
        if (name in respondsTo) slot("value")
        else super.slot(name)



}

