package rain.graph


// NOTE: unlike in the python implementation, not using data classes
// (because Kotlin data classes don't allow inheritance, and inheritance
// is important for this data modeling)

abstract class Item(
    val key:String
) {

    abstract fun delete()

    abstract val label: Label<*, out Item>

    override fun toString():String = "${label.labelName}($key)"



}







