package rain.rndr.nodes

import rain.graph.Label
import rain.graph.NodeLabel
import rain.rndr.relationships.VALUE
import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation
import rain.utils.*


class RespondingValue private constructor(
    key:String = autoKey(),
    ) : Machine(key) {
    companion object : NodeLabel<Machine, RespondingValue>(
        Machine, RespondingValue::class, { k -> RespondingValue(k) }
    )

    override val label: Label<out Machine, out RespondingValue> = RespondingValue

    open class ValueAnimation: MachineAnimation() {
        var value = 0.0
    }


    override val animation: ValueAnimation = ValueAnimation()

    open var value by RespondingPropertySlot(animation::value, +VALUE)

    var respondsTo by DataSlot<List<String>>("respondsTo", listOf())

    var respond: (sourceValue:Double)->Double = { value }

    // updates or creates slot with the given value, and returns the slot
    override fun  <T:Any?> slot(name: String, value: T): DataSlot<T> =
        if (name in respondsTo) slot("value", value)
        else super.slot(name, value)

    // gets slot by name (if it exists, otherwise null)
    override fun <T:Any?> slot(name:String): DataSlot<T>? =
        if (name in respondsTo) slot("value")
        else super.slot(name)



}

