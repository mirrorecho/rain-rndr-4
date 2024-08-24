// NOTE: using plural "interface" to avoid name clash with "interface" keyword
package rain.graph.interfacing

// NOTE: unlike in the python implementation, not using data classes for subsclasses of this
// (because Kotlin data classes don't allow inheritance, and inheritance
// is important for this data modeling)
//interface GraphableItem {
//
//
//    val key: String
//
//    val labels: Array<String>
//
//    val labelName: String
//
//    val properties: MutableMap<String, Any?>
//
//    // TODO: bring this back?
////    operator fun get(name:String) = this.properties[name]
//
////    fun <T>getAs(): T = this.properties.
//
//    operator fun set(name:String, value:Any?) { this.properties[name]=value }
//
//    // TODO: used?
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
//
//}
//
//// ===========================================================================================================
//
//interface GraphableNode: GraphableItem {
//}
//
//// ===========================================================================================================
//
//interface GraphableRelationship: GraphableItem {
//
//    val source: GraphableNode
//    val target: GraphableNode
//
//    fun directedTarget(directionIsRight:Boolean=true): GraphableNode = if (directionIsRight) target else source
//
//}