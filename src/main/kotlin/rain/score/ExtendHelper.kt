package rain.score

import rain.graph.NodeLabel
import rain.utils.cycleOf
import rain.score.nodes.Event


class ExtendHelper<ET: Event>(
    val parentEvent: Event,
    val label: NodeLabel<*, out ET>,
) {
    var extendLength: Int = 0

    // a list of manipulations to an extended event for a given index
    // (generally, each field to be updated will get an item in the list)
    val lambdaList = mutableListOf<(ET, Int)->Unit>()

    fun <T:Any?> slots(name: String, vararg values:T?) {
        if (values.size>extendLength) extendLength = values.size

        lambdaList.add { e: ET, i: Int ->
            values.getOrNull(i) ?.let { v->
                e.slot(name, v)
            }
        }

    }

    fun <T:Any?> cycle(name: String, vararg values:T?) {
        val cycle = cycleOf(*values)
        lambdaList.add { e: ET, i: Int ->
            cycle[i] ?.let { v->
                e.slot(name, v)
            }
        }
    }

    fun extendEvent() {
        parentEvent.childrenPattern().extend(
            *(0..<extendLength).map { i->
                label.create {
                    lambdaList.forEach { block ->
                        block.invoke(this, i)
                    }
                }
            }.toTypedArray<Event>()
        )
    }

}