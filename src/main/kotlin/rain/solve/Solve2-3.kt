package rain.sandbox.solve
//
//import org.openrndr.animatable.easing.Easing
//import rain.language.Palette
//import rain._bak.patterns.*
//import rain.rndr.*
//
//
//fun solve3() {
//    createValues(true,"X", "Y", "H", "S", "V", "A", "STROKE_WEIGHT", "RADIUS")
//
//    // TODO: this is not being picked up in the trigger properties!
//    RndrMachine<Value>("X").apply {
//        setProperty("value", 0.25)
//        save()
//    }
//
//    val position1 = positionMachine("POSITION_1", true, "X", "Y")
//
//    val color1 = colorMachine("COLOR_1", true, "H", "S", "V", "A")
//
//    val circle = circleMachine("CIRCLE_1", true,
//        "RADIUS",
//        "POSITION_1",
//        "COLOR_1",
//        "STROKE_WEIGHT",
//        "COLOR_1"
//    )
//
//
//    println("----------------------------------------------------------------------------")
//
////    println(RndrMachine<ValueAct>("RADIUS").apply { read() }.actFactory)
//
//    val rndrMachines = Palette.fromKeys<RndrMachine<Act>>(
//        "X", "Y", "H", "S", "V", "A", "STROKE_WEIGHT", "RADIUS",
//        "POSITION_1", "COLOR_1", "CIRCLE_1"
//    )
//
//
////    val c1 = Cell("C1").apply { createMe() }
////    c1.setVeinCycle("machine", "CIRCLE_1")
////    c1.setVeinCycle("act", "CIRCLE_1")
////
////    // TODO: dur  relationship to the animation is confusing here (first value doesn't animate)
////    c1.setVeins("dur", 1.0, 1.0, 8.0, 1.0)
////    c1.setVeins("radius", 499.0, 20.0, 200.0, 20.0)
////    c1.setVeins("radius:easing", "None", "CubicInOut", "None", null)
////    c1.setVeins("radius:animate", 0.5, 1.0, "None", null)
//
//
//
//    val c1 = cell("C1", "CIRCLE_1") { // if no act specified, then actName=machineName
//        vein("dur")(0.4, 0.6, 2.0, 1.0 )
////        vein("radius")(400.0, 600.0, 90.0, 200.0, 20.0).ani(null, "CubicInOut")
//        vein("RADIUS")(
//            ani(20.0, initValue = 0.0, easing = "CubicIn"), // start at 120.0 and animate to 20.0 over the length of dur (1.0)
//            ani(9.0, easing="CubicOut"),
//            ani(120.0, easing="CubicOut"),
//            ani(99.0)
//        )
////        vein("RADIUS")(
////            20.0,
////            200.0,
////            90.0,
////            9.0,
////        )
//    }
//
//    val cSpace = cell("SPACE", "RADIUS") { vein("dur")(0.01) }
//
//    // TODO: simplify below with a helper function
//    val s = rain._bak.patterns.CellTree().apply {
//        simultaneous = false
//        createMe()
//    }
//    s.extend(
////        cSpace,
//        c1
//    )
//
//
//    val rain.score = Score(rndrMachines)
//    rain.score.readPattern(s)
//    rain.score.play()
//
////    println("YO MAMA")
//
//}
//
//// TODO maybe: make animation pattern an object, and a node stored in the graph
//
//// TODO: consider storing a "map" of how a cell could animate in the graph (i.e. only dur and easing, but not values)
////  ... more generally, consider storing Cell streams that could be reused and merged
//class AnimateCell(
//): Cell(
//
//) {
//    // TODO: don't include value here... should be able to map same AnimateEvent across many value streams
//    var value: Sequence<Double> by this.properties
//    var initValue: Sequence<Double?> by this.properties
//    var easing: Sequence<String> by this.properties
//}
//
//// -------------------------------------------------------------------------------------
//
