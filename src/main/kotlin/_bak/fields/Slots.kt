package _bak.fields

//import graph.quieries.RelatedQuery
//import org.openrndr.animatable.Animatable
//import rain.language.Node
//import rain.rndr.relationships.H
//import kotlin.reflect.KMutableProperty
//import kotlin.reflect.KMutableProperty1
//import kotlin.reflect.KProperty
//
//
//
//
//List<KMutableProperty1<out Node, out Any?>>
//
//
//
//
//class Slots<T:Any?> {
//    val registry = mutableMapOf<String, Slot<T>>()
//
//
//
//}
//
//class MyNode {
//    val intData: Double? by Slot(0.0, +H)
//
//    var myInt by intData
//
//    fun yo () {
//        ::myInt.slotMe()
//    }
//
//}
//
//
//interface SlotInterface<T:Any?> {
//    val default:T
//    var localValue: T
//}
//
//
//
//
//interface DataManagerInterface {
//
//}
//
//open class Node0() {
//    inner class DataManager(): DataManagerInterface {
//
//    }
//    open val dataManager: DataManagerInterface = DataManager()
//
//}
//
//open class Node1(): Node0() {
//    inner class DataManager(): DataManagerInterface {
//
//    }
//    override val dataManager: DataManagerInterface = DataManager()
//
//}
//
//open class Node2(): Node1() {
//    inner class DataManager(): DataManagerInterface, Animatable() {
//
//    }
//    override val dataManager: DataManager = DataManager()
//}
//
//class Node1A() {
//
//}
//
//class Node2B(): Node1() {
//
//}
//
//class AnimatableSlot<T:Double?>(
//    override val default:T
//): SlotInterface<T>, Animatable() {
//
//    override var localValue = default
//
//}
//
//
//data class AnimatableSlot2(
//    val h:Double,
//    val s: Double
//): Animatable() {
//
//}
//
//fun yo() {
//    val as2 = AnimatableSlot2(1.0, 2.0)
//    as2.component1()
//}
//
//
//class ColorSlots : Slots {
//
//}
