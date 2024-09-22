package rain.score.nodes

import rain.graph.Label
import rain.graph.NodeLabel


// a simple machine for demo/testing purposes..
open class Printer(
    key: String = rain.utils.autoKey(),
) : Machine(key) {
    companion object : NodeLabel<Machine, Printer>(
        Machine, Printer::class, { k -> Printer(k) }
    )

    override val label: Label<out Machine, out Printer> = Printer

    var msg by DataSlot("msg", "Default Message")
    var renderMe by DataSlot("renderMe", false)


    override fun bump(context: Score.ScoreContext) {
        // could add logic here
    }

    override fun render(context: Score.ScoreContext) {
        if (renderMe) {
            println("${this.key} PRINTING: $msg")
            renderMe = false // set back to false to prevent endless messages
        }
    }

}
