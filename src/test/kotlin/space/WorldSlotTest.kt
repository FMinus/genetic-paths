package space

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import de.jodamob.kotlin.testrunner.KotlinTestRunner
import entities.Creature
import junit.framework.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(KotlinTestRunner::class)
class WorldSlotTest {

    lateinit var worldSlot: WorldSlot
    val creature: Creature = mock()
    val xCoord = 5
    val yCoord = 5
    val worldSize = 10
    val worldEnd = worldSize - 1
    val creatureKind = "Creature Kind"

    @Before
    fun init() {
        worldSlot = WorldSlot(xCoord, yCoord, worldSize, false, null)
    }

    @Test
    fun `Slot Value is food existence - 0 means no food`() {
        worldSlot.hasFood = false
        assertEquals(0, worldSlot.slotValue())
    }

    @Test
    fun `Slot Value is food existence - 1 means has food`() {
        worldSlot.hasFood = true
        assertEquals(1, worldSlot.slotValue())
    }

    @Test
    fun `Check for creature existence - no creature`() {
        worldSlot.creatureInSlot = null
        assertFalse(worldSlot.hasCreature())
    }

    @Test
    fun `Check for creature existence - has creature`() {
        worldSlot.creatureInSlot = creature
        assertTrue(worldSlot.hasCreature())
    }

    @Test
    fun `Slot is on edge if x = 0`() {
        worldSlot = WorldSlot(0, yCoord, worldSize)
        assertTrue(worldSlot.isOnEdge())

    }

    @Test
    fun `Slot is on edge if y = 0`() {
        worldSlot = WorldSlot(xCoord, 0, worldSize)
        assertTrue(worldSlot.isOnEdge())

    }

    @Test
    fun `Slot is on edge if x = worldSize - 1`() {
        worldSlot = WorldSlot(worldEnd, yCoord, worldSize)
        assertTrue(worldSlot.isOnEdge())

    }

    @Test
    fun `Slot is on edge if y = worldSize - 1`() {
        worldSlot = WorldSlot(xCoord, worldEnd, worldSize)
        assertTrue(worldSlot.isOnEdge())

    }

    @Test
    fun `Slot is not on edge if y and x at middle of world`() {
        worldSlot = WorldSlot(xCoord, yCoord, worldSize)
        assertFalse(worldSlot.isOnEdge())
    }

    @Test
    fun `calculates distance to other slot`() {
        val otherX = xCoord + 1
        val otherY = yCoord + 1
        val otherWorldSlot = WorldSlot(otherX, otherY, worldSize, false, null)
        val expected = abs((xCoord - otherX + yCoord - otherY).toDouble() / 2)
        val actual = worldSlot.distanceTo(otherWorldSlot)
        assertEquals(expected, actual)
    }

    @Test
    fun `Check if creature has a specific kind is false if there is no creature in slot`() {
        worldSlot.creatureInSlot = null
        assertFalse(worldSlot.hasCreature(creatureKind))
    }

    @Test
    fun `Check if creature has a specific kind is false if there is a creature of a different kind`() {
        val anotherKind = "Totally Other Kind"
        whenever(creature.kind).thenReturn(anotherKind)
        worldSlot.creatureInSlot = creature
        assertFalse(worldSlot.hasCreature(creatureKind))
    }

    @Test
    fun `Check if creature has a specific kind is true if there is a creature of the same kind`() {
        whenever(creature.kind).thenReturn(creatureKind)
        worldSlot.creatureInSlot = creature
        assertTrue(worldSlot.hasCreature(creatureKind))
    }

    @Test
    fun `Slot is not spawn candidate if not on edge`() {
        assertFalse(worldSlot.isSpawnCandidate())
    }

    @Test
    fun `Slot is not spawn candidate if already has creature`() {
        worldSlot = WorldSlot(xCoord, 0, worldSize)
        worldSlot.creatureInSlot = creature
        assertFalse(worldSlot.isSpawnCandidate())
    }

    @Test
    fun `Slot is spawn candidate if not on edge and has no creature`() {
        worldSlot = WorldSlot(xCoord, 0, worldSize)
        worldSlot.creatureInSlot = null
        assertTrue(worldSlot.isSpawnCandidate())
    }

    @Test
    fun `Gives out 0 as creature energy if there is no creature in slot`() {
        worldSlot.creatureInSlot = null
        assertEquals(0.0, worldSlot.creatureEnergy())
    }

    @Test
    fun `Gives out current creature's energy `() {
        val energyValue = 33.0
        worldSlot.creatureInSlot = creature
        whenever(creature.currentEnergy).thenReturn(energyValue)
        assertEquals(energyValue, worldSlot.creatureEnergy())
    }
}