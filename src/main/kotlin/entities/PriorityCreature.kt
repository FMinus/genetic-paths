package entities

import space.World
import space.WorldSlot
import javafx.scene.paint.Color

data class PriorityCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE
) : Creature(index, name, world, currentSlot, currentEnergy) {

    override val kind: String = KIND
    override val color: Color = Color.DARKGRAY
    override val priority = 99.0

    companion object {
        const val KIND = "Priority"
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)
}