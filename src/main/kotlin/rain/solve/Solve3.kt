package rain.solve


import org.openrndr.animatable.easing.Easing
import org.openrndr.extra.noise.random
import rain.score.*
import rain.score.nodes.*
import rain.rndr.nodes.*
import rain.rndr.relationships.X


fun solve3() {
    val startingPosition = Position.create { center(DEFAULT_SCORE) }


    val circleFly = par("CIRCLE_FLY",
        Event.create {
            dur=4.0
            slot("style.h", 220.0)
            animate("x") {
            }
            animate("machine.radius") {
                offsetDur = -3.0
                fromValue = 0.4
                value = 20.0
                easing = Easing.QuadIn
            }
        },
        seq(
            Event.create {
                dur=2.9
                animate("style.a") {
                    easing = Easing.CubicInOut
                    fromValue = 0.0
                    value = 1.0
                }
            },
            Event.create {
                dur=1.1
                animate("style.a") {
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
            machine = Circle.create {
                this.relate(X, Value.create { value=random(0.0, DEFAULT_SCORE.widthUnits)  }, true, )
            //                this.relate(X, ValueRandom.create { maxValue = DEFAULT_SCORE.widthUnits }, true, )
//                fromPosition = startingPosition
            }
            gate = Gate.ON_OFF
            times = 4
        },
    ).play(DEFAULT_SCORE.asHalfRes())

}