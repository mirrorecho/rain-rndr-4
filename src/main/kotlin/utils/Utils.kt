package rain.utils

import kotlin.math.abs
import kotlin.reflect.KProperty

fun autoKey(): String {
    // TODO: consider a better implementation for this?
    return  abs((100..999999999999).random()).toString()
}

fun <T>lazyish(block: ()->T) = Lazyish(block)

class Lazyish<T>(val block: ()->T) {
    private var myValue: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = myValue ?: block().also { myValue=it }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {myValue = value}

}


class Caching<T>(val block: ()->T) {
    fun reset() { myValue = null }

    private var myValue: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = myValue ?: block().also { myValue=it }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {myValue = value}
}

//class NullableMap<T>(val map: MutableMap<String,Any?>, val default:T?=null) {
//
//    operator fun getValue(thisRef: Any?, property: KProperty<*>): T? = (map[property.name] ?: default) as T?
//
//    operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {map[property.name] = value}
//
//}

class DefaultableMap<T>(val map: MutableMap<String,Any?>, val default:T) {

    // TODO: is this the right logic here, or should we check for key existence instead of null check?
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = map.getOrDefault(property.name, default) as T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {map[property.name] = value}

}

fun <T>MutableMap<String, Any?>.withNull(default:T?=null) = DefaultableMap(this, default)

fun <T>MutableMap<String, Any?>.withDefault(default:T) = DefaultableMap(this, default)

//fun nullableMap<T>(val map: MutableMap<String,Any?>, val default:T?=null) = NullableMap


// slick way to create infinite cycle:
// https://stackoverflow.com/questions/48007311/how-do-i-infinitely-repeat-a-sequence-in-kotlin
fun <T> Sequence<T>.cycle() = sequence { while (true) yieldAll(this@cycle) }

fun <T>cycleOf(vararg elements:T): Sequence<T> = elements.asSequence().cycle()

// copies map1 into a new map, then puts all values from map2 into that map as well, and returns the result
// TODO: used? Just able to use simple addition???!!
fun mapCopy(map1: Map<String, Any?>, map2: Map<String, Any?>): Map<String, Any?> = map1.toMutableMap().apply { putAll(map2) }
