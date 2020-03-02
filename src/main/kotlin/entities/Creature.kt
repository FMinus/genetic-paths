package entities

import RandomGenerator
import javafx.scene.paint.Color
import space.World
import space.WorldSlot
import space.noCreatureSlots

val INITIAL_ENERGY_VALUE = 20.0
val DEFAULT_MUTATION_CHANCE = 5
val DEFAULT_REPLICATION_CHANCE = 5
val DEFAULT_ENERGY_SPENT_PER_MOVE = 1.0
val MAX_ENERGY = 50.0
val MAX_LIFE_TIME = 60

abstract class Creature(
    open val index: Int,
    open val name: String,
    open val world: World,
    open var currentSlot: WorldSlot,
    open var currentEnergy: Double = INITIAL_ENERGY_VALUE,
    val random: RandomGenerator = RandomGenerator()
) {

    abstract val color: Color
    abstract val kind: String
    open var daysSurvived: Int = 0

    open val chanceToReplicate = DEFAULT_REPLICATION_CHANCE
    open val energySpentPerMove: Double = DEFAULT_ENERGY_SPENT_PER_MOVE
    open val energyGainPerFood = INITIAL_ENERGY_VALUE / 2
    open val priority = 1.0

    open fun additionalEnergySpent() = 0.0

    private fun move(newSlot: WorldSlot) {
        eat(newSlot)
        newSlot.creatureInSlot = this
        currentSlot.creatureInSlot = null
        currentSlot = newSlot
    }

    private fun eat(slot: WorldSlot){
        if (slot.hasFood) {
            grantEnergy(energyGainPerFood)
            slot.hasFood = false
        }
    }

    fun randomSlot(availableSlot: List<WorldSlot>): WorldSlot? {
        return random.of(availableSlot)
    }

    /** randomly selects slot to move to **/
    open fun move() {
        if (daysSurvived > MAX_LIFE_TIME) {
            die()
        } else {
            val slotToMoveInto = selectSlotToMoveInto()
            currentEnergy -= energySpentPerMove + additionalEnergySpent()
            if (slotToMoveInto != null) {
                preMove(slotToMoveInto)
                move(slotToMoveInto)
            }
        }
    }

    open fun preMove(slotToMoveInto: WorldSlot) {}

    open fun selectSlotToMoveInto(): WorldSlot? {
        val adjucantSlots = world.crossSight(currentSlot).noCreatureSlots()
        if (adjucantSlots.isNotEmpty()) {
            return randomSlot(adjucantSlots)
        }
        return null
    }

    fun slotSelector(slots: List<WorldSlot>, slotFilter: (WorldSlot) -> Boolean = { true }): WorldSlot? {
        if (slots.isEmpty()) {
            return null
        }
        val filtredSlots = slots.filter { slotFilter(it) }
        return if (filtredSlots.size == 1) {
            filtredSlots.first()
        } else if (filtredSlots.isEmpty()) {
            randomSlot(slots)
        } else {
            randomSlot(filtredSlots)
        }
    }

    fun outOfEnergy() = currentEnergy < energySpentPerMove

    fun grantEnergy(grantValue: Double) {
            if (currentEnergy < MAX_ENERGY)
            currentEnergy += grantValue
    }

    fun survived() {
        daysSurvived++
    }

    fun status() = "${kind[0]}${currentEnergy.toInt()}"

    open fun shouldReplicate(): Boolean {
        val chance = (chanceToReplicate * 2 * currentEnergy / MAX_ENERGY)
        return random.chanceTo(chance.toInt())
    }

    fun die() = world.killCreature(this)

    open fun replicateInto() = this.kind to 100
    open fun mutate() {}



    override fun hashCode(): Int = name.hashCode() * 31 + index + color.hashCode() + kind.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Creature

        if (index != other.index) return false
        if (name != other.name) return false
        if (currentEnergy != other.currentEnergy) return false
        if (kind != other.kind) return false

        return true
    }

}


