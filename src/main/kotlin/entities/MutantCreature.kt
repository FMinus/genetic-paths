package entities

import javafx.scene.paint.Color
import space.World
import space.WorldSlot
import tornadofx.turn
import kotlin.math.pow

data class MutantCreature(
    override val index: Int,
    override val name: String,
    override val world: World,
    override var currentSlot: WorldSlot,
    override var currentEnergy: Double = INITIAL_ENERGY_VALUE,
    /** attributes to mutate **/
    var speed: Double = 1.0,
    var size: Double = 1.0
) : Creature(index, name, world, currentSlot, currentEnergy) {

    override val chanceToReplicate: Int = 10
    override val kind: String = KIND
    override val color: Color
        get() = Color(Math.abs(size - 1), Math.abs(speed - 1), Math.abs(size + speed) / 4, 1.0)

    override val priority = speed

    override val energySpentPerMove: Double = 1 + (size.pow(3) + speed.pow(2)) / 3

    override fun selectSlotToMoveInto(): WorldSlot? {
        val possibleMoves = world.crossSight(currentSlot)
        return slotSelector(possibleMoves) {
            it.hasFood || (it.hasCreature() && isSmaller(it.creatureInSlot!!))
        }
    }

    override fun preMove(slotToMoveInto: WorldSlot) {
        if (slotToMoveInto.hasCreature() && isSmaller(slotToMoveInto.creatureInSlot!!) && random.decide()) {
            grantEnergy(slotToMoveInto.creatureEnergy() / 2)
            world.killCreature(slotToMoveInto.creatureInSlot!!)
        }
    }

    private fun isSmaller(other: Creature): Boolean {
        return other.kind == this.kind && (other as MutantCreature).size < this.size
    }

    override fun move(){
        var turns = 0
        val speedCeil = Math.ceil(speed)

        if(speedCeil == speed){
            /** speed was alread an int **/
            turns = speedCeil.toInt()
        } else {
            /** float speed -> chance to round it up **/
            turns = (if(random.decide()) speedCeil.toInt() else speed.toInt())
        }

        for(i in 0 until turns){
            super.move()
        }
    }


    override fun mutate() {
        val mutationDirection = random.decide()
        val genomToMutate = random.decide()
        if (genomToMutate) {
            /** mutate speed **/
            if (mutationDirection && speed < 2) {
                /** add **/
                speed += .5
            } else if (speed > 0.5) {
                /** substract **/
                speed -= .5
            }

        } else {
            /** mutate size **/
            if (mutationDirection && size < 2) {
                /** add **/
                size += .25
            } else if (size > 0.25) {
                /** substract **/
                size -= .25
            }
        }

    }

    companion object {
        val KIND = "Mutant"
    }

    override fun hashCode(): Int = super.hashCode()
    override fun equals(other: Any?) = super.equals(other)
}