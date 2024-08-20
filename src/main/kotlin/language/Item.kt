package rain.language

import rain.graph.interfacing.GraphableItem


// ===========================================================================================================

// TODO: worth this extra abstract class?
abstract class Item(
    override val key:String,
): GraphableItem {

    abstract val label: Label<out Item>

    final override val labels: Array<String> get() = label.allNames

    final override val labelName: String get() = label.labelName

    final override val properties: MutableMap<String, Any?> = mutableMapOf()

    override fun toString():String = "$labelName($key) $properties"

    // =====================================================================
    // TODO: worth keeping the fancy properties?? KISS!!!

//    fun <T>getFancyProperty(fancyName: String): FancyProperty<T> {
//        return context.getFancyProperty(fancyName)
//    }
//
//    fun setFancyProperty(fancyProperty: FancyProperty<*>) {
//        properties[fancyProperty.name] = fancyProperty.graphValue
//        context.setFancyProperty(fancyProperty)
//    }

//    // TODO: consider if a setProperty is warranted as well
//    fun <T>getProperty(name: String): T? {
//        properties[name]?.let {
//            with(it.toString()) {
//                if (this.startsWith(":FANCY:")) {
//                    return this@Item.getFancyProperty<T>(this.substringAfter(":FANCY:")).value
//                } else {
//                    return it as T
//                }
//            }
//        }
//        return null
//    }
//
//    // TODO: will I end up using this?
//    fun setProperty(name: String, value: Any, isFancy:Boolean=false) {
//        if (isFancy) {
//            setFancyProperty(FancyProperty(name, value, context)) // TODO: ?? hmm why is this OK as opposed to needing setFancyProperty<T>
//        } else {
//            this.properties[name] = value
//        }
//    }

}

// ===========================================================================================================




// TODO: would this be used?
//fun relate(sourceKey:String, labelsToKeys:Map<RelationshipLabel, String>, context:ContextInterface = LocalContext) {
//    labelsToKeys.forEach { (label, key) ->
//        relate(sourceKey, label, key)
//    }
//}



