package entities

import space.World
import space.WorldSlot
import javafx.scene.paint.Color

data class FleeingCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE
) : Creature(index, name, world, currentSlot, currentEnergy) {
    override val kind: String = KIND

    override val energySpentPerMove: Double = .75 * DEFAULT_ENERGY_SPENT_PER_MOVE

    override val color: Color = Color.DARKBLUE

    override fun selectSlotToMoveInto(): WorldSlot? {
        val adjucantSlots = world.kingSight(currentSlot)
        val predators = world.kingSight(2, currentSlot).filter { it.hasCreature(PredatorCreature.KIND) }
        val dangerSlots = predators.map { world.kingSight(1, it) }.flatMap { it }
        val safeSlots = world.kingSight(currentSlot).minus(dangerSlots)
        if(safeSlots.isEmpty()){
            return slotSelector(adjucantSlots)
        }
        return slotSelector(safeSlots)
    }

    override fun preMove(slotToMoveInto: WorldSlot) {
        if(slotToMoveInto.hasCreature()){
            if(random.chanceTo(50)){
                grantEnergy(slotToMoveInto.creatureInSlot!!.currentEnergy / 2)
                slotToMoveInto.creatureInSlot!!.currentEnergy = 0.0
            }
        }
    }

    override fun replicateInto(): Pair<String, Int> {
        return SightCreature.KIND to 10
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)

    companion object {
        val KIND = "Fleeing"
    }
}