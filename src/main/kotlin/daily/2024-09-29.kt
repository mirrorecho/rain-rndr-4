package daily

import org.openrndr.animatable.easing.Easing
import org.openrndr.extra.noise.random
import rain.language.patterns.relationships.DIRTIES
import rain.rndr.nodes.*
import rain.rndr.relationships.*
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
 - more sophisticated draw style animations
 - text
 (new)
 - responding value chains

 */


fun main() {

    DEFAULT_SCORE.asHalfRes().play {

        // TODO: make a helper function for this
//        val scaling = Value.create {
//            respondBlock = { _, sv-> sv * this.value }
//        }

        fun circleEvents(count:Int=9): Array<Event> =
            arrayListOf<Event>(). apply {
                (1..count).forEach { _ ->
                    add(Event.create {
                        gate = Gate.ON_OFF
                        dur = 18.0
                        machine = Circle.create {
                            radius = random(0.0, 9.0)
                            x = random(0.0, 65.0)
                            y = random(0.0, 37.0)
//                            this.relate(RADIUS, scaling)
                            val xRandom = ValueRandom.create {
                                walkValue = 0.01
                                minValue = 9.0
                                maxValue = 64.0
                                randomize()
                            }
//                            // TODO: helper fun for relate + relate DIRTIES with direction left
                            this.relate(X, xRandom)
                            this.relate(DIRTIES, xRandom, false)
                        }
                        style {
                            fill {
                                h = random(0.0, 360.0)
                                a = 0.9
                                v = 0.9
                                s = 0.9
//                                this.relate(A, scaling)
//                                this.relate(H, scaling)
//                                this.relate(DIRTIES, scaling, false)
                            }
                        }
                    })
                }
            }.toTypedArray()

        par(
            // TODO: helper for animation sequences
//            seq(
//                Event.create {
//                    dur = 9.0
//                    animate("machine.value") {
//                        value = 0.9
//                        easing = Easing.SineInOut
//                    }
//                },
//                Event.create {
//                    dur = 9.0
//                    animate("machine.value") {
//                        value = 0.0
//                        easing = Easing.SineInOut
//                    }
//                },
//            ) { machine = scaling },
            *circleEvents(220)
        )

    }


}