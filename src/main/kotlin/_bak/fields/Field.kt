package rain.language.fields

//import rain.language.*
//import kotlin.reflect.KProperty
//
//// TODO: able to implement Animatable seamlessly here?
//// TODO maybe: reference label containing field here?
//open class Field<T:Any?>(
//    val name: String,
//    val default: T,
//    open val cascade: Boolean = true // TODO: reconsider cascade defaults (after playing with this with solves)
//) {
//
//    open val isNode: Boolean = false
//
//    open inner class Attached(
//        val node: Node
//    ) {
//        val field = this@Field
//
//        var default: T = field.default
//
//        open val isLocal: Boolean = true
//
//        open var value: T = default
//
//        open operator fun getValue(thisRef: Any?, property: KProperty<*>): T = this.value
//
//        open operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {this.value = value}
//
//        open fun resetValue() { value = default }
//
//        open fun resetDefault() { default = field.default }
//
//        open fun store() { node.properties[field.name] = this.value }
//
//        open fun retrieve() {
//            (node.properties[field.name] as T?)?.let { this.value = it }
////            println("retrieving: ${field.name}")
//        }
//
//    }
//
//    open fun attach(
//        node: Node,
//        // previous:Pattern<*>?=null // NOTE: could consider this, for now, KISS
//    ): Attached = Attached(node)
// }
//
////// ======================================================================
////
//fun <T:Any?> field(name: String, default: T, cascade: Boolean = true) =
//    Field(name, default, cascade)
