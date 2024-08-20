package rain.sandbox.solve

//
//fun seq(key:String = autoKey(), machine:NodeLabel<out Machine>?=null, gate: Gate?=null, properties:Map<String,Any?>?=null, vararg children:Tree): Event {
//    return Event.create(key, properties, {
//        gate?.let { this.properties["gate"]=it }
//        this.properties.putIfAbsent("isTrigger", gate!=null )
//        machine?.let { this.properties["machine"]=it }
//    }) { extend(*children) }
//}
//fun seq(machine:NodeLabel<out Machine>?=null, gate: Gate?=null, properties:Map<String,Any?>?=null, vararg children:Tree)  =
//    seq(autoKey(), machine, gate, properties, *children)
//fun seq(gate: Gate?=null, properties:Map<String,Any?>?=null, vararg children:Tree) =
//    seq(null, gate, properties)
//fun seq(properties:Map<String,Any?>?=null, vararg children:Tree) =
//    seq(null, properties, *children)
//fun seq(vararg children:Tree) =
//    seq(null, *children)
//
//fun par(key:String = autoKey(), machine:NodeLabel<out Machine>?=null, gate: Gate?=null, properties:Map<String,Any?>?=null, vararg children:Tree): Event {
//    return Event.create(key, properties, {
//        gate?.let { this.properties["gate"]=it }
//        this.properties["simultaneous"] = true
//        this.properties.putIfAbsent("isTrigger", gate!=null )
//        machine?.let { this.properties["machine"]=it }
//    }) { extend(*children) }
//}
//fun par(machine:NodeLabel<out Machine>?=null, gate: Gate?=null, properties:Map<String,Any?>?=null, vararg children:Tree)  =
//    par(autoKey(), machine, gate, properties, *children)
//fun par(gate: Gate?=null, properties:Map<String,Any?>?=null, vararg children:Tree) =
//    par(null, gate, properties)
//fun par(properties:Map<String,Any?>?=null, vararg children:Tree) =
//    par(null, properties, *children)
//fun par(vararg children:Tree) =
//    par(null, *children)
//
//fun Event.Companion.value(key:String= autoKey(), vararg path:RelationshipLabel, value:Double?=null): Event {
//    return Event()
//}
//fun Event.Companion.value(vararg path:RelationshipLabel, value:Double?=null):Event =
//    Event.value(autoKey(), *path, value=value)
//fun Event.Companion.value(value:Double?=null):Event =
//    Event.value(autoKey(), value=value)
//
//
//
//fun Event.Companion.builder() {
//
//}
//
//
//
//fun main() {
//
//    val e2 = targeting(Event, Circle.TriggerManager(), "YOYO") {}
//
//    val e3 = Event.sends(Circle.receives) {
//        machine = Circle
//        gate = Gate.ON_OFF
//        ::radius.stream(90.0, 200.0)
//    }
//
//    val ev = SubEvent.create().apply {
//        Circle.manager3(this.properties).apply {
//        }
//    }
//
//    Event.patternManager<Color>(ev.properties, ev.getPattern()).apply {
//
//    }
//
//    val manager = Event.patternManager<Color>(ev.properties, ev.getPattern())
//    manager.apply {
//        ::dur.stream(0.5, 1.0)
//        h = 1.4
//    }
//
//
//    Event.triggering {
//        h = 90.0
//    }
//    val radiusAnimate = Event.triggering<AnimateValue>(RADIUS, TRIGGERS.left) { // TODO: test this left label
//        gate = Gate.ON_OFF
//        ::dur.stream(0.2, 2.0, 0.4, 4.0)
//        ::initValue.stream(490.0)
//        ::value.stream(0.0, 200.0, 4.0, 99.0)
//        ::animateDur.stream(0.0, 0.0, 0.0, -1.0)
//    }
//
//    val colorSva = Event.triggering {
//        s = 0.8
//        v = 0.8
//        a = 0.8
//    }
//
//    Event.triggering<Circle>("CIRCLE-ANIM-1") {
//        simultaneous = true
//    }.extend(
//        Event.triggering<AnimateValue>(POSITION, X, TRIGGERS.left) {
//            gate = Gate.ON_OFF
//            ::dur.stream(2.0, 2.0)
//            ::value.stream(0.9, 0.6)
//        },
//
//
//        Event.create(properties = mapOf("machinePath" to listOf(POSITION, Y), "machinePathType" to Value, "value" to 0.2) ),
//        Event.create(properties = mapOf("machinePath" to listOf(FILL_COLOR, H), "machinePathType" to Value, "value" to 290.0) ),
//        colorSva,
//        radiusAnimate
//    )
//
//    val e = seq (
//        par("CIRCLE-ANIM-1", Circle, Gate.ON_OFF,)).makeTrigger().apply {
//
//            val pX = this[POSITION(), X()](Value).first()
//            val pXController = AnimateValue.create {  pX.relate(TRIGGERS, this) }
//        }),
//        )
//        extend(
//
//        )
//    },
//    Event.create("CIRCLE-ANIM-2", mapOf("simultaneous" to true, "machine" to Circle, "gate" to Gate.ON_OFF,)) {
//        relate(TRIGGERS, Circle.create { autoTarget() })
//        extend(
//            Event.triggering {
//                x = 0.5
//                y = 0.5
//            },
//            Event.create(properties = mapOf("machinePath" to listOf(FILL_COLOR, H), "machinePathType" to Value, "value" to 90.0) ),
//            colorSva,
//            radiusAnimate
//        )
//    },
//    )

//    val rChange = Event.create(properties = mapOf("gate" to Gate.ON_OFF, "machinePath" to listOf(RADIUS), "machinePathType" to AnimateValue ) ) {
//        stream("dur", 0.2, 2.0, 0.4, 4.0)
//        stream("value", 90.0, 200.0, 4.0, 99.0)
//        stream("initValue", 490.0)
//        stream("animateDur", 0.0, 0.0, 0.0, -1.0)
//    }
//    val sVA = par(
//        Event.create(properties = mapOf("machinePath" to listOf(FILL_COLOR, S), "machinePathType" to Value, "value" to 0.8) ),
//        Event.create(properties = mapOf("machinePath" to listOf(FILL_COLOR, V), "machinePathType" to Value, "value" to 0.8) ),
//        Event.create(properties = mapOf("machinePath" to listOf(FILL_COLOR, A), "machinePathType" to Value, "value" to 0.8) ),
//        )


//
//
//    val e = seq(
//            par("CIRCLE-ANIM-1", Circle, Gate.ON_OFF,)) {
//                relate(TRIGGERS, Circle.create {
//                    autoTarget();
//                    val pX = this[POSITION(), X()](Value).first()
//                    val pXController = AnimateValue.create {  pX.relate(TRIGGERS, this) }
//                }),
//                extend(
//                    Event.create(properties = mapOf("gate" to Gate.ON_OFF, "machinePath" to listOf(POSITION, X, TRIGGERS.left), "machinePathType" to AnimateValue, "value" to 0.2) ) {
//                        stream("dur", 2.0, 2.0)
//                        stream("value", 0.9, 0.6)
//                        stream("animateDur", 0.0, 0.0)
//                    },
//                    Event.create(properties = mapOf("machinePath" to listOf(POSITION, Y), "machinePathType" to Value, "value" to 0.2) ),
//                    Event.create(properties = mapOf("machinePath" to listOf(FILL_COLOR, H), "machinePathType" to Value, "value" to 290.0) ),
//                    sVA,
//                    rChange
//                )
//            },
//            Event.create("CIRCLE-ANIM-2", mapOf("simultaneous" to true, "machine" to Circle, "gate" to Gate.ON_OFF,)) {
//                relate(TRIGGERS, Circle.create { autoTarget() })
//                extend(
//                    Event.create(properties = mapOf("machinePath" to listOf(POSITION, X), "machinePathType" to Value, "value" to 0.8) ),
//                    Event.create(properties = mapOf("machinePath" to listOf(POSITION, Y), "machinePathType" to Value, "value" to 0.8) ),
//                    Event.create(properties = mapOf("machinePath" to listOf(FILL_COLOR, H), "machinePathType" to Value, "value" to 90.0) ),
//                    sVA,
//                    rChange
//                )
//            },
//    )


//    e.play()


//    e.lineage.nodes.forEach { println(it) }

//
//    val tSelect = SelectRelationships(CONTAINS)
//
////    println(SelectRelationships(CONTAINS).asSequence().toList().size)
//
//    val tSelect = Event.label.select("T0", "T1")[CONTAINS()]
//    val tSelect = t(CONTAINS(), )
//    val tNodes1 = t.path(CUES_FIRST(), CUES(), CUES_LAST(), CUES())(Node.label)
//    val tSelect = t[CONTAINS()]
////    val tSelect = SelectNodes(Tree.label, listOf("T1", "T4")).r(CONTAINS).n(Node.label)
//    val tSelect = t.leaves

////    val tSelect = SelectRelationships(TARGETS)
//    println("----------------------------------------")
//    tSelect.forEach { print("${it.key}:"); println( it.getUp<Any>("yo")) }
//    tSelect(CONTAINS).forEach { println(it) }


////    tSelect.forEach { println(it); println(" - target: ${it.targetKey}") }
//
//    graph.graphRelationships.forEach { println( it ) }

//    t.branches.forEach { println(it.properties) }

//    val c = Cell.create("C1") { machine = Circle.label }.apply {
//        stream("dur", 1.0, 2.0, 1.0, 4.0)
////        channel(RADIUS) {
////            stream("value", 90.0, 20.0, 400.0, 200.0)
////        }
////        channel(RADIUS) {
////            stream("value", 90.0, 20.0, 400.0, 200.0)
////        }
//    }

//    c.nodes.forEach { println(it.properties) }

//    val c = Circle.create("YOMAMA")
////    val c2 = Circle.create()
////    c.relate(Relationship.TARGETS, c2)
//    c.autoTarget()
//    c.radius.target?.apply { this["value"]=400.0; save() }
//
//    val r = Relationship.rndr.RADIUS.select().n(Value.label)
//    val rv = r.first!!
//    println(r.asSequence().toList().size)
//
//    rv["value"] = 20.0
//    rv.save()
//
//    println(rv.properties)
//    c.radius.target = null
//    println(c.radius.target?.get("value"))

//}
//    val c = cell("C1", "Circle", "CIRCLE1") {
//        this["easing"] = "CubicIn"
//        stream("dur", 4.0, 1.0, 2.0, 6.0)
//        stream("gate", true)
////        last("gate", false)
//    }
//    c.leaves.forEach<Event> {
//        println(it.propertiesUp)
//    }
//}

//
//fun solve6() {
//
//    cellBuilder("CELL_14")<> {
//        dur{0.0}
//        dur(0.4, 0.6, 2.0, 1.0 )
//        position("CENTER_POSITION") {
//            x {
//                dur = 0.0
//                value(20.0, 9.0, 120.0, 99.0)
//            }
//            y {
//                value(20.0, 9.0, 120.0, 99.0)
//            }
//        }
//        radius {
//            value(20.0, 9.0, 120.0, 99.0)
//            randMin()
//            randMax()
//            animate(0.0, 0.0, 0.0, null)
//            animateInit()
//            stream("init")(0.0, null, null, null)
//            stream("easing")("CubicIn", "CubicOut", "CubicOut", null)
//        }
//    }
//
////    val circle = circleMachine("CIRCLE_1", true
////        "RADIUS",
////        "POSITION_1",
////        "COLOR_1",
////        "STROKE_WEIGHT",
////        "COLOR_1"
////    )
//
//    println("----------------------------------------------------------------------------")
//
//    val c1 = cell("C1", "CIRCLE_1") { // if no act specified, then actName=machineName
//        vein("dur")(0.4, 0.6, 2.0, 1.0 )
//        vein("RADIUS")(
//            ani(20.0, initValue = 0.0, easing = "CubicIn"), // start at 120.0 and animate to 20.0 over the length of dur (1.0)
//            ani(9.0, easing="CubicOut"),
//            ani(120.0, easing="CubicOut"),
//            ani(99.0)
//        )
//    }
//
//    val c1a = cell<Circle.Builder>("C1", "CIRCLE_1") { // if no act specified, then actName=machineName
//        dur(0.4, 0.6, 2.0, 1.0 )
//        radius {
//            value(20.0, 9.0, 120.0, 99.0)
//            randMin()
//            randMax()
//            animate(0.0, 0.0, 0.0, null)
//            animateInit()
//            vein("init")(0.0, null, null, null)
//            vein("easing")("CubicIn", "CubicOut", "CubicOut", null)
//        }
//        channel("POSITION") {
//            vein("animate")(0.0, 0.0, 0.0, null)
//            vein("easing")("CubicIn", "CubicOut", "CubicOut", null)
//            channel("X") {
//                vein("value")(20.0, 9.0, 120.0, 99.0)
//                vein("init")(0.0, null, null, null)
//            }
//            channel("Y") {
//                vein("value")(20.0, 9.0, 120.0, 99.0)
//                vein("init")(0.0, null, null, null)
//            }
//        }
//    }
//
//    val p1 = cell("P1", "POSITION_1") { // if no act specified, then actName=machineName
//        vein("dur")(2.4, 1.6 )
////        vein("radius")(400.0, 600.0, 90.0, 200.0, 20.0).ani(null, "CubicInOut")
//        vein(machine="X", value="X")(
//            ani(rand(0.0, 0.0), initValue = 0.5, easing = "CubicIn"), // start at 120.0 and animate to 20.0 over the length of dur (1.0)
//            ani(rand(0.0, 1.0), easing="CubicOut"),
//            ani(99.0)
//        )
//        cell(rel="X") {
//            vein()()
//        }
//    }
//
//    val a1 = cell("ALPHA_1", "COLOR_1") {
//        vein("dur")(1.1, 2.9)
//        vein("A")(
//            ani(1.0, initValue = 0.0),
//            ani(0.0, easing="CubicOut")
//        )
//    }
//
//    val par1 = par()(c1, a1)
//
//    val score = Score(rndrMachines)
//    score.readPattern(par1)
//    score.play()
//
//}



// -------------------------------------------------------------------------------------

