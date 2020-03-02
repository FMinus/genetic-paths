package entities

import javafx.scene.paint.Color
import space.World
import space.WorldSlot

data class HistoricCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE
) : Creature(index, name, world, currentSlot, currentEnergy) {

    override val kind: String = KIND
    override val energySpentPerMove: Double = DEFAULT_ENERGY_SPENT_PER_MOVE * 1.1
    override val color: Color = Color.DARKTURQUOISE

    val visitedSlots: MutableList<WorldSlot> = mutableListOf()


    override fun selectSlotToMoveInto(): WorldSlot? {
        val possibilities = world.crossSight(currentSlot)
        val excludeVisited = possibilities.minus(visitedSlots)
        if (excludeVisited.isEmpty()) {
            return randomSlot(possibilities)
        }
        return randomSlot(excludeVisited)
    }

    override fun preMove(slotToMoveInto: WorldSlot) {
        visitedSlots.add(slotToMoveInto)
        if (visitedSlots.size >= 9) {
            visitedSlots.removeAt(0)
        }
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)

    companion object {
        val KIND = "Historic"
    }
}