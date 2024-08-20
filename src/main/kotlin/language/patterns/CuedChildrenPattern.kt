package rain.language.patterns

import rain.graph.interfacing.GraphableNode
import rain.language.*
import rain.language.patterns.nodes.Cue

import rain.language.patterns.relationships.*


open class CuedChildrenPattern<T: Node>(
    source: T,
    previous: Pattern<*>? = null,
): Pattern<T>(source,  previous) {


    private fun getChildCues(qCue: Query): Sequence<GraphableNode> = sequence {
        qCue[CUES()].graphableNodes.forEach {
            yield(it)
            yieldAll(getChildCues(qCue[CUES_NEXT()]))
        }
    }

    override val graphableNodes = getChildCues(this.source[CUES_FIRST()])

    override fun extend(vararg nodes: Node) {
        // creates all Cue nodes for the extension (inc. Contains and Cues relationships)
        val cues = nodes.map { childNode ->
            Cue.create().also { cue ->
                this.source.relate(CONTAINS, cue)
                cue.relate(CUES, childNode)
            }
        }

        if (graphableNodes.none())
        // if empty, then create the CuesFirst
        // note... empty check works even after creating the Contains relationships above
        // because the isEmpty logic checks for CUES_FIRST
            CUES_FIRST.create(this.source.key, cues[0].key)
        else {
            // otherwise create a CuesNext relationship from the existing CuesLast target node to the start of extension cue nodes
            // and remove the CuesLast
            this.source.getRelationships(CUES_LAST).first().also {
                CUES_NEXT.create(it.targetKey, cues[0].key)
                it.delete()
            }

        }

        // creates CuesNext relationships between all the Cue nodes
        cues.asIterable().zipWithNext { c, cNext ->
            CUES_NEXT.create(c.key, cNext.key)
        }

        // adds CuesLast relationship at the end
        CUES_LAST.create(this.source.key, cues.last().key)
    }

    // this is cool... HAH!
    // TODO: type cast is not cool here... able to remove it???
    val children: Sequence<CuedChildrenPattern<T>> get() = this.asPatterns(source.label as NodeLabel<T>) { s,p -> CuedChildrenPattern(s, p) }

    // TODO: implement...
    override fun clear(deleteNodes:Boolean) = warningNotImplemented("clear")

}

