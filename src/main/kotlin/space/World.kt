package space

import RandomGenerator
import entities.*
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.math.floor


open class World(
    val worldSize: Int = 10,
    private val foodQuantity: Int = 20,
    private val random: RandomGenerator = RandomGenerator()
) {

    var slots: MutableMap<Int, MutableList<WorldSlot>> = mutableMapOf()
    val creatures = CopyOnWriteArrayList<Creature>()
    val creaturesHistory = mutableListOf<Creature>()
    val worldEnd = worldSize - 1

    fun slotsList(): List<WorldSlot> = slots.flatMap { (_, line) -> line }

    fun shouldHaveFood(remainingFoodCount: Int): Boolean {
        if (remainingFoodCount <= 0)
            return false
        return random.chanceTo(35)
    }

    fun edgeSlots() = slotsList().filter { it.isSpawnCandidate()}

    fun randomEdgeSlot(): WorldSlot? = random.of(edgeSlots().noCreatureSlots())

    fun getSlot(xIndex: Int, yIndex: Int) = slots[xIndex]!![yIndex]

    fun isEdgeIndex(index: Int) = index == 0 || index == worldEnd

    fun killCreature(creature: Creature){
        creature.currentSlot.creatureInSlot = null
        creatures.remove(creature)
    }

    fun spawn(creature: Creature, slot: WorldSlot){
        creature.currentSlot = slot
        slot.creatureInSlot = creature
        creatures.add(creature)
    }

    fun initSlots() {
        for (xIndex in 0 until worldSize) {
            slots[xIndex] = (0 until worldSize).mapIndexed { yIndex, _ ->
                WorldSlot(xIndex, yIndex, worldSize)
            }.toMutableList()
        }
    }

    fun countRemainingFood() = slots.map { it.value.map { slot -> slot.slotValue() }.sum() }.sum()

    fun countRemainingCreatures() = creatures.size

    fun deadWorld() = creatures.isEmpty()
    fun fullWorld() = creatures.size >= floor(worldSize * worldSize * .95)


    fun crossSight(slot: WorldSlot): List<WorldSlot> {
        return listOf(
            slot.x - 1 to slot.y,
            slot.x + 1 to slot.y,
            slot.x to slot.y - 1,
            slot.x  to slot.y + 1
        ).filter { (x, y) -> validSlotCoordinates(x, y) }.
            map { (x, y) -> getSlot(x, y) }
    }

    fun kingSight(range: Int, worldSlot: WorldSlot): List<WorldSlot> {
        val currentX = worldSlot.x
        val currentY = worldSlot.y

        val kingSides = (0..range).map { xRange ->
            (0..range).filter { !(it == 0 && xRange == 0) }.map { yRange ->
                listOf(
                    currentX - xRange to currentY - yRange,
                    currentX + xRange to currentY + yRange,
                    currentX - xRange to currentY + yRange,
                    currentX + xRange to currentY - yRange
                )
            }.flatMap { it }
        }.flatMap { it }.toSet()

        return kingSides.filter { validSlotCoordinates(it.first, it.second) }.map { getSlot(it.first, it.second) }
            .toList()
    }

    fun kingSight(worldSlot: WorldSlot): List<WorldSlot> = kingSight(1, worldSlot)

    fun validSlotCoordinates(x: Int, y: Int) = x >= 0 && x <= worldEnd && y >= 0 && y <= worldEnd

}

fun List<WorldSlot>.foodSlots() = this.filter { it.hasFood }

fun List<WorldSlot>.noCreatureSlots() = this.filter { !it.hasCreature() }