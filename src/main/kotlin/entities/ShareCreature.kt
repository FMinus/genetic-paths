package entities

import javafx.scene.paint.Color
import space.World
import space.WorldSlot

data class ShareCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE
) : Creature(index, name, world, currentSlot, currentEnergy) {
    override val kind: String = KIND

    override val energySpentPerMove: Double = 1.75 * DEFAULT_ENERGY_SPENT_PER_MOVE

    override val color: Color = Color.CHOCOLATE

    override val chanceToReplicate: Int = 5

    override fun preMove(slotToMoveInto: WorldSlot) {
        val sameKind = world.kingSight(1, currentSlot)
            .filter { it.hasCreature(this.kind) && daysSurvived == it.creatureInSlot!!.daysSurvived }
        if (sameKind.isNotEmpty()) {
            var groupEnergyAverage = (sameKind.map { it.creatureEnergy() }.sum() + this.currentEnergy) / sameKind.size
            if (groupEnergyAverage > 50.0) {
                groupEnergyAverage = 50.0
            }
            currentEnergy = groupEnergyAverage
            sameKind.forEach {
                it.creatureInSlot!!.currentEnergy = groupEnergyAverage
            }
        }
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)

    companion object {
        val KIND = "Share"
    }
}