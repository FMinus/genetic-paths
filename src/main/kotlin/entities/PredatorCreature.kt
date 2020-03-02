package entities

import javafx.scene.paint.Color
import space.World
import space.WorldSlot
import space.noCreatureSlots

val CHANCE_TO_PREY = 80

data class PredatorCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE
) : Creature(index, name, world, currentSlot, currentEnergy) {
    override val kind: String = KIND

    override val energySpentPerMove: Double = 1.5 * DEFAULT_ENERGY_SPENT_PER_MOVE
    override val energyGainPerFood = INITIAL_ENERGY_VALUE / 3
    override val color: Color = Color.ORANGE


    override fun selectSlotToMoveInto(): WorldSlot? {
        val adjucantSlots = world.crossSight(currentSlot)
        val prey = adjucantSlots.filter { it.hasCreature() && !it.hasCreature(this.kind) }
        if (prey.isEmpty()) {
            return slotSelector(adjucantSlots.noCreatureSlots())
        }
        return slotSelector(prey)
    }

    override fun preMove(slotToMoveInto: WorldSlot) {
        if (slotToMoveInto.hasCreature() && random.chanceTo(CHANCE_TO_PREY)) {
            grantEnergy(slotToMoveInto.creatureEnergy() / 2)
            world.killCreature(slotToMoveInto.creatureInSlot!!)
        }
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)

    companion object {
        val KIND = "Predator"
    }
}