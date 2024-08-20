package rain.sandbox.solve

import rain.language.patterns.nodes.*

//
//import rain.language.Palette
//import rain.language.patterns.nodes.Cell
//import rain.rndr.*
//
//
fun solve1() {
    val e = Event.sends(Printer) {
        it.bumps = Printer.create {
//            msg = "I am printing!"
        }
        it.dur = 1.0
        it[Printer.msg] = "I am printing!"
        it[Printer.renderMe] = true
    }
    par(e, e, e).play()
}

//    createValues(true,"X", "Y", "H", "S", "V", "A", "STROKE_WEIGHT", "RADIUS")
//
//    // TODO: this is not being picked up in the trigger properties!
//    RndrMachine<Value>("X").apply {
//        setProperty("value", 0.25)
//        save()
//    }
//
//
//
//    val position1 = createRndrMachine("POSITION_1", true) { tr ->
//        // TODO: how to implement the factory if trigger is an update to an existing act? (actName passed, or a "single" machine?)
//        Position(
//            x = tr.relatedAct("X"), // NOTE: this is set at the machine level above
//            y = tr.relatedAct("Y", properties = mapOf("value" to 0.5)),
//        )
//    }.apply { relate("X", "X"); relate("Y", "Y"); } // TODO: implement relate method
//    // TODO: relating here within the apply method is wonky
//
//    val color1 = createRndrMachine("COLOR_1", true) { tr ->
//        Color(
//            h = tr.relatedAct("H", properties = mapOf("value" to 199.0)),
//            s = tr.relatedAct("S", properties = mapOf("value" to 1.0)),
//            v = tr.relatedAct("V", properties = mapOf("value" to 0.5)),
//            a = tr.relatedAct("A", properties = mapOf("value" to 1.0)),
//        )
//    }.apply {
//        relate("H", "H")
//        relate("S", "S")
//        relate("V", "V")
//        relate("A", "A")
//    }
//
//    val circle = createRndrMachine("CIRCLE_1", true) {tr ->
//        println("triggering circle!")
//        Circle(
//            radius = tr.relatedAct("RADIUS", properties = mapOf("value" to 99.0)),
//            position = tr.relatedAct("POSITION"),
//            strokeColor = tr.relatedAct("STROKE_COLOR"),
//            strokeWeight = tr.relatedAct("STROKE_WEIGHT", properties = mapOf("value" to 1.0)),
//            fillColor = tr.relatedAct("FILL_COLOR"),
//        )
//    }.apply {
//        relate("RADIUS", "RADIUS")
//        relate("POSITION", "POSITION_1")
//        relate("STROKE_COLOR", "COLOR_1")
//        relate("STROKE_WEIGHT", "STROKE_WEIGHT")
//        relate("FILL_COLOR", "COLOR_1")
//    }
//    // TODO: DANG!!! So much boilerplate needed... figure out how to simplify!
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
//    val c1 = Cell("C1").apply { createMe() }
//    c1.setVeinCycle("machine", "CIRCLE_1")
//    c1.vein("dur")(2.0)
//
//    val score = Score(rndrMachines)
//    score.readPattern(c1)
//    score.play()
//
////    println("YO MAMA")
//
//}
//
//// -------------------------------------------------------------------------------------