package daily

import org.openrndr.animatable.easing.Easing
import org.openrndr.extra.noise.random
import rain.rndr.nodes.Circle
import rain.rndr.nodes.Rectangle
import rain.score.DEFAULT_SCORE
import rain.score.nodes.*

/*

TOOLS AVAILABLE:
(existing)
 - draw circles
 - specify the color (hsva), radius, and position
 - animate any of the above
 (new)
  - draw rectangles
 */

fun main() {

    fun eventSquareRandomMove(dur: Double = 2.0) = Event.create {
        this.dur=dur

        // associates a new style to this Event (or updates existing)
        style {
            fill { a=0.8; s=0.8;}
            stroke { a=0.8; }
        }

        // creates a slot that updates the applicable slot (this or parent), at the time of execution

        animate("machine.x") {
            value = 0.0 + random(0.0, 64.0)
            easing = Easing.QuadOut
        }

        animate("machine.y") {
            value = 0.0 + random(0.0, 36.0)
            easing = Easing.QuadIn
        }

        animate("machine.width") {
            value = random(0.0,12.0)
            easing = Easing.SineIn
        }
        animate("machine.height") {
            value = random(0.0,12.0)
            easing = Easing.SineOut
        }

        animate("style.stroke") {
            value = random(0.0,2.0)
            easing = Easing.CubicIn
        }

        animate("style.fill.h") {
            value = random(0.0,90.0)
            easing = Easing.CubicOut
        }


    }

    fun eventSeq() = seq(

        eventSquareRandomMove(4.0),
        eventSquareRandomMove(1.0),
        eventSquareRandomMove(),
        eventSquareRandomMove(),
        eventSquareRandomMove(0.5),
        eventSquareRandomMove(0.5),
        eventSquareRandomMove(0.5),
        eventSquareRandomMove(0.2),
        eventSquareRandomMove(0.2),
        eventSquareRandomMove(0.2),
    )
        {
            bumps = Rectangle.create {
                width = 0.0
                height = 0.0
                x = random(0.0, 64.0)
                y = random(0.0, 36.0)
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