package rain.rndr.nodes

import rain.language.*
import rain.language.fields.field
import rain.language.patterns.nodes.Machine
import rain.utils.*


open class Value(
    key:String = autoKey(),
    ): Machine(key) {
    abstract class ValueLabel<T:Value>: MachineLabel<T>() {
        val value = field("value", 0.0)
    }

    companion object : ValueLabel<Value>() {
        override val parent = Machine
        override val labelName:String = "Value"
        override fun factory(key:String) = Value(key)
    }

    override val label: NodeLabel<out Value> = Value

    var value by attach(Value.value)


}

