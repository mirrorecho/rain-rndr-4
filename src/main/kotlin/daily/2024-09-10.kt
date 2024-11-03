package daily

import org.openrndr.animatable.easing.Easing
import rain.rndr.nodes.*
import rain.rndr.relationships.*
import rain.score.nodes.DEFAULT_SCORE
import rain.score.nodes.*

/*

TOOLS AVAILABLE:
(existing)
 - draw circles
 - specify the color (hsva), radius, and position
 - animate any of the above
 - draw rectangles
 (new)
 - draw lines

 */

fun main() {

    DEFAULT_SCORE.asHalfRes().play {

        val fromPosition = Position.create { x = 20.0; y = 20.0 }

        val fromPosition2 = Position.create { x = 22.0; y = 22.0 }

        val fromPosition3 = Position.create {
            x = -1.0
            y = 8.0

            relate(X, fromPosition2)
            relate(Y, fromPosition2)

            respondBlock = { n, sv ->
                val value = when(n) {
                    "x" ->  20.0 + this.slot<Double>(n)!!.value + sv
                    else -> this.slot<Double>(n)!!.value + sv
                }
                value
            }

        }

        // TODO: a function to consolidate this
        val line1 = Line.create {
            center(this@play)

            lineTo = LinePoint.create {
                x = 2.0
                y = 2.0

                // TODO, easy method to relate both to position
                relate(X, fromPosition)
                relate(Y, fromPosition)

                lineTo = LinePoint.create {

                    relate(X, fromPosition2)
                    relate(Y, fromPosition2)

                    lineTo = LinePoint.create {

                        relate(X, fromPosition3)
                        relate(Y, fromPosition3)

                        lineTo = LinePoint.create {
                            center(this@play)
                        }
                    }
                }
            }


        }

        val makeLine = seq(
            Event.create {
                dur = 3.0
                machine = fromPosition2
                animate("machine.x") {
                    fromValue = 22.0
                    value = 8.0
                    easing = Easing.CubicIn
                }
                animate("machine.y") {
                    fromValue = 34.0
                    value = 8.0
                    easing = Easing.CubicOut
                }
            },
            Event.create {
                dur = 3.0
                machine = fromPosition2
                animate("machine.x") {
                    value = 22.0
                    easing = Easing.CubicIn
                }
                animate("machine.y") {
                    value = 34.0
                    easing = Easing.CubicOut
                }
            },

            )



        par(
            Event.create {
                dur = 24.0
                machine = line1
                gate = Gate.ON_OFF
                style {
                    strokeWeight = 2.0
                    stroke {a = 0.6; h=120.0; s=0.8}
                }
            },
            seq(makeLine, makeLine, makeLine, makeLine) {
                machine = fromPosition2
            },
            Event.create {
                dur = 24.0
                machine = fromPosition
                animate("machine.x") {
                    value = 40.0
                    easing = Easing.CubicIn
                }
                animate("machine.y") {
                    value = 9.0
                    easing = Easing.CubicOut
                }
            },
        )
    }


}