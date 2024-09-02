package rain.language.patterns

import rain.graph.Node
import rain.graph.quieries.UpdatingQuery
import rain.score.nodes.Cue

import rain.language.patterns.relationships.*


open class CuedChildrenQuery: UpdatingQuery<Node, Node> {

    private fun getCuedNodes(cueNodes: Sequence<Cue>): Sequence<Node> = sequence {
        cueNodes.forEach {cue ->
            yieldAll(cue[+CUES]())
            yieldAll(getCuedNodes(cue[+CUES_NEXT](Cue)))
        }
    }

    override fun asSequence(queryFrom: Sequence<Node>) = sequence {
        queryFrom.forEach {parentNode ->
            yieldAll( getCuedNodes( parentNode[+CUES_FIRST](Cue) ) )
        }
    }


    override fun extend(queryFrom: Sequence<Node>, vararg nodes: Node) {
        if (nodes.isNotEmpty()) {
            queryFrom.forEach { parentNode ->
                // creates all Cue rain.score.nodes for the extension (inc. Contains and Cues rain.score.relationships)
                val cues = nodes.map { childNode ->
                    Cue.create().also { cue ->
                        parentNode.relate(CONTAINS, cue)
                        cue.relate(CUES, childNode)
                    }
                }

                if (parentNode[+CONTAINS]().none())
                // if empty, then create the CuesFirst
                // note... empty check works even after creating the Contains rain.score.relationships above
                // because the isEmpty logic checks for CUES_FIRST
                    CUES_FIRST.create(parentNode, cues[0])
                else {
                    // otherwise create a CuesNext relationship from the existing CuesLast target node to the start of extension cue rain.score.nodes
                    // and remove the CuesLast
                    parentNode.getRelationships("CUES_LAST", true).first().also {
                        CUES_NEXT.create(it.target, cues[0])
                        it.delete()
                    }

                }

                // creates CuesNext rain.score.relationships between all the Cue rain.score.nodes
                cues.asIterable().zipWithNext { c, cNext ->
                    CUES_NEXT.create(c, cNext)
                }

                // adds CuesLast relationship at the end
                CUES_LAST.create(parentNode, cues.last())
            }
        }
    }

    // TODO: implement... clear and deleteAll

}

val CUED_CHILDREN_QUERY = CuedChildrenQuery()