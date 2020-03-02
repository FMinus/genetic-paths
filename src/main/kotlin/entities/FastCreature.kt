package entities

import space.World
import space.WorldSlot
import javafx.scene.paint.Color

data class FastCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE
) : Creature(index, name, world, currentSlot, currentEnergy) {

    override val kind: String = KIND
    override val color: Color = Color.LIME
    override val energySpentPerMove: Double = .75
    override val priority = 5.0

    /* move twice */
    override fun move() {
        super.move()
        super.move()
    }

    companion object {
        const val KIND = "Fast"
    }

    override fun replicateInto(): Pair<String, Int> {
        return SightCreature.KIND to 10
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)
}