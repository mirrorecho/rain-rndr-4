package _bak.fields
//
//import graph.quieries.RelatedQuery
//import org.openrndr.animatable.Animatable
//import kotlin.reflect.KMutableProperty0
//import kotlin.reflect.KProperty
//
///*
//Purpose of Slots is 4-fold:
//- (1) link  to either local value OR value from another node
//- (2) implement Animatable (for local Double values only)
//- (3) interface for specifying values to send to another node (i.e. values an Event sends to a Machine)
//- (4) implement asProperties (for eventually saving/retrieving to/from db)
//- - (3/4) may be related
//- (5) group similar attributes with logic (e.g. color attributes), regardless to whether the data is coming from the same node, or multiple noes
//
//OPTIONS:
// - every single slot is just a node, with one relevant data point
//     (simplest conceptually, but A LOT of rain.score.nodes created!)
// - slot can relate to a node, but if no relationship found, then fall back on local value
//     (as in current / field implementation)
// - slot represents a group of related properties, along with logic (e.g. a ColorManager)
//     (allows more complexity and compartmentalization, but could get confusing,
//     and key data is stored outside the core graph)
//
//*/
//
//interface DataSlot<T:Any?> {
//    val name:String // TODO: needed?
//    var value: T
//
//    fun wireUp() {
//
//    }
//
//    // Will delegation work with Animatable? (assume no)
//    operator fun getValue(thisRef: Any?, property: KProperty<*>): T =
//        value
////        slotRegistry[property.name]!!.localValue
//
//    operator fun setValue(thisRef: Any?, property: KProperty<*>, value:T) {
//        this.value = value
//    }
//
//}
//
//class LocalSlot<T:Any?>(
//    override val name:String,
//    val default:T,
//    val property: KMutableProperty0<T>,
//    val relatedQuery: RelatedQuery? = null
//): DataSlot<T> {
//    private var relatedSlot: DataSlot<T>? = null
//
//
//
////    val slot: Slot<T>
////        get() = relatedSlot ?: this
//
//    override var value: T
//        get() = relatedSlot?.value ?: property.get()
//        set(value) { slot.value = value }
//
//}
//
//
//
//
//
//class PropertySlot<T:Any?>(
//    val default:T,
//    val property: KMutableProperty0<T>,
//    val relatedQuery: RelatedQuery? = null
//): DataSlot<T> {
//    private var relatedSlot: DataSlot<T>? = null
//
//    fun wireUp() {
//
//    }
//
//    val slot: Slot<T>
//        get() = relatedSlot ?: this
//
//    override var value: T
//        get() = relatedSlot?.value ?: property.get()
//        set(value) { slot.value = value }
//
//}
//
//class AnimatableSlot(
//    val default:Double,
//    val relatedQuery: RelatedQuery? = null
//): DataSlot<Double>, Animatable() {
//
//    var localValue = default
//    private var relatedSlot: DataSlot<Double>? = null
//
//    fun wireUp() {
//        this.has
//    }
//
//    val slot: DataSlot<Double?>
//        get() = relatedSlot ?: this
//
//    override var value: Double
//        get() = slot.value ?: localValue ?: default
//        set(value) { slot.value = value }
//
//
//}