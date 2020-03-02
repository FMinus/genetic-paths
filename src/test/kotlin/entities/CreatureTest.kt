package entities

import RandomGenerator
import com.nhaarman.mockito_kotlin.mock
import de.jodamob.kotlin.testrunner.KotlinTestRunner
import junit.framework.Assert.assertEquals
import junit.framework.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import space.World
import space.WorldSlot

@RunWith(KotlinTestRunner::class)
class CreatureTest {

    lateinit var start: WorldSlot
    val random: RandomGenerator = mock()
    val world: World = mock()

    lateinit var creature: NormalCreature

    val worldSize = 10
    val worldEnd = worldSize - 1
    val initialEnergy = 10.0
    val worldCenter = worldSize / 2

    val creatureName = "Testing Creature"


    @Before
    fun setup() {
        start = WorldSlot(0, 0, worldSize)
        creature = NormalCreature(0, creatureName, world, start, initialEnergy)
        start.creatureInSlot = creature
    }

    @Test
    fun `Test instantiation works`() {
        assertEquals(creatureName, creature.name)
    }

    @Test
    fun `working hashcode`() {
        val hashCode = creature.hashCode()
        assertNotNull(hashCode)
    }

    @Test
    fun `Grant energy adds to current creature's energy`() {
        val grantValue = 5.0
        creature.grantEnergy(grantValue)
        assertEquals(initialEnergy + grantValue, creature.currentEnergy)
    }


}