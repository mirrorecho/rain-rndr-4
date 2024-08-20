package rain.language.patterns

//import rain.graph.interfacing.*
//import rain.language.*
//
//// TODO: consider bringing this back, or more likely KISS and archive
//open class HistoryPattern<T: Node>(
//    fromPattern: Pattern<T,T,T>,
//    label: NodeLabel<T>,
//): Pattern<T, T, T>(fromPattern.source, label, null) {
//
//    override val graphableNodes:Sequence<GraphableNode> = sequence {
//        yield(source);
//        previous?.let { yieldAll(it.graphableNodes) }
//    }
//
//    override operator fun invoke(): Sequence<T> = sequence {
//        yield(source);
//        previous?.let { yieldAll(it.history.map {  }) }
//    }
//
//}