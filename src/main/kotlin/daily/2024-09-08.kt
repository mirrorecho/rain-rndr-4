package daily

import org.openrndr.animatable.easing.Easing
import org.openrndr.extra.noise.random
import rain.rndr.nodes.Circle
import rain.rndr.nodes.Color
import rain.rndr.nodes.DrawStyle
import rain.score.DEFAULT_SCORE
import rain.score.nodes.*

/*

TOOLS AVAILABLE:
(existing)
 - draw circles
 - specify the color (hsva), radius, and position
 - animate any of the above

 */

fun main() {

    val circles = par("CIRCLES") {

        val strokeColor = Color.white()

        // TODO/WARNING: full size causes stack overflow error... WHY?!
        (0..DEFAULT_SCORE.widthUnits.toInt()/2).forEach {cX ->
            (0..DEFAULT_SCORE.heightUnits.toInt()/2).forEach {cY->

                val fillColor = Color.create { v = 1.0;}
                val myStyle = DrawStyle.create {
                    strokeWeight = 0.6
                    fill = fillColor
//                    fill = Color.white()
                    stroke = strokeColor
                }

                // animate not working... why?
                val colorAnimate = Event.create {
                    dur=29.0
                    machine = fillColor
                    animate("machine.a") {
                        fromValue = 1.0
                        value = random(0.0, 0.6)
                        easing = Easing.CubicInOut
                    }
//                    animate("machine.h") {
//                        fromValue = 20.0
//                        value = random(11.0, 166.0)
//                        easing = Easing.CubicIn
//                    }
                    animate("machine.s") {
                        fromValue = 1.0
                        value = random(0.2, 0.8)
                    }
                }

                val circleAnimate = Event.create {
                    dur=29.0
                    machine = Circle.create()
                    gate = Gate.ON
                    drawStyle = myStyle

                    animate("style.strokeWeight") {
//                        fromValue = 0.6
                        value = random(0.0, 0.01)
                        easing = Easing.SineIn
                    }

                    animate("style.fill.h") {
                        fromValue = 20.0
                        value = random(11.0, 166.0)
                        easing = Easing.CubicIn
                    }

                    animate("machine.radius") {
                        fromValue = 0.02
                        value = random(2.0, 9.0)
                        easing = Easing.SineOut
                    }
                    animate("machine.x") {
                        fromValue = cX.toDouble() * 2
                        value = cX.toDouble() * 2 + random(-2.0, 12.0)
                        easing = Easing.QuadOut
                    }
                    animate("machine.y") {
                        fromValue = cY.toDouble() * 2
                        value = cY.toDouble() * 2 + random(-2.0, 2.0)
                        easing = Easing.QuadOut
                    }
                }


                extend(
                    colorAnimate,
                    circleAnimate
                )
            }
        }

    }

    seq(circles).play(DEFAULT_SCORE)

}