package rain.language

import rain.graph.interfacing.GraphableRelationship
import rain.utils.autoKey

class Relationship(
    key:String = autoKey(),
    override val label: RelationshipLabel,
    var sourceKey: String,
    var targetKey: String,
): GraphableRelationship, Item(key) {

    fun save() = context.graph.save(this)

    fun read() = context.graph.read(this)

    fun delete() = context.graph.deleteRelationship(this.key)

    val context get() = label.context // TODO: let's avoid this override

    override fun toString():String = "(${source.key} $labelName ${target.key} | $key) $properties"

    // TODO: are these ever used?
    override val source: Node by lazy { context.nodeFrom(sourceKey)!! }
    override val target: Node by lazy { context.nodeFrom(targetKey)!! }

}

fun relate(
    sourceKey: String,
    rLabel:RelationshipLabel,
    targetKey:String,
    key:String = autoKey()
):Relationship = rLabel.create(sourceKey, targetKey, key)

fun relate(
    sourceNode: Node,
    rLabel:RelationshipLabel,
    targetNode: Node,
    key:String = autoKey()
):Relationship = rLabel.create(sourceNode.key, targetNode.key, key)