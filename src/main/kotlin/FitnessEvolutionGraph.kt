import entities.MutantCreature
import javafx.geometry.Insets
import javafx.scene.chart.BarChart
import javafx.scene.chart.CategoryAxis
import javafx.scene.chart.LineChart
import javafx.scene.chart.NumberAxis
import javafx.scene.control.Label
import javafx.scene.control.TextField
import javafx.scene.layout.HBox
import javafx.scene.layout.VBox
import javafx.scene.paint.Color
import javafx.scene.text.Font
import javafx.scene.text.TextAlignment
import space.WorldSlot
import tornadofx.*
import java.math.RoundingMode
import java.text.DecimalFormat
import java.util.*

lateinit var statusLabel: Label
var slotsGraphics: MutableMap<Int, MutableList<WorldSlotView>> = mutableMapOf()
var timer = Timer()


var creaturesSpawnCounts: MutableMap<String, TextField> = mutableMapOf()

lateinit var graphBoxView: VBox
lateinit var gridContainerView: HBox
lateinit var grid: HBox
lateinit var mutantsDataContainer: HBox

fun slotLabel(slot: WorldSlot) =
    if (slot.creatureInSlot != null) slot.creatureInSlot!!.status() else ""

fun slotColor(slot: WorldSlot): Color {
    return if (slot.hasCreature())
        slot.creatureInSlot!!.color
    else
        Color.BLACK
}


class FitnessEvolutionGraph : View() {
    override val root =
        hbox {
            vbox {
                label("Initial Creature Spawn") {
                    padding = Insets(5.0, 15.0, 15.0, 45.0)
                    minWidth = 70.0
                    textFill = Color.RED
                    font = Font(15.0)
                }
                creaturesKind.map { kind ->
                    hbox {
                        label("$kind:") {
                            padding = Insets(5.0, 5.0, 0.0, 5.0)
                            minWidth = 70.0
                        }
                        val textField = textfield {
                            text = "$DEFAULT_SPAWN_AMOUNT"
                        }
                        button("X") {
                            textFill = Color.RED
                            action {
                                textField.text = ""
                            }
                        }
                        creaturesSpawnCounts[kind] = textField
                    }
                }
                hbox {
                    padding = Insets(5.0, 5.0, 0.0, 50.0)
                    label("Clear All") {
                        padding = Insets(5.0, 5.0, 0.0, 5.0)
                        minWidth = 70.0
                    }
                    button("X") {
                        textFill = Color.RED
                        action {
                            creaturesSpawnCounts.forEach {
                                it.value.text = ""
                            }
                        }
                    }

                }
                mutantsDataContainer = hbox {
                    this.add(mutantTable())
                }
            }
            vbox {
                gridContainerView = hbox {
                    this.add(createGrid())
                }
                hbox {
                    statusLabel =
                        label {
                            minWidth = 400.0
                            padding = Insets(0.0, 50.0, 0.0, 50.0)
                            textAlignment = TextAlignment.CENTER
                        }
                    updateStatus()
                }
                hbox {
                    padding = Insets(10.0, 10.0, 10.0, 10.0)
                    button("Next Turn") {
                        action {
                            runSimulationStep(true)
                        }
                    }
                    button("Start Simulation") {
                        action {
                            startSimulation()
                        }
                    }
                    button("Stop Simulation") {
                        action {
                            timer.cancel()
                        }
                    }
                    button("Reset Simulation") {
                        action {
                            resetSimulation()
                        }
                    }
                }
            }
            graphBoxView = vbox {
                this.add(graphByKind())
                this.add(graphTotalCreatures())
            }
        }


    fun mutantTable(): VBox {
        val mutants = simulation.mutants()
        if (mutants.isEmpty()) {
            return vbox { }
        }
        val averageSize = mutants.map { it.size }.average().round()
        val averageSpeed = mutants.map { it.speed }.average().round()
        return vbox {
            maxWidth = 290.0
            label("average speed $averageSpeed, average size $averageSize")
            tableview(mutants.observable()) {
                readonlyColumn("index", MutantCreature::index)
                readonlyColumn("size", MutantCreature::size)
                readonlyColumn("speed", MutantCreature::speed)
                readonlyColumn("survived", MutantCreature::daysSurvived)
                readonlyColumn("energy", MutantCreature::currentEnergy)
            }
        }
    }

    fun updateMutantTable() {
        mutantsDataContainer.clear()
        mutantsDataContainer.add(mutantTable())
    }

    fun updateGrid() {
        gridContainerView.clear()
        gridContainerView.add(createGrid())
    }

    fun createGrid(): HBox = hbox {
        for (xIndex in 0 until worldSize) {
            slotsGraphics[xIndex] = mutableListOf()
            vbox {
                for (yIndex in 0 until worldSize) {
                    val slot = simulation.world.getSlot(xIndex, yIndex)
                    val worldSlotView =
                        WorldSlotView(slot.hasFood, slot.hasCreature(), slotLabel(slot), slotColor(slot))
                    this.add(worldSlotView)
                    slotsGraphics[xIndex]!!.add(yIndex, worldSlotView)
                }
            }
        }
    }

    fun parseSpawnAmounts(): Map<String, Int> {
        return creaturesSpawnCounts.map { (kind, textField) -> kind to textField.parseInt() }.toMap()
    }

    fun startSimulation() {
        timer = Timer()
        timer.schedule(object : TimerTask() {
            override fun run() {
                runSimulationStep()
            }
        }, 0, 200)
    }


    fun updateStatus() {
        statusLabel.text =
            "remaining creatures = ${simulation.creaturesCount}, remaining food = ${simulation.world.countRemainingFood()} - day = ${simulation.dayCount}"
    }

    fun resetSimulation() {
        simulation = Simulation(
            worldSize,
            foodQuantity,
            parseSpawnAmounts()
        )
        simulation.init()
        updateGrid()
        updateGraph()
        updateStatus()
    }

    fun runSimulationStep(instantUpdate: Boolean = false) {
        if (simulation.isOver()) {
            timer.cancel()
        }
        runAsync {
            simulation.singleRun()

        } ui {
            updateGrid()
            updateGraph(instantUpdate)
            updateStatus()
            updateMutantTable()

        }
    }

    fun graphByKind(): BarChart<String, Number> {
        return barchart("Creatures Count By Kind", CategoryAxis(), NumberAxis()) {
            series("Count") {
                simulation.countCreatureByKind().map { (kind, value) ->
                    data(kind, value)
                }
            }
        }
    }

    fun graphTotalCreatures(): LineChart<String, Number> {
        return linechart("Creature Count Evolution", CategoryAxis(), NumberAxis()) {
            simulation.calculateLifeTimeByKind().map { (kind, results) ->
                series(kind) {
                    results.map { (day, value) ->
                        data("$day", value)
                    }

                }
            }
        }
    }


    fun updateGraph(instantUpdate: Boolean = false) {
        if (simulation.dayCount % 5 == 0 || instantUpdate) {
            graphBoxView.clear()
            graphBoxView.add(graphByKind())
            graphBoxView.add(graphTotalCreatures())

        }
    }
}

fun Double.round(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.CEILING
    return df.format(this).toDouble()

}

fun TextField.parseInt(): Int {
    return try {
        this.text.toInt()
    } catch (e: NumberFormatException) {
        0
    }
}