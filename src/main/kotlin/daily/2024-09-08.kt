package daily

import org.openrndr.animatable.easing.Easing
import org.openrndr.extra.noise.random
import rain.rndr.nodes.Circle
import rain.rndr.nodes.ValueRandom
import rain.rndr.relationships.X
import rain.score.DEFAULT_SCORE
import rain.score.nodes.*

/*

TOOLS AVAILABLE:
 - draw circles
 - specify the color (hsva), radius, and position
 - animate any of the above
 */

fun main() {

    val circles = par("CIRCLES") {

        (0..DEFAULT_SCORE.widthUnits.toInt()).forEach {cX ->
            (0..DEFAULT_SCORE.heightUnits.toInt()).forEach {cY->
                extend(
                    Event.create {
                        dur=40.0
                        animate("radius") {
                            fromValue = 0.02
                            value = random(2.0, 9.0)
                            easing = Easing.SineIn
                        }
                        animate("a") {
                            fromValue = 0.3
                            value = random(0.0, 0.6)
                            easing = Easing.CubicInOut
                        }
                        animate("h") {
                            fromValue = 20.0
                            value = random(11.0, 49.0)
                            easing = Easing.CubicIn
                        }

                        animate("s") {
                            fromValue = 0.6
                            value = random(0.4, 0.8)
                        }
                        animate("x") {
                            fromValue = cX.toDouble()
                            value = cX.toDouble() + random(-2.0, 12.0)
                            easing = Easing.QuadOut
                        }

                        animate("y") {
                            fromValue = cY.toDouble()
                            value = cY.toDouble() + random(-2.0, 2.0)
                            easing = Easing.QuadOut
                        }

                        bumps = Circle.create()
                        gate = Gate.ON_OFF

                    }
                )
            }
        }

    }


    seq(
        pause(),
        seq(circles) {
//            bumps = Circle.create {
////                this.relate(X, ValueRandom.create { maxValue = DEFAULT_SCORE.widthUnits }, true, )
////                fromPosition = startingPosition
//            }
            gate = Gate.ON_OFF
//            times = 4
        },
    ).play(DEFAULT_SCORE)


}