import entities.*
import space.World
import space.WorldSlot
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.random.Random


val DEFAULT_SPAWN_AMOUNT = 2

val creaturesKind = listOf(
    NormalCreature.KIND,
    FastCreature.KIND,
    MortalCreature.KIND,
    SightCreature.KIND,
    PredatorCreature.KIND,
    FleeingCreature.KIND,
    ShareCreature.KIND,
    MutantCreature.KIND,
    HistoricCreature.KIND,
    PriorityCreature.KIND
)

val DEFAULT_INITIAL_SPAWNS = creaturesKind.map { it to DEFAULT_SPAWN_AMOUNT }.toMap()

class Simulation(
    val worldSize: Int = 10,
    private val foodQuantity: Int = 20,
    private val intialSpawns: Map<String, Int> = DEFAULT_INITIAL_SPAWNS,
    private val random: RandomGenerator = RandomGenerator()
) {

    val world: World

    private val creatures
        get() = world.creatures

    val creaturesCount
        get() = world.countRemainingCreatures()

    var dayCount = 0

    val creaturesHistory = mutableListOf<Creature>()

    private var creatureCountHistory: MutableMap<String, MutableMap<Int, Int>> = mutableMapOf()

    init {
        world = World(worldSize, foodQuantity, random = random)
    }

    fun isOver() = world.deadWorld() || world.fullWorld()

    fun init() {
        dayCount = 0
        world.initSlots()
        putFood()
        spawnCreatures()
        creatureCountHistory = mutableMapOf()
    }

    private fun putFood(foodToPut: Int = foodQuantity) {
        var foodCount = foodToPut
        world.slotsList().filter { !it.hasCreature() && !it.hasFood }.forEach { slot ->
            val hasFood = !slot.isOnEdge() && world.shouldHaveFood(foodCount)
            if (hasFood) foodCount--
            slot.hasFood = hasFood
        }
    }

    private fun spawnCreatures() {
        intialSpawns.forEach { (kind, spawnAmount) ->
            spawnCreatures(kind, spawnAmount)
        }
    }

    private fun spawnCreatures(kind: String, spawnCount: Int) {
        for (index in 1..spawnCount) {
            spawnCreature(index, kind, null)
        }
    }

    fun spawnCreature(index: Int, kind: String, original: Creature?): Creature? {
        val randomSlot = world.randomEdgeSlot()
        if (randomSlot != null) {
            val creature = when (kind) {
                FastCreature.KIND -> FastCreature(creatures.size, "C$index", world, randomSlot)
                SightCreature.KIND -> SightCreature(creatures.size, "C$index", world, randomSlot)
                MortalCreature.KIND -> MortalCreature(creatures.size, "C$index", world, randomSlot)
                PriorityCreature.KIND -> PriorityCreature(creatures.size, "C$index", world, randomSlot)
                PredatorCreature.KIND -> PredatorCreature(creatures.size, "C$index", world, randomSlot)
                ShareCreature.KIND -> ShareCreature(creatures.size, "C$index", world, randomSlot)
                FleeingCreature.KIND -> FleeingCreature(creatures.size, "C$index", world, randomSlot)
                MutantCreature.KIND -> spawnMutant(creatures.size, randomSlot, original = original as MutantCreature?)
                HistoricCreature.KIND -> HistoricCreature(creatures.size, "C$index", world, randomSlot)
                else -> NormalCreature(creatures.size, "C$index", world, randomSlot)
            }
            recordCreature(creature, randomSlot)
            return creature
        }
        return null
    }

    fun spawnMutant(index: Int, worldSlot: WorldSlot, original: MutantCreature?): MutantCreature {
        val size = original?.size ?: 1.0
        val speed = original?.speed ?: 1.0
        return MutantCreature(index = creatures.size, name = "C$index", world = world, currentSlot = worldSlot, size = size, speed = speed)
    }

    fun recordCreature(creature: Creature, slot: WorldSlot) {
        world.spawn(creature, slot)
        creaturesHistory.add(creature)
    }


    fun moveCreature(creature: Creature) {
        if (!creature.outOfEnergy()) {
            creature.move()
            creature.survived()
        } else {
            world.killCreature(creature)
        }
    }

    fun replicate(creature: Creature){
        if (creature.shouldReplicate()) {
            val replicateInto = creature.replicateInto()
            if (random.chanceTo(replicateInto.second)) {
                val spawnCreature = spawnCreature(creatures.size, replicateInto.first, creature)
                spawnCreature?.mutate()
            } else {
                spawnCreature(creatures.size, creature.kind, creature)
            }
        }
    }



    fun singleRun() {

        val iterator = creatures.iterator()
        while(iterator.hasNext()){
            val creature = iterator.next()
            moveCreature(creature)
            replicate(creature)
        }

        if (dayCount % 10 == 0) {
            putFood(creatures.size * 2 + 5)
        }

        recordHistory()

        dayCount++
    }

    private fun recordHistory() {
        creatures.groupBy { it.kind }.forEach {
            setHistoryData(it.key, dayCount, it.value.size)
        }
        setHistoryData("All", dayCount, simulation.creaturesCount)
    }


    fun calculateLifeTimeByKind(): MutableMap<String, MutableMap<Int, Int>> {
        return creatureCountHistory
    }

    fun setHistoryData(kind: String, day: Int, value: Int) {
        when {creatureCountHistory[kind] == null -> creatureCountHistory[kind] = mutableMapOf() }
        creatureCountHistory[kind]!![day] = value
    }

    fun countCreatureByKind(): Map<String, Int> {
        return creatures.groupBy { it.kind }.map { it.key to it.value.size }.toMap()
    }

    fun mutants(): List<MutantCreature> {
        return creatures.filter { it.kind == MutantCreature.KIND }.map { it as MutantCreature }
    }

}
