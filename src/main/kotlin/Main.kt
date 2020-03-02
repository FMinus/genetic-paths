import tornadofx.App
import tornadofx.launch

val worldSize = 14
var foodQuantity = 50

lateinit var simulation: Simulation

fun main(args: Array<String>) {
    simulation = Simulation(worldSize = worldSize, foodQuantity = foodQuantity)
    simulation.init()
    launch<GeneSimulationApp>(args)

}

class GeneSimulationApp : App(FitnessEvolutionGraph::class)

