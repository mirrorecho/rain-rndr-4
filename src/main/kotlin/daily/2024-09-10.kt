package daily

import org.openrndr.animatable.easing.Easing
import rain.rndr.nodes.*
import rain.score.DEFAULT_SCORE
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


        // TODO: a function to consolidate this
        val line1 = Line.create {
            center(this@play)

            lineTo = LinePoint.create {
                x = 2.0
                y = 2.0

                this.fromPosition = fromPosition
                lineTo = LinePoint.create {
                    this.fromPosition = fromPosition2
                    lineTo = LinePoint.create {
                        x = -1.0
                        y = 8.0
                        this.fromPosition = fromPosition
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
                gate = Gate.ON_OFF
            },
            Event.create {
                dur = 24.0
                machine = fromPosition
                gate = Gate.ON_OFF
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