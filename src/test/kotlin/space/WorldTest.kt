package space

import RandomGenerator
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import de.jodamob.kotlin.testrunner.KotlinTestRunner
import entities.NormalCreature
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(KotlinTestRunner::class)
class WorldTest {

    val worldSize = 10
    val worldEnd = worldSize - 1
    val worldCenter = worldSize / 2

    lateinit var creature: NormalCreature
    lateinit var world: World
    lateinit var startingWorldSlot: WorldSlot

    val random: RandomGenerator = mock()

    @Before
    fun setup() {
        startingWorldSlot = WorldSlot(worldCenter, worldCenter, worldSize, false, null)
        world = World(worldSize = worldSize, foodQuantity = 50, random = random)
        world.initSlots()
        creature = NormalCreature(0, "Testing Creature", world, startingWorldSlot)
        startingWorldSlot.creatureInSlot = creature
    }

    @Test
    fun `KingSight grants vision of all surrounding slots to a creature`(){

        val kingSight = world.kingSight(creature.currentSlot)
            .map { it.x to it.y }
        val expectedSlots = setOf(
            worldCenter to worldCenter + 1,
            worldCenter to worldCenter - 1,
            worldCenter - 1 to worldCenter,
            worldCenter + 1 to worldCenter,
            worldCenter - 1 to worldCenter - 1,
            worldCenter + 1 to worldCenter + 1,
            worldCenter - 1 to worldCenter + 1,
            worldCenter + 1 to worldCenter - 1
        )
        assertEquals(expectedSlots.size, kingSight.size)
        assertEquals(expectedSlots, kingSight.toSet())
    }

    @Test
    fun `KingSight does not includes slots out of bounds`(){

        val corner = world.getSlot(worldEnd, worldEnd)
        creature.currentSlot = corner

        val kingSight = world.kingSight(creature.currentSlot)
            .map { it.x to it.y }

        val expectedSlots = setOf(
            worldEnd - 1 to worldEnd,
            worldEnd to worldEnd - 1,
            worldEnd - 1 to worldEnd - 1
        )

        assertEquals(3, kingSight.size)
        assertEquals(expectedSlots, kingSight.toSet())
    }


    @Test
    fun `Cross Sight Works in World Corner`(){

        val corner = world.getSlot(0, 0)
        creature.currentSlot = corner

        val crossSight = world.crossSight(creature.currentSlot)
            .map { it.x to it.y }

        val expectedSlots = setOf(
            1 to 0,
            0 to 1
        )

        assertEquals(2, crossSight.size)
        assertEquals(expectedSlots, crossSight.toSet())
    }

    @Test
    fun `Killing creature removes it from list`(){
        val corner = world.getSlot(0, 0)
        world.spawn(creature, corner)
        world.killCreature(creature)
        assertFalse(world.creatures.contains(creature))
    }

    @Test
    fun `Empty creatures list means all creatures are dead - dead world`(){
        world.creatures.clear()
        assertTrue(world.deadWorld())
    }

    @Test
    fun `Should Count Food In the world empty`(){
        resetFoodSlots()
        assertEquals(0, world.countRemainingFood())
    }

    @Test
    fun `Should Count Food In the world - has one food`(){
        resetFoodSlots()
        val corner = world.getSlot(0, 0)
        corner.hasFood = true
        assertEquals(1, world.countRemainingFood())
    }

    @Test
    fun `Should Retrieve all slots in the world`(){
        resetFoodSlots()
        val allSlots: List<WorldSlot> = world.slotsList()
        assertFalse(allSlots.isEmpty())
        assertEquals(worldSize * worldSize ,allSlots.size)
    }

    @Test
    fun `Should Retrieve all food slots in the world - no food in the world`(){
        resetFoodSlots()
        val allSlots: List<WorldSlot> = world.slotsList()
        assertTrue(allSlots.foodSlots().isEmpty())
    }

    @Test
    fun `Should Retrieve all food slots in the world`(){
        resetFoodSlots()
        val allSlots: List<WorldSlot> = world.slotsList()
        val onlyFood = allSlots.get(0)
        onlyFood.hasFood = true

        val foodSlots = allSlots.foodSlots()
        assertEquals(listOf(onlyFood) ,foodSlots)
    }

    @Test
    fun `Count remaining creatures returns creatures list size`(){
       assertEquals(world.creatures.size, world.countRemainingCreatures())
    }

    @Test
    fun `Disallow putting down food when requested quantity is zero`(){
        val shouldHaveFood = world.shouldHaveFood(0)
        assertFalse(shouldHaveFood)
    }

    @Test
    fun `Disallow putting down food when requested quantity below zero`(){
        val shouldHaveFood = world.shouldHaveFood(-1)
        assertFalse(shouldHaveFood)
    }

    @Test
    fun `Chance to put down food when quantity requested not yet reached - true case`(){
        whenever(random.chanceTo(any())).thenReturn(true)
        val shouldHaveFood = world.shouldHaveFood(10)
        assertTrue(shouldHaveFood)
    }

    @Test
    fun `Chance to put down food when quantity requested not yet reached - false case`(){
        whenever(random.chanceTo(any())).thenReturn(false)
        val shouldHaveFood = world.shouldHaveFood(10)
        assertFalse(shouldHaveFood)
    }

    @Test
    fun `Edge slots have an X index of 0 and Y at worldEnd or vice versa`(){
       val edges = world.edgeSlots()
       assertTrue(edges.all { (it.x == 0 || it.y == 0 || it.x == worldEnd || it.y == worldEnd)})
    }

    @Test
    fun `An index is edge if index == 0`(){
        assertTrue(world.isEdgeIndex(0))
    }

    @Test
    fun `An index is edge if index == worldEnd`(){
        assertTrue(world.isEdgeIndex(worldEnd))
    }

    @Test
    fun `An index is not edge if index != worldEnd && index != 0`(){
        assertFalse(world.isEdgeIndex(worldCenter))
    }

    @Test
    fun `Spawning creature attachs it to slot`(){
        val corner = world.getSlot(0, 0)
        world.spawn(creature, corner)

        assertTrue(world.creatures.isNotEmpty())
        assertTrue(world.creatures.contains(creature))
        assertEquals(creature.currentSlot, corner)
        assertEquals(corner.creatureInSlot, creature)
    }

    @Test
    fun `World is Full if creatures are on more than 95% of slots`(){
        val smallWorld = World(worldSize = 2, random = random)
        smallWorld.initSlots()
        smallWorld.spawn(creature, smallWorld.getSlot(0, 0))
        smallWorld.spawn(creature, smallWorld.getSlot(1, 0))
        smallWorld.spawn(creature, smallWorld.getSlot(0, 1))
        assertTrue(smallWorld.fullWorld())
    }

    @Test
    fun `Gives slots with creatures in them - no creatures`(){
        val smallWorld = World(worldSize = 2, random = random)
        smallWorld.initSlots()
        val allSlots = smallWorld.slotsList()
        smallWorld.spawn(creature, smallWorld.getSlot(0, 0))
        smallWorld.spawn(creature, smallWorld.getSlot(1, 0))
        smallWorld.spawn(creature, smallWorld.getSlot(0, 1))
        assertEquals(listOf(smallWorld.getSlot(1, 1)), allSlots.noCreatureSlots())
    }

    fun resetFoodSlots() {
        iterateSlots {it.hasFood = false}
    }

    @Test
    fun `random Edge slot yields a random edge slot`(){
        val edges = world.edgeSlots()
        val corner = edges[0]
        whenever(random.of(edges)).thenReturn(corner)
        assertEquals(corner, world.randomEdgeSlot())
    }


    fun iterateSlots(action: (WorldSlot) -> Unit){
        world.slots.forEach { (_, slotsLine) ->
            slotsLine.forEach {
                action(it)
            }
        }
    }
}