package rain.graph

import rain.graph.quieries.Queryable
import rain.utils.*


open class Relationship internal constructor(
    override val label: RelationshipLabel,
    val source: Node,
    val target: Node,
    key:String,
    ): Item(key), Queryable<Relationship> {

    override fun asSequence(): Sequence<Relationship> = sequenceOf(this)

//    override val label: RelationshipLabel<*, out Relationship> = Relationship

    override fun delete() {
        label.unregisterFromLabel(this.key)
    }

    override fun toString():String = "(${source.key} ${Node.labelName} ${target.key} | $key) $properties"

    fun directedTarget(directionIsRight:Boolean): Node =
        if (directionIsRight) target else source

}

fun relate(
    source: Node,
    label: RelationshipLabel,
    target: Node,
    key:String = autoKey()
): Relationship = label.create(source, target, key)

fun relate(
    sourceKey: String,
    label: RelationshipLabel,
    targetKey: String,
    key:String = autoKey()
): Relationship? =
    Node[sourceKey]?.let { s-> Node[targetKey]?.let { t-> label.create(s, t, key) } }


// =========================================================================

// TODO maybe: would these be useful?

//typealias LabelDirected = Pair<String, Boolean?>

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

//fun relate(sourceKey:String, labelsToKeys:Map<RelationshipLabel, String>, context:ContextInterface = LocalContext) {
//    labelsToKeys.forEach { (label, key) ->
//        relate(sourceKey, label, key)
//    }
//}

