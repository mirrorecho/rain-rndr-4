package rain.rndr.nodes

import org.openrndr.extra.noise.random
import rain.graph.*
import rain.rndr.relationships.*
import rain.score.nodes.*
import rain.utils.*


class ValueRandom private constructor(
    key:String = autoKey(),
    ) : Machine(key) {
    companion object : NodeLabel<Machine, ValueRandom>(
        Machine, ValueRandom::class, { k -> ValueRandom(k) }
    )

    override val label: Label<out Machine, out ValueRandom> = ValueRandom

    class ValueRandomAnimation: MachineAnimation() {
        var value = 0.0
        var minValue = 0.0
        var maxValue = 1.0
        var walkValue = 0.0
    }

    override val animation: ValueRandomAnimation = ValueRandomAnimation()

    var minValue by RespondingPropertySlot(animation::minValue, +MIN_VALUE)
    var maxValue by RespondingPropertySlot(animation::maxValue, +MAX_VALUE)
    var walkValue by RespondingPropertySlot(animation::maxValue, +WALK_VALUE)

    var value by RespondingPropertySlot(animation::value, +VALUE)

    fun randomize() {
        value = random(minValue, maxValue)
    }


    override fun bump(context: Score.ScoreContext) {

        super.bump(context)
        if (eventSlotIs("randomize")) randomize()

    }


    override fun updateAnimation(context: Score.ScoreContext) {
        super.updateAnimation(context)
        value += walkValue
    }

}

