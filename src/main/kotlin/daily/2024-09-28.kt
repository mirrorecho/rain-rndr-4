package daily

import org.openrndr.draw.loadFont
import rain.rndr.nodes.Color
import rain.rndr.nodes.Text
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
 - summing value chains

 */


fun main() {

    DEFAULT_SCORE.asHalfRes().play {

        val msg1 = Text.create {
            center(this@play)
            y-=2.0
        }
        val msg2 = Text.create {
            center(this@play)
            y+=2.0
        }

        val random1 = EventRandom.seq(
            Event.create { this["machine.text"]="RAIN"; dur=1.0 },
            Event.create { this["machine.text"]="FAST"; dur=1.0 },
        ) {
            times = 3
            machine = msg1
            gate = Gate.ON_OFF
        }
        val random2 = EventRandom.seq(
            Event.create { this["machine.text"]="SNOW"; dur=1.0 },
            Event.create { this["machine.text"]="SLOW"; dur=1.0 },
        ) {
            times = 3
            machine = msg2
            gate = Gate.ON_OFF
        }

        par(
            random1,
            random2
        ){
            style {
                fontMap = loadFont("data/fonts/default.otf", 48.0)
                fill = Color.white()
            }
        }

    }





}