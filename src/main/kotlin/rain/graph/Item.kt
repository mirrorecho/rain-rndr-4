package rain.graph


// NOTE: unlike in the python implementation, not using data classes
// (because Kotlin data classes don't allow inheritance, and inheritance
// is important for this data modeling)

abstract class Item(
    val key:String
) {


    abstract fun delete()

    // TODO: consider making this private or protected
    // TODO: is this even worthwhile? Or only worthwhile for relationships?
    val properties: MutableMap<String, Any?>  = mutableMapOf()

    open operator fun get(name:String) = properties[name]

    open operator fun set(name:String, value:Any?) { properties[name]=value }

    abstract val label: Label<*, out Item>

    override fun toString():String = "${label.labelName}($key) $properties"

    // ================================================================================

    // TODO maybe: used?

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







