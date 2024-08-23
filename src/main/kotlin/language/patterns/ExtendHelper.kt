package language.patterns

import rain.language.fields.*
import rain.language.patterns.nodes.*
import rain.utils.cycleOf


class ExtendHelper<NT:Event, LT:Event.EventLabel<NT>>(
    val parentEvent:Event,
    val label:LT,
) {
    var extendLength: Int = 0

    // a list of manipulations to an extended event for a given index
    // (generally, each field to be updated will get an item in the list)
    val lambdaList = mutableListOf<(NT, Int)->Unit?>()

    operator fun <T:Any?, F:Field<T>> F.invoke(vararg values:T?) {
        if (values.size>extendLength) extendLength = values.size

        lambdaList.add { e: NT, i: Int ->
            values.getOrNull(i) ?.let { e[this]=it }
        }
    }

    fun <T:Any?, F:Field<T>> F.cycle(vararg values:T?) {
        val cycle = cycleOf(*values)
        lambdaList.add { e: NT, i: Int ->
            cycle[i] ?.let { e[this]=it }
        }
    }

    fun extendEvent() {
        parentEvent.treePattern.extend(
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