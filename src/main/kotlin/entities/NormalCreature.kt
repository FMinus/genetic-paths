package entities

import javafx.scene.paint.Color
import space.World
import space.WorldSlot

data class NormalCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE
) : Creature(index, name, world, currentSlot, currentEnergy) {

    override var daysSurvived: Int = 0
    override val color: Color = Color.RED

    override val kind: String = KIND

    companion object {
        const val KIND = "Normal"
    }

    override fun replicateInto(): Pair<String, Int> {
        return FastCreature.KIND to 20
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)
}