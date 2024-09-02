package rain.solve

import rain.graph.Node
import rain.score.nodes.Event

fun main()  {
    solve0()
}

// TODO: SOLVE THE FOLLOWING:
//  - I.
//  - (0) print 4 messages, each 1-2 seconds apart (DONE)!
//  - (1) create a circle in the center of the screen (DONE)!
//  - (2) animate the circle's radius so that it increases from 0. to 99. over 4 seconds
//  - (3) add easing to the above radius animation
//  - (4) animate the circle's color's alpha so that it increases from 0. to 1. over 1.1 seconds
//  - (5) continue to animate the circle's color's alpha so that it decreases from 1. to 0. over the next 2.9 seconds
//  - (6) change the circle's position to a random point, over 2 seconds, with easing for both X/Y coordinates
//  - ... cleanup and refactor!
//  - II.
//  - (7) repeat I.(1)-(6) above, every 1/10 second (multiple circle animations overlap)
//  - (8) move the center-point (from 1) around the screen, to a rhythm
//  - ... cleanup and refactor!
//  - III.
//  - (9) create multiple instances of II., each moving around the screen to different rhythms
//  - ... cleanup and refactor!

// -------------------------------------------------------------------------------------

//fun yo() {
//
//    Event.create {
//        dur = 1.0
//        putSlot("radius", 90.0)
//        animate("radius") {
//            toValue = 90.0
//        }
//    }
//
//}


fun solve0() {
    // TODO: util function to easily create Event children with field values
    //  for a particular receiver

    val n = Node.create {  }
    println(n)


//    val n1 = Node.create("N1")
//    val n2 = Node.create("N1")
//
//    n1.relate(BUMPS, n2)
//
//    val doubleNegativeBump = -BUMPS * -BUMPS * -BUMPS
//
//    val n2a = n1[ doubleNegativeBump ].filter { it.key=="YO" } [+BUMPS] .first

//    val s1 = seq {
//        extend(Event) {
//            Event.dur(1.0, 2.0, 1.0)
//            Printer.msg("I am message A!", "I am messageB!", "I am messageC!")
//            Printer.renderMe.cycle(true)
//        }
//    }
//    val s2 = EventRandom.seq {
//        times = 4
//        extend(Event) {
//            Event.dur(0.5, 0.5, 0.5)
//            Printer.msg("AAA!", "BBB!", "CCC!")
//            Printer.renderMe.cycle(true)
//        }
//    }
//
//    val er = EventRandom.seq(s1, s2) {
//        times = 4
//    }
//
////    println(e.treePattern)
//
//    seq(s1) {
//        bumps = Printer.create()
//        gate = Gate.ON_OFF
//    }.play()


//    val eNone = Event.create {
//        dur = 2.0
//        this[Printer.renderMe] = true
//    }
//
//    val eA = Event.create {
//        dur = 1.0
//        this[Printer.msg] = "I am message A!"
//        this[Printer.renderMe] = true
//    }
//
//    val eB = Event.create {
//        dur = 1.0
//        this[Printer.msg] = "I am message B!"
//        this[Printer.renderMe] = true
//    }

//    val s = seq(eNone, eA, eNone, eB) {
//        bumps = Printer.create() { msg = "I am a starting message." }
//        gate = Gate.ON_OFF
//    }.play()

}

//// -------------------------------------------------
//
//fun solve1() {
//    Event.create {
//        gate = Gate.ON_OFF
//        dur = 4.0
//        bumps = Circle.create { h=40.0 }
//    }.play()
//}
//
//// -------------------------------------------------
//
//fun solve2() {
//    val radiusAnimated = ValueAnimate.create("RADIUS") {
//        value = 200.0
//        easing = Easing.CubicInOut
//    }
//
//    // TODO: simplify this!!!
//
//    val eRadius = Event.create("ER") {
//        gate = Gate.ON_OFF
//        dur = 2.0
//        this[ValueAnimate.initValue] = 22.0
//        this[ValueAnimate.animateDur] = 2.0
//        this[ValueAnimate.value] = 290.0
//        bumps = radiusAnimated
//    }
//    val eCircle = Event.create("EC") {
//        gate = Gate.ON_OFF
//        dur = 2.0
//        bumps = Circle.create("CIRCLE") {
//            ::radius.connect(radiusAnimated, "value")
//            h=240.0
//        }
//
//    }
//
//    val anim = par(eRadius, eCircle)
//
//    seq(anim, anim).play()
//
//}