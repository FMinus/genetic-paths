package entities

import javafx.scene.paint.Color
import space.World
import space.WorldSlot
import space.foodSlots
import space.noCreatureSlots

data class SightCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE
) : Creature(index, name, world, currentSlot, currentEnergy) {
    override val kind: String = KIND

    override val energySpentPerMove: Double = DEFAULT_ENERGY_SPENT_PER_MOVE * 1.5

    override val color: Color = Color.BLUE

    override fun selectSlotToMoveInto(): WorldSlot? {
        val adjucantSlots = world.kingSight(currentSlot).noCreatureSlots()
        val foodSlots = adjucantSlots.foodSlots()
        if (foodSlots.isEmpty()) {
            val foodInSight = world.kingSight(2, currentSlot).foodSlots()
            return if (foodInSight.isNotEmpty() && adjucantSlots.isNotEmpty()) {
                adjucantSlots.minBy { it.distanceTo(foodInSight.first()) }
            } else {
                slotSelector(adjucantSlots)
            }
        }
        return slotSelector(foodSlots)
    }

    override fun replicateInto(): Pair<String, Int> {
        return PredatorCreature.KIND to 10
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)

    companion object {
        val KIND = "Sight"
    }
}