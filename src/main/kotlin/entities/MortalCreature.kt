package entities

import javafx.scene.paint.Color
import space.World
import space.WorldSlot

data class MortalCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE
) : Creature(index, name, world, currentSlot, currentEnergy) {

    override var daysSurvived: Int = 0
    override val color: Color = Color.PURPLE
    override val chanceToReplicate = 30
    override val kind: String = KIND

    /** lives a fraction of other creatures **/
    override fun preMove(slotToMoveInto: WorldSlot) {
        if (daysSurvived > MAX_LIFE_TIME / 5 && random.chanceTo(95)) {
            die()
        }
    }

    override fun replicateInto(): Pair<String, Int> {
        return NormalCreature.KIND to 15
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)

    companion object {
        val KIND = "Mortal"
    }
}