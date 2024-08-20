package rain.language.patterns.nodes

import rain.language.Node
import rain.language.NodeLabel
import rain.language.Thingy
import rain.language.fields.field


// TODO... long and nasty with all the class inheritance and companion objects ... REFACTOR!!!!

open class Cue(
    key:String = rain.utils.autoKey(),
): Node(key) {
    abstract class CueLabel<T:Cue>: NodeLabel<T>() {
        // add fields here:
        val thing = field("thing", "One and Two")
    }

    companion object : CueLabel<Cue>() {
        override val labelName:String = "Thingy"
        override fun factory(key:String) = Cue(key)
    }

    override val label: NodeLabel<out Thingy> = Thingy

    // attach fields here:
    val thing = attach(Thingy.thing)


}


