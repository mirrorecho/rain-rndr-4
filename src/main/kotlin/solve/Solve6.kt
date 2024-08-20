package rain.sandbox.solve

//
//
//import rain.language.*
//import rain.language.patterns.nodes.*
//import rain.rndr.nodes.Circle
//import rain.rndr.nodes.Position
//import rain.rndr.nodes.Value
//import rain.rndr.nodes.ValueAnimate
//import rain.rndr.relationships.RADIUS
//
//
//fun yo()
//
//fun solve1() {
////
////    // TODO: remove "sends" method
////
//
////    Event.sends(Circle, "E1", { msg ->
////        msg[gate] = Gate.ON_OFF
////
////    }) { e1 ->
////        receives(e1, "C1") { circle ->
////            circle.relate(radius, "RADIUS_MASTER")
////        }
////
////    }
//
//    Event.sends(Circle, "E1") { e1 ->
////        receives(e1, "C1") { circle ->
////            circle.relate(radius, "RADIUS_MASTER")
////        }
//        e1.bumps.value = Circle.get("C1")
//        e1.dur.value = 4.0
//        e1[radius] = 20.0
//    }
//
//
//    par(
//        Event.sends(Circle, "E1", {initE1 ->
//            initE1[gate] = Gate.ON_OFF
//            initE1["yomama"] = "sofat"
//        }) {e1 ->
//
//            // merges to create/get Circle with key "C1"
//            // and adds BUMPS relationship from this event to that Circle
//            // TODO: naming?
//            receives(event, "C1", { msg ->
//                msg[strokeWeight] = 2.0
//            }) { circle ->
//                // relates circle radius to existing RADIUS_MASTER node
//                // TODO: is specifying the property name "value" necessary here?
//
//                circle.relate(radius, "RADIUS_MASTER")
//
//                // merges to create/get ValueAnimate with key "R1"
//
//                position[circle]
//
//                event.nest(circle, position, Position, Event, "P1", "E1", {}, {})
//
//                event.childSends(Event, Position, "E1", {}) {
//                    receives(event, "P1", {msg[]})
//                }
//
//                // TODO: combine this logic all into 1 method
//                //  (i.e. creates a related machine node, and also an event
//                circle.relateMerge(radius, Position, "POSITION_1", {
//                }) { p->
//                    event.childrenPattern.extend(
//                        Event.sends(Position) {
//                            receives(it, p.key)
//                            // ...
//                        }
//                    )
//                }
//
//
//
//                circle.fieldTo(radius,  ) { position ->
//
//                    event.childrenPattern.extend(
//                        // TODO make preCreate optional
//                        Event.sends(Position, "E2",{}) {event2 ->
//                            event2.bumps(this, "P1")
//                        }
//                    )
//
//                    Circle.strokeColor,
//
//                    event.propagate { p ->
//                        p(easing, 1.0, 20.0, 200.0)
//                        p(value, 90.0, 20.0, 200.0)
//                    }
//
//                    event.propagate(Event, value, 90.0, 20.0, 200.0)
//                    event.propagate(Event, easing, 1.0, 20.0, 200.0)
//                }
//
//            }
//
//
//            // adds dur property fields to child nodes,
//            // creating notes as necessary of type specified (Event)
//            // TODO: see about omitting Event unless a specific subclass of Event needed
//            // TODO: see about slicker/compound stream method that can accommodate multiple fields
//            it.stream(Event, dur, 1.0, 3.0, 0.5)
//
//            it.stream()[]
//
//
//
//            // compare with streams for value and easing above... the idea is the same here
//            it.stream(Event, radius.related<ValueAnimate>().initValue, 1.0, 20.0)
//
//            // TODO: is this workable? how to simplify?
//            it.stream(Event, position.related<Position>().x, 0.0, 0.5, 0.0)
//            it.stream(Event, position.related<Position>().y, 0.0, 0.5, 0.0)
//
//        },
//        Event.sends(Circle) {
//            it.bumps()
//
//            // relates to existing R1 ... note that
//            it.relate(radius, "R1")
//
//            it.stream(Event, dur, 0.5, 4.0)
//
//            it.stream(Event, position.related<Position>().x, 1.0, 0.5)
//            it.stream(Event, position.related<Position>().y, 1.0, 0.5)
//        }
//    )
//
//
//
//        m.apply {
//            receiverLabel.apply {
//                set(radius, 1.0)
//            }
//            radius(this) = 1.0
//            this[Circle.radius] = 1.0
//            Circle.apply {
//                set(radius, 1.0)
//            }
//
//            set(Circle.radius, 1.0)
//            stream(Circle.radius, 1.0, 1.0, 1.0)
//        }
//
//
//
//        // IMPORTANT: messages cascade IFF
//        // replace below with something like Event.sends(CircleMessage)
//        CircleMessage<Event>().sends(Event) {
//            connectMachine() // connects to newly created or existing machine
//            gate = "ON_OFF"
//            receiver.radius.container() { // IMPORTANT: container creates an event that doesn't bump... only has child events
//                dur.stream(1.0, 1.0, 2.0)
//                value.stream(90.0, 200.0, 20.0)
//                stream { yo="mama0" } { } { yo="mama1" }
//            }
//        }
//
//
//    val e = event("E1", Circle.receives) {
//        addTrigger("C1")
//        gate = Gate.ON_OFF
//        simultaneous = true
//        radius.value = 0.0
//
//
////        targeting(radius) {
////
////        }
////
////        targeting(fillColor.h) {
////
////        }
//
//        fun <T:Machine>event2(key:String, targeting:CachedTarget<T>): T {
//
//        }
//
//        val m = event2("E1", CachedTarget<Circle>())
//        m.manager.extend()
//
//        extend(
//
//            event("E1-1", Value.receives) {
//
//                deferToPattern {
//                    it.historyDimension?.pattern
//                }
////                gate = Gate.NONE // NOTE: shouldn't have to specify this
////                simultaneous = false // NOTE: shouldn't have to specify this
////                deferToPattern { p-> ValueAnimate.create().relate(ANIMATES, p.node) }
////                machinePath = arrayOf(RADIUS, ANIMATES.left)
//                // TODO: are cascading properties + defaults working correctly?
//                machinePath = arrayOf(RADIUS)
////                ::initValue.stream(0.0)
//                ::dur.stream(2.0, 0.5, 4.0)
//                ::value.stream(90.0, 200.0, 9.0)
//            }
//        )
//    }
////    println(Circle.get("C1"))
//
//    e.manageWith(cr) {
////
////            play()
//////        val child = this[DimensionLabel.CHILDREN]?.invoke()?.first()
//////        val grandChildren = child?.get(DimensionLabel.CHILDREN)
//////        grandChildren?.forEach { println(it.cascadingProperties) }
////
//////        val child = this[DimensionLabel.CHILDREN]?.invoke()?.first()
//////        println(child?.node)
//////        println(child?.history?.invoke()?.first()?.node)
//////        print(child?.history?.invoke()?.toList())
//////        val machineDimension = child?.get(DimensionLabel.TRIGGERS) as RelatesHistoryDimension?
//////        val machine = machineDimension?.invoke()?.first()
//////        println(machineDimension?.extendedRelationships?.toList())
//////        println(child?.node)
//////        println(machine?.node)
////
//////        play()
////    }
//        this[DimensionLabel.CHILDREN]?.asPatterns()?.forEach {
////            println(it.historyDimension)
////            println(it.historyDimension?.pattern?.node)
//            println(it.node)
//            it[DimensionLabel.CHILDREN].asPatterns().forEach { it2 ->
//                println(it2.cascadingProperties)
//            }
//        }
//    }
//
//
//
////    Event.get("C1").manageWith(Circle.receives) {
////        get(DimensionLabel.TRIGGERS)?.forEach {
////            println(it.node)
////        }
////    }
//
//
//}
//
//fun main() {
//    solve1()
////    val m = Color.receives
////    val e = Event.sends(m) {
////        h = 200.0
////        machineLabel = Color
////        addTrigger()
//////        simultaneous = false
////        // TODO: need a method that can create the TRIGGERS relationship
////
////        extend(
////            Event.sends(Value.receives) { value = 0.4; dur = 1.0; machineLabel=Value },
////            Event.sends(Value.receives) { value = 0.9; dur = 2.0 },
////            Event.sends(Value.receives) { value = 0.0; dur = 1.0 }
////        )
////    }
//
////    e.manageWith(Color.receives) {
////        get(DimensionLabel.CHILDREN)?.forEach {
////            it.node.properties.manageWith(Value.receives) {
////                println(dur)
//////                println(properties)
////////                println(value)
////////                println(it.cascadingProperties)
////            }
////        }
////    }
//
//
////    e.manageWith(Event.EventManager()) {
////
////        getPatterns(DimensionLabel.CHILDREN).forEach { println(it.node) }
//////        node?.let {n->
//////            n[CUES_FIRST(), CUES_NEXT(), CUES_NEXT(), CUES()].forEach { println(it) }
//////        }
////
//////        println(pattern)
//////        getPatterns(PatternDimension.CHILDREN).forEach { println(it.node) }
////    }
//
//
////    println(e.manager.pattern)
//
////    Cue.get().forEach { println(it) }
////
////    Cue[CUES()].forEach { println(it) }
//
////    e[CONTAINS(), CUES()].forEach { println(it) }
//
////    println(e.manager.pattern)
////    println(e.getPatterns(PatternDimension.CHILDREN))
////    val eChildren = TriggeringTree(e)[PatternDimension.CHILDREN]
////    println(eChildren.first())
////    eChildren.forEach { println(it) }
//
//}
