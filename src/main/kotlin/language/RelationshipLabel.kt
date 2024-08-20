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

    fun factory(sourceKey:String, targetKey:String, key:String): Relationship {
        return Relationship(key, this, sourceKey, targetKey)
    }

    fun from(gRelationship: GraphableRelationship):Relationship = gRelationship.run {
        factory(source.key, target.key, key).also { it.updatePropertiesFrom(this) }
    }

    // TODO: is all this complexity necessary for relationships???

    fun merge(
        sourceKey:String,
        targetKey:String,
        key:String,
        properties:Map<String,Any?>?=null,
    ):Relationship =
        factory(sourceKey, targetKey, key).apply {
            properties?.let{ this.properties.putAll(it) };
            context.graph.merge(this);
        }

    fun create(
        sourceKey:String,
        targetKey:String,
        key:String = autoKey(),
        properties:Map<String,Any?>?=null,
    ):Relationship =
        factory(sourceKey, targetKey, key).apply {
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