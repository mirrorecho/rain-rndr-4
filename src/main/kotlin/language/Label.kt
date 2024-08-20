package rain.language

abstract class Label<T: Item> {
    abstract val labelName:String
    abstract val allNames: Array<String> // TODO: change to Array?
    val isRelationship:Boolean = false
    var context: Context = LocalContext // TODO: OK to default to LocalContext here? And should this be a val instead?
}
