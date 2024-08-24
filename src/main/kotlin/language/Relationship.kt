package rain.language

import rain.graph.interfacing.GraphableRelationship
import rain.utils.autoKey

class Relationship(
    key:String = autoKey(),
    override val label: RelationshipLabel,
    override val source: Node,
    override val target: Node,
): Item(key) {

    fun save() = context.graph.save(this)

    fun read() = context.graph.read(this)

    fun delete() = context.graph.deleteRelationship(this.key)

    val context get() = label.context // TODO: let's avoid this override

    override fun toString():String = "(${source.key} $labelName ${target.key} | $key) $properties"

}

// let's only use if needed...
//fun relate(
//    sourceKey: String,
//    rLabel:RelationshipLabel,
//    targetKey:String,
//    key:String = autoKey()
//):Relationship = rLabel.create(
//    rLabel.context.nodeFrom(sourceKey)!!,
//    rLabel.context.nodeFrom(targetKey)!!,
//    key
//)

fun relate(
    source: Node,
    rLabel:RelationshipLabel,
    target: Node,
    key:String = autoKey()
):Relationship = rLabel.create(source, target, key)