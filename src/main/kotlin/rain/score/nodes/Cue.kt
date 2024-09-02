package rain.score.nodes

import rain.graph.Node
import rain.graph.Label
import rain.graph.NodeLabel
import rain.language.patterns.relationships.CUES
import rain.language.patterns.relationships.CUES_NEXT
import rain.utils.autoKey


// TODO... long and nasty with all the class inheritance and companion objects ... REFACTOR!!!!

open class Cue(
    key:String = autoKey(),
): Node(key) {
    companion object : NodeLabel<Node, Cue>(
        null, Cue::class, { k-> Cue(k) }
    )
    override val label: Label<out Node, out Cue> = Cue

    val cues by related(+CUES, Node)
    val cuesNext by related(+CUES_NEXT, Cue)

}


