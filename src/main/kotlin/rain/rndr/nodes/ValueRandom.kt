package rain.rndr.nodes

import org.openrndr.Program
import org.openrndr.extra.noise.random
import rain.graph.Label
import rain.graph.NodeLabel
import rain.score.Score
import rain.score.ScoreContext
import rain.score.nodes.Machine
import rain.score.nodes.MachineAnimation
import rain.utils.*


open class ValueRandom(
    key:String = autoKey(),
    ) : Machine(key) {
    companion object : NodeLabel<Machine, ValueRandom>(
        Machine, ValueRandom::class, { k -> ValueRandom(k) }
    )

    override val label: Label<out Machine, out ValueRandom> = ValueRandom

    class ValueRandomAnimation: MachineAnimation() {
        var minValue = 0.0
        var maxValue = 1.0

        var value: Double
            get() = random(minValue, maxValue)
            set(value) { maxValue = value }
    }

    val valueRandomAnimation = ValueRandomAnimation()
    override val machineAnimation = valueRandomAnimation

    var minValue by PropertySlot(valueRandomAnimation::minValue)
    var maxValue by PropertySlot(valueRandomAnimation::maxValue)
    val value by PropertySlot(valueRandomAnimation::value)

    override fun render(context: ScoreContext) {
        valueRandomAnimation.updateAnimation()
    }


}

