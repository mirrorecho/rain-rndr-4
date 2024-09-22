package daily

import org.openrndr.animatable.easing.Easing
import rain.rndr.nodes.*
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
 (new)
  - show images

 */


fun main() {

    DEFAULT_SCORE.play {
        EventRandom.seq(
            Event.create {
                dur = 0.2
                gate = Gate.ON_OFF
                animate("machine.x") { fromValue=-12.0; value=-21.0; easing=Easing.QuadInOut }
                animate("machine.y") { fromValue=-12.0; value=-8.0; easing=Easing.SineIn }
                machine = Image.create {
                    imagePath = "data/images/plants/PXL_20240809_235408758.jpg"
                    load()
                }
            },
            Event.create {
                dur = 0.2
                gate = Gate.ON_OFF
                animate("machine.x") { fromValue=-12.0; value=-3.0; easing=Easing.QuadInOut }
                animate("machine.y") { fromValue=-12.0; value=0.0; easing=Easing.SineIn }
                machine = Image.create {
                    imagePath = "data/images/plants/PXL_20240810_005131814.jpg"
                    load()
                }
            },
            Event.create {
                dur = 0.2
                gate = Gate.ON_OFF
                animate("machine.x") { fromValue=-12.0; value=-18.0; easing=Easing.SineOut }
                animate("machine.y") { fromValue=-12.0; value=-10.0; easing=Easing.CubicOut }
                machine = Image.create {
                    imagePath = "data/images/plants/PXL_20240825_001120920.jpg"
                    load()
                }
            },
            Event.create {
                dur = 0.2
                gate = Gate.ON_OFF
                animate("machine.x") { fromValue=-12.0; value=3.0; easing=Easing.CubicIn }
                animate("machine.y") { fromValue=-12.0; value=0.0; easing=Easing.CubicOut }
                machine = Image.create {
                    imagePath = "data/images/plants/PXL_20240825_005054169.jpg"
                    load()
                }
            },
            Event.create {
                dur = 0.2
                gate = Gate.ON_OFF
                animate("machine.x") { fromValue=-12.0; value=0.0; easing=Easing.QuadInOut }
                animate("machine.y") { fromValue=-12.0; value=-21.0; easing=Easing.CubicOut }
                machine = Image.create {
                    imagePath = "data/images/plants/PXL_20240825_005352166.jpg"
                    load()
                }
            },
        ) {
            times = 80
        }
    }


}