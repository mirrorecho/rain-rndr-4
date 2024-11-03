package rain.graph


// NOTE: unlike in the python implementation, not using data classes
// (because Kotlin data classes don't allow inheritance, and inheritance
// is important for this data modeling)

interface Item {

    val key:String

    fun delete()

    val label: Label<*, out Item>


}







