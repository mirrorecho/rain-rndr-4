package daily

import org.openrndr.animatable.easing.Easing
import org.openrndr.extra.noise.random
import rain.rndr.nodes.DrawStyle
import rain.rndr.nodes.Rectangle
import rain.score.nodes.DEFAULT_SCORE
import rain.score.nodes.*
import rain.utils.autoKey

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

    fun eventSquareRandomMove(dur: Double = 2.0, key:String= autoKey()) = Event.create(key) {
        this.dur=dur

        style {
            fill { a=0.8; s=0.8; h= random(129.0, 144.0) }
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

    fun eventSeq(prefix:String) = seq("$prefix-SEQ",

        eventSquareRandomMove(4.0, "$prefix-REC1"),
        eventSquareRandomMove(1.0, "$prefix-REC2"),
        eventSquareRandomMove(2.0, "$prefix-REC3"),
        eventSquareRandomMove(2.0, "$prefix-REC4"),
        eventSquareRandomMove(0.5, "$prefix-REC5"),
        eventSquareRandomMove(0.5, "$prefix-REC6"),
        eventSquareRandomMove(0.5, "$prefix-REC7"),
        eventSquareRandomMove(0.2, "$prefix-REC8"),
        eventSquareRandomMove(0.2, "$prefix-REC9"),
        eventSquareRandomMove(0.2),
    )
        {
            machine = Rectangle.create {
                width = 0.0
                height = 0.0
                x = random(0.0, 64.0)
                y = random(0.0, 36.0)
            }
            gate = Gate.ON_OFF
        }

    val anEvent = eventSeq("Yo")
    println(anEvent.children.first().drawStyle?.fill?.a)


    seq(
        pause(),
        par(
            eventSeq("0A"),
            seq(pause(), eventSeq("0B")),
            seq(pause(2.0), eventSeq("A")),
            seq(pause(3.0), eventSeq("B")),
            seq(pause(4.0), eventSeq("C")),
            seq(pause(4.5), eventSeq("D")),
            seq(pause(4.8), eventSeq("E")),
            seq(pause(4.9), eventSeq("F")),
            seq(pause(5.0), eventSeq("G")),
            seq(pause(5.0), eventSeq("H")),
            seq(pause(5.0), eventSeq("I")),
        ),
        pause(2.0),
    )
        .play(DEFAULT_SCORE.asHalfRes())




}