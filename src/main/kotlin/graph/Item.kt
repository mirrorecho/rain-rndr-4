package graph

import rain.language.Label


// NOTE: unlike in the python implementation, not using data classes for subsclasses of this
// (because Kotlin data classes don't allow inheritance, and inheritance
// is important for this data modeling)

abstract class Item(
    val key:String,
) {

    abstract fun delete()

    // TODO: consider making this private
    val properties: MutableMap<String, Any?> = mutableMapOf()

    open operator fun get(name:String) = this.properties[name]

    open operator fun set(name:String, value:Any?) { this.properties[name]=value }

    abstract val label: Label<*, out Item>

    // ================================================================================

    // TODO: used?

//    override fun toString():String = "$labelName($key) $properties"

//    fun anyPropertyMatches(matchProperties: Map<String, Any?>): Boolean {
//        return matchProperties.asIterable().indexOfFirst {
//            // TODO is this the fastest implementation...? maybe another indexOfFirst instead?
//            this.properties.contains(it.key) && this.properties[it.key] == it.value
//        } > -1
//    }
//
//    fun updatePropertiesFrom(properties: Map<String, Any?>) {
//        this.properties.putAll(properties)
//    }
//
//    fun updatePropertiesFrom(item: GraphableItem) {
//        this.properties.putAll(item.properties)
//    }

}







