package rain.language

import rain.graph.Graph
import rain.graph.interfacing.GraphInterface
import rain.graph.interfacing.GraphableNode

abstract class Context {
    abstract val graph: GraphInterface

    // TODO: interaction with nodeLabels should happen here
    val nodeLabels:MutableMap<String, NodeLabel<*>> = mutableMapOf()

    fun nodeFrom(node: GraphableNode): Node? =
        nodeLabels[node.labelName]?.from(node)

    fun nodeFrom(key:String): Node? =
        nodeFrom(graph.getNode(key))

//    fun queryNodes(query: Query<GraphableNode>): Sequence<GraphableNode> = sequence {  }

//    fun <T : Node>queryNodes(query: Query, label: NodeLabel<T>): Sequence<T> = sequence {
//        graph.selectGraphNodes(query).forEach {
//            yield(label.from(it))
//        }
//    }
//
//    fun queryNodes(query: Query): Sequence<Node> = sequence {
//        graph.selectGraphNodes(query).forEach {
//            this@Context.nodeLabels[it.labelName]?.from(it)?.let { n -> yield(n) }
//        }
//    }
//
//    // TODO: consider removing RelationshipLabelInterface
//    fun queryRelationships(query: Query, label: RelationshipLabelInterface): Sequence<Relationship> = sequence {
//        graph.selectGraphRelationships(query).forEach {
//            yield(label.from(it))
//        }
//    }
//
//    fun queryNodeKeys(query: Query): Sequence<String> = sequence {
//        graph.selectGraphNodes(query).forEach {
//            yield(it.key)
//        }
//    }
//
//    fun selectRelationshipKeys(query: Query): Sequence<String> = sequence {
//        graph.selectGraphRelationships(query).forEach {
//            yield(it.key)
//        }
//    }

    // TODO maybe: consider tracking node labels with the context
//    override val nodeLabels:MutableMap<String, NodeLabel<*>> = mutableMapOf()
//
//    fun <T:Node>getNodeLabel(name:String):NodeLabel<T> {
//        return nodeLabels[name] as NodeLabel<T>
//    }
//
//    inline fun <reified T:Node>getLabel():NodeLabel<T>? {
//        return typeOf<T>()::class.simpleName?.let { getNodeLabel(it) }
//    }


    // ====================================================================
    // KISS:

//    // TODO: maybe these could move into the context of the label????
//    private val fancyProperties: MutableMap<String, FancyProperty<*>> = mutableMapOf()
//
//    override fun setFancyProperty(fancyProperty: FancyProperty<*>) {
//        fancyProperties[fancyProperty.universalName] = fancyProperty
//    }
//
//    override fun <T> getFancyProperty(universalName: String): FancyProperty<T> {
//        return fancyProperties[universalName] as FancyProperty<T>
//    }



}

object LocalContext: Context() {
    override val graph: GraphInterface = Graph()
}

// =================================================================================================
// ARCHIVE:


//abstract class Context {
//    abstract val graph: GraphInterface


    // note, with Kotlin generics and class values, no need for initEmptyGraph and such nonsense
    // as in python implementation

//    fun <T: LanguageItem>make(
//        labelName:String, key:String, properties: Map<String, Any?> = mapOf(), context: ContextInterface=this
//    ): T
//
//    fun <T: LanguageItem>make(fromItem:GraphableItem): T {
//        return this.make(fromItem.primaryLabel, fromItem.key, fromItem.properties, this)
//    }
//
//    // TODO: this is odd, esp. since generic type is just LanguageItem (not a type of relationship)
//    fun <T: LanguageItem>makeRelationship(labelName:String, key:String, source_key:String, target_key:String, properties: Map<String, Any?> = mapOf(), context: ContextInterface=this): T {
//        val myRelationship = this.make(labelName, key, properties, context) as Relationship
//        myRelationship.source_key = source_key
//        myRelationship.target_key = target_key
//        return myRelationship as T
//    }
//
//    fun <T: LanguageRelationship>makeRelationship(fromItem:GraphableRelationship): T {
//        return this.makeRelationship(fromItem.primaryLabel, fromItem.key, fromItem.source.key, fromItem.target.key, fromItem.properties)
//    }

//}

//@property
//@abstractmethod
//def graph(self) -> GraphInterface: pass
//
//@abstractmethod
//def init_empty_graph(self, graph_type:type=None, **kwargs) -> GraphInterface: pass
//
//def init_graph(self, graph_type:type=None, **kwargs)-> GraphInterface:
//# here, this merely calls init_empty_graph to create a new empty graph
//# but specific implementations may connect to existing graph data stores
//return self.init_empty_graph(graph_type, **kwargs)
//
//# TO CONSIDER... a decorator for this
//@abstractmethod
//def register_types(self, *types): pass #TODO: type hinting for this
//
//@abstractmethod
//def get_type(self, label:str) -> type: pass
//
//@abstractmethod
//def new_by_key(self, key:str) -> GraphableInterface: pass
//
//def new_by_label_and_key(self, label:str, key:str, **kwargs) -> GraphableInterface:
//return self.get_type(label)(key, **kwargs)