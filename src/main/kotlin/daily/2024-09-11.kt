package daily

import org.openrndr.animatable.easing.Easing
import org.openrndr.extra.noise.random
import rain.rndr.nodes.*
import rain.score.nodes.DEFAULT_SCORE
import rain.score.nodes.*

/*

TOOLS AVAILABLE:
(existing)
 - draw circles
 - specify the color (hsva), radius, and position
 - animate any of the above
 - draw rectangles
 - draw lines
 (new)
  - show images

 */


fun main() {


    fun eventCircleRandomMove(dur: Double = 2.0) = Event.create {
        this.dur=dur

        animate("machine.x") {
            value = 0.0 + random(0.0, 64.0)
            easing = Easing.QuadOut
        }

        animate("machine.y") {
            value = 0.0 + random(0.0, 36.0)
            easing = Easing.QuadIn
        }

        animate("style.h") {
            value = random(220.0,290.0)
            easing = Easing.CubicIn
        }

        animate("machine.radius") {
            value = random(0.0,12.0)
            easing = Easing.SineIn
        }

    }

    fun eventSeq() = seq(
        eventCircleRandomMove(4.0),
        eventCircleRandomMove(1.0),
        eventCircleRandomMove(),
        eventCircleRandomMove(),
        eventCircleRandomMove(0.5),
        eventCircleRandomMove(0.5),
        eventCircleRandomMove(0.5),
        eventCircleRandomMove(0.2),
        eventCircleRandomMove(0.2),
        eventCircleRandomMove(0.2),
    )
    {
        machine = Circle.create {
            x = random(0.0, 64.0)
            y = random(0.0, 36.0)
//            a = 0.2
        }
        gate = Gate.ON_OFF
    }


    seq(
        pause(),
        par(
            eventSeq(),
            seq(pause(), eventSeq()),
            seq(pause(2.0), eventSeq()),
            seq(pause(3.0), eventSeq()),
            seq(pause(4.0), eventSeq()),
            seq(pause(4.5), eventSeq()),
            seq(pause(4.8), eventSeq()),
            seq(pause(4.9), eventSeq()),
            seq(pause(5.0), eventSeq()),
            seq(pause(5.0), eventSeq()),
            seq(pause(5.0), eventSeq()),
        ),
        pause(2.0),
    ).play(DEFAULT_SCORE.asHalfRes())


}