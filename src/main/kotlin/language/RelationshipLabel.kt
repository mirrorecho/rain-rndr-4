package rain.language

import rain.graph.interfacing.*
import rain.utils.autoKey

class RelationshipLabel(
    override val labelName:String,
    val directionIsRight: Boolean = true,
): Label<Relationship>() {
    val left =
        if (directionIsRight)
            RelationshipLabel(labelName, false)
        else
            this

    override val allNames: Array<String> = arrayOf(labelName)

    override fun toString() = labelName

    fun factory(source:Node, target:Node, key:String): Relationship {
        return Relationship(key, this, source, target)
    }

    fun from(gRelationship: GraphableRelationship):Relationship = gRelationship.run {
        factory(context.nodeFrom(source)!!, context.nodeFrom(target)!!, key).also { it.updatePropertiesFrom(this) }
    }

    // TODO: is all this complexity necessary for relationships???

    fun merge(
        source:Node,
        target:Node,
        key:String,
        properties:Map<String,Any?>?=null,
    ):Relationship =
        factory(source, target, key).apply {
            properties?.let{ this.properties.putAll(it) };
            context.graph.merge(this);
        }

    fun create(
        source:Node,
        target:Node,
        key:String = autoKey(),
        properties:Map<String,Any?>?=null,
    ):Relationship =
        factory(source, target, key).apply {
            properties?.let{ this.properties.putAll(it) };
            context.graph.create(this);
        }

    operator fun invoke(predicate: Filter?=null) =
        Query(
            QueryMethod.related(directionIsRight),
            selectLabelName = labelName
        )




//    fun left(keys:List<String>?=null, properties: Map<String, Any>?= null, label:NodeLabel<*>?=null) =
//        SelectRelationships(labelName=this.labelName, direction= SelectDirection.LEFT).nodes(keys, properties, label?.labelName)
//
//    fun left(vararg keys:String, label:NodeLabel<*>?=null) =
//        left(keys.asList(), null, label)
//
//    fun left(properties: Map<String, Any>?= null, label:NodeLabel<*>?=null) =
//        left(null, properties, label)

}

val TARGETS = RelationshipLabel("TARGETS")