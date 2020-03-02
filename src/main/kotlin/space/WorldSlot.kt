package space

import entities.Creature
import kotlin.math.abs

data class WorldSlot(var x: Int, var y: Int, val worldSize: Int, var hasFood: Boolean = false, var creatureInSlot: Creature? = null) {
    fun slotValue(): Int {
        return if (hasFood) 1 else 0
    }

    fun hasCreature() = creatureInSlot != null

    fun isOnEdge() = x == 0 || y == 0 || x == worldSize - 1 || y == worldSize - 1

    fun distanceTo(slot: WorldSlot) = abs((this.x - slot.x) + (this.y - slot.y)).toDouble() / 2

    fun hasCreature(kind: String) = creatureInSlot != null && creatureInSlot!!.kind == kind

    fun isSpawnCandidate() = isOnEdge() && !hasCreature()

    fun creatureEnergy(): Double {
        if(creatureInSlot != null){
            return creatureInSlot!!.currentEnergy
        }
        return 0.0
    }
}