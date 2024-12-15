package rain.rndr.nodes

import org.openrndr.extra.noise.random
import rain.graph.*
import rain.rndr.relationships.*
import rain.score.nodes.*
import rain.utils.*


open class ValueRandom protected constructor(
    key:String = autoKey(),
    ) : Value(key) {
    companion object : NodeLabel<Value, ValueRandom>(
        Value, ValueRandom::class, { k -> ValueRandom(k) }
    )

    override val label: Label<out Value, out ValueRandom> = ValueRandom

    var localMinValue = 0.0
    var localMaxValue = 1.0
    var localWalkValue = 0.0


    var minValue by RespondingPropertySlot("minValue", ::localMinValue, +MIN_VALUE)
    var maxValue by RespondingPropertySlot("maxValue", ::localMaxValue, +MAX_VALUE)
    var walkValue by RespondingPropertySlot("walkValue", ::localWalkValue, +WALK_VALUE)

    fun randomize() {
        value = random(minValue, maxValue)
    }


    override fun bump(context: Score.ScoreContext) {
        super.bump(context)
        if (eventSlotIs("randomize")) randomize()

    }

    override val isAnimating: Boolean
        get() = hasAnimations() || walkValue != 0.0

    override fun updateAnimation(context: Score.ScoreContext) {
        super.updateAnimation(context)
        value += walkValue
    }

}

