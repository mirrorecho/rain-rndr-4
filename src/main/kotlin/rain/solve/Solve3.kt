package rain.solve


import org.openrndr.animatable.easing.Easing
import rain.graph.*
import rain.graph.queries.*
import rain.language.patterns.relationships.BUMPS
import rain.score.*
import rain.score.nodes.*
import rain.rndr.*
import rain.rndr.nodes.*
import kotlin.reflect.KMutableProperty1



fun solve3() {
    val startingPosition = Position.create { center(DEFAULT_SCORE) }


    val circleFly = par("CIRCLE_FLY",
        Event.create {
            dur=4.0
            slot("h", 220.0)
            animate("radius") {
                offsetDur = -3.0
                fromValue = 2.0
                value = 20.0
                easing = Easing.QuadIn
            }
        },
        seq(
            Event.create {
                dur=2.9
                animate("a") {
                    easing = Easing.CubicInOut
                    fromValue = 0.0
                    value = 1.0
                }
            },
            Event.create {
                dur=1.1
                animate("a") {
                    easing = Easing.CubicInOut
                    fromValue = 1.0
                    value = 0.0
                }
            },
        )
    )


    seq(
        pause(),
        EventRandom.seq(circleFly) {
            bumps = Circle.create {
                fromPosition = startingPosition
            }
            gate = Gate.ON_OFF
            times = 4
        },
    ).play(DEFAULT_SCORE.asHalfRes())

}