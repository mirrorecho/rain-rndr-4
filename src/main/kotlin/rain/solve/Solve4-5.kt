package rain.solve
//
//import org.openrndr.animatable.easing.Easing
//import rain.language.Palette
//import rain._bak.patterns.*
//import rain.rndr.*
//
//
//fun solve5() {
//    createValues(true,"X", "Y", "H", "S", "V", "A", "STROKE_WEIGHT", "RADIUS")
//
//    RndrMachine<Value>("X").apply {
//        setProperty("X", 0.5)
//        save()
//    }
//    RndrMachine<Value>("Y").apply {
//        setProperty("Y", 0.5)
//        save()
//    }
//    RndrMachine<Value>("S").apply {
//        setProperty("S", 0.8)
//        save()
//    }
//    RndrMachine<Value>("V").apply {
//        setProperty("V", 0.8)
//        save()
//    }
//    RndrMachine<Value>("A").apply {
//        setProperty("A", 1.0)
//        save()
//    }
//
//    val position1 = positionMachine("POSITION_1", true, "X", "Y").apply {
//
//    }
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
//    println("----------------------------------------------------------------------------")
//
//    val rndrMachines = Palette.fromKeys<RndrMachine<Act>>(
//        "X", "Y", "H", "S", "V", "A", "STROKE_WEIGHT", "RADIUS",
//        "POSITION_1", "COLOR_1", "CIRCLE_1"
//    )
//
//    val c1 = cell("C1", "CIRCLE_1") { // if no act specified, then actName=machineName
//        vein("dur")(0.4, 0.6, 2.0, 1.0)
////        vein("radius")(400.0, 600.0, 90.0, 200.0, 20.0).ani(null, "CubicInOut")
//        vein("RADIUS")(
//            ani(20.0, initValue = 0.0, easing = "CubicIn"), // start at 120.0 and animate to 20.0 over the length of dur (1.0)
//            ani(9.0, easing="CubicOut"),
//            ani(120.0, easing="CubicOut"),
//            ani(99.0)
//        )
//    }
//
//    val a1 = cell("ALPHA_1", "COLOR_1") {
//        vein("dur")(2.9, 1.1)
//        vein("A")(
//            ani(1.0, initValue = 0.0),
//            ani(0.0, easing="CubicOut")
//        )
//    }
//
//    val par1 = par()(c1, a1)
//
//    val rain.score = Score(rndrMachines)
//    rain.score.readPattern(par1)
//    rain.score.play()
//
//
//}
//
//
//
//// -------------------------------------------------------------------------------------
//
