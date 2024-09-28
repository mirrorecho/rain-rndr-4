package daily

import org.openrndr.extra.noise.random
import rain.rndr.nodes.Line
import rain.rndr.nodes.LinePoint
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
 - show images
 (new)
 - more sophisticated draw style animations

 */


fun main() {

    DEFAULT_SCORE.asHalfRes().play {

        val er = EventRandom.seq {
            (0..1000).forEach { i ->
                extend(
                    Event.create {
                        dur = random(2.0, 4.0)
                        gate = Gate.ON_OFF
                        machine = Line.create {
                            x = random(0.0, 64.0)
                            y = random(0.0, 36.0)
                            lineTo = LinePoint.create {
                                x = random(0.0, 64.0)
                                y = random(0.0, 36.0)
                            }
                        }
                    }
                )
            }
        }

        par(er, er, er) {
            style {
                stroke { a=0.8; v=0.8; }
                strokeWeight = 0.4
            }
        }

    }





}