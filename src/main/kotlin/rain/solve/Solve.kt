package rain.solve

import org.openrndr.animatable.easing.Easing
import rain.score.nodes.*
import rain.rndr.nodes.*

fun main()  {
    solve0()
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
        machine = Printer.create("PRINTER_1") { msg = "I am a $key" }
        gate = Gate.ON_OFF
        times = 4
//        extend(
//            Event.create {
//                dur = 1.0
//                this["machine.msg"] = "I am message A!"
//                this["machine.renderMe"] = true
//            },
//            Event.create {
//                dur = 1.0
//                this["machine.msg"] = "I am message B!"
//                this["machine.renderMe"] = true
//            },
//            Event.create {
//                dur = 1.0
//                this["machine.msg"] = "I am message C!"
//                this["machine.renderMe"] = true
//            }
//        )
        extend(Event) {
            slots("machine.msg", null, "I am message A!", "I am message B!", "I am message C!")
            cycle("dur", 1.0)
            cycle("machine.renderMe", true)
        }
    }
    seq(s1, s1).play(DEFAULT_SCORE.asHalfRes())
}

//// -------------------------------------------------



fun solve1() {

    Color.create("COLOR_1") { a = 1.0; s=0.9; v=0.9; h=190.0 }
    DrawStyle.create("STYLE_1") { fill("COLOR_1") }

    Event.create {
        machine = Circle.create {
            x = 32.5
            y = 18.5
        }
        gate = Gate.ON_OFF
        dur = 2.0
        style("STYLE_1")
    }.play(DEFAULT_SCORE.asHalfRes())
}

//// -------------------------------------------------

fun solve2() {

    DrawStyle.create("STYLE_1") {
        fill("COLOR_BLUE") { s=0.8; }
    }

    seq("FADE_BLUE",
        Event.create {
            dur=0.9
            animate("machine.a") {
                easing = Easing.CubicInOut
                fromValue = 0.0
                value = 1.0
            }
        },
        Event.create {
            dur=1.1
            animate("machine.a") {
                easing = Easing.CubicInOut
                fromValue = 1.0
                value = 0.0
            }
        },
    ) {
        machine = Color["COLOR_BLUE"]
    }

    DEFAULT_SCORE.play {

        val e1 = par(
            Event["FADE_BLUE"]!!,
            Event.create {
                dur = 2.0
                animate("machine.radius") {
                    fromValue = 1.0
                    value = 6.0
                    easing = Easing.QuadIn
                }
            }
        )

        val e2 = par(
            Event["FADE_BLUE"]!!,
            Event.create {
                dur = 2.0
                animate("machine.radius") {
                    fromValue = 16.0
                    value = 4.0
                    easing = Easing.QuadIn
                }
            },
        )

        seq(
            EventRandom.seq("YOMAMA",
                e1,
                e2
            ) {
                machine = Circle.create {
                    x = 32.5
                    y = 18.5
                }
                style("STYLE_1")
                gate = Gate.ON_OFF
                times = 8
            }
        )
    }

}


