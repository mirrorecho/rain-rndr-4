package rain.rndr.nodes

import rain.graph.Label
import rain.graph.NodeLabel
import rain.rndr.relationships.VALUE
import rain.score.nodes.Machine
import rain.utils.*


open class Value protected constructor(
    key:String = autoKey(),
    ) : Machine(key) {
    companion object : NodeLabel<Machine, Value>(
        Machine, Value::class, { k -> Value(k) }
    )

    override val label: Label<out Machine, out Value> = Value

    var localValue = 0.0

    var value by RespondingPropertySlot("value", ::localValue, +VALUE)

    // KISS for now, instead just let Value respond to any name
//    var respondsTo by DataSlot("respondsTo", listOf("value"))

    // overriding so that by default, Value responds to any name
    override var respondBlock: (name: String, sourceValue: Double) -> Double = { _, _ -> value }

}
