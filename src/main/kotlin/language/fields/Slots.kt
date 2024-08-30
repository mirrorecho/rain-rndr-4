package language.fields

import graph.quieries.RelatedQuery
import org.openrndr.animatable.Animatable

/*
Purpose of Slots is 4-fold:
 - (1) link  to either local value OR value from another node
 - (2) implement Animatable (for local Double values only)
 - (3) interface for specifying values to send to another node (i.e. values an Event sends to a Machine)
 - (4) implement asProperties (for eventually saving/retrieving to/from db)
   - - (3/4) may be related
*/


abstract class Slots {
    val intSlots = listOf<Slot<Int?>>()
    val doubleSlots = listOf<Slot<Double?>>()
    val stringSlots = listOf<Slot<String?>>()
    val boolSlots = listOf<Slot<Boolean?>>()
}


interface SlotInterface<T:Any?> {
    val default:T
    var localValue: T
}


abstract class Slot<T:Any?>(
    val default:T,
    val relatedQuery: RelatedQuery? = null
) {

    var localValue = default

}

interface DataManagerInterface {

}

open class Node0() {
    inner class DataManager(): DataManagerInterface {

    }
    open val dataManager: DataManagerInterface = DataManager()

}

open class Node1(): Node0() {
    inner class DataManager(): DataManagerInterface {

    }
    override val dataManager: DataManagerInterface = DataManager()

}

open class Node2(): Node1() {
    inner class DataManager(): DataManagerInterface, Animatable() {

    }
    override val dataManager: DataManager = DataManager()
}

class Node1A() {

}

class Node2B(): Node1() {

}

class AnimatableSlot<T:Double?>(
    override val default:T
): SlotInterface<T>, Animatable() {

    override var localValue = default

}


data class AnimatableSlot2(
    val h:Double,
    val s: Double
): Animatable() {

}

fun yo() {
    val as2 = AnimatableSlot2(1.0, 2.0)
    as2.component1()
}


class ColorSlots : Slots {

}
