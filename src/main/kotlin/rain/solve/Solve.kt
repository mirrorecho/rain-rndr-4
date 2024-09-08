package rain.solve

import org.openrndr.animatable.easing.Easing
import rain.graph.*
import rain.graph.queries.*
import rain.language.patterns.relationships.BUMPS
import rain.score.*
import rain.score.nodes.*
import rain.rndr.*
import rain.rndr.nodes.*
import kotlin.reflect.KMutableProperty1

fun main()  {
    solve3()
}

/*
SOLVE THE FOLLOWING:
    - I.
    - (0) print 4 messages, each 1-2 seconds apart (DONE!)
    - (0.1) randomize the order (DONE!)
    - (0.2) repeat it twice (DONE!)

    - (1) create a circle in the center of the screen (DONE!)

    - (2) animate the circle's radius so that it increases from 0. to 99. over 4 seconds (DONE!)
    - (2.1) add easing to the above radius animation (DONE!)
    - (2.2) animate the circle's color's alpha so that it increases from 0. to 1. over 2.9 seconds (DONE!)
    - (2.3) continue to animate the circle's color's alpha so that it decreases from 1. to 0. over the next 1.1 seconds (DONE!)

    - (3.1) change the circle's position to a random point, over 2 seconds, with easing for both X/Y coordinates
    - (3.2) combine the above into a larger nested repeating structure of pars and seqs
    - ... cleanup and refactor!

    - II.
    - (7) repeat I.(1)-(6) above, every 1/10 second (multiple circle animations overlap)
    - (8) move the center-point (from 1) around the screen, to a rhythm
    - ... cleanup and refactor!
    - III.
    - (9) create multiple instances of II., each moving around the screen to different rhythms
    - ... cleanup and refactor!
*/


// -------------------------------------------------------------------------------------

fun solve0() {
    // TODO maybe: util function to easily create Event children with field values
    //  for a particular receiver

    val s1 = EventRandom.seq {
        bumps = Printer.create("PRINTER_1") { msg = "I am a $key" }
        gate = Gate.ON_OFF
        times = 4
        extend(Event) {
            slots("msg", null, "I am message A!", "I am message B!", "I am message C!")
            cycle("dur", 1.0)
            cycle("renderMe", true)
        }
    }


    seq(s1, s1).play()
}

//// -------------------------------------------------

fun solve1() {
    Event.create {
        gate = Gate.ON_OFF
        dur = 4.0
        bumps = Circle.create { h=20.0 }
    }.play()
}

//// -------------------------------------------------

fun solve2() {
    val e1 = Event.create {
        dur = 2.0
        slot("h", 40.0)
        slot("a", 0.2)
        animate("radius") {
            value = 220.0
        }
    }

    val e2 = par(
        Event.create {
            dur=4.0
            slot("h", 220.0)
            animate("radius") {
                offsetDur = -3.0
//                fromValue = 20.0
                value = 20.0
                easing = Easing.QuadIn
            }
        },
        seq(
            Event.create {
                dur=2.9
                animate("a") {
                    easing = Easing.CubicInOut
                    fromValue = 0.0
                    value = 1.0
                }
            },
            Event.create {
                dur=1.1
                animate("a") {
                    easing = Easing.CubicInOut
                    fromValue = 1.0
                    value = 0.0
                }
            },
        )
    )

    EventRandom.seq(e1, e2) {
        bumps = Circle.create()
        gate = Gate.ON_OFF
        times = 8
    }.play()

}


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