import javafx.scene.paint.Color
import tornadofx.*

class WorldSlotView(
    var hasFood: Boolean,
    var hasCreature: Boolean,
    var labelText: String = "",
    val fillColor: Color = Color.BLACK
) : View() {
    private val SIZE = 55.0
    private val BACKGROUND_COLOR = "#f7f5f5"
    private val BORDER_COLOR = "#a1a1a1"
    override val root = hbox {
        if (hasFood) {
            button {
                minWidth = SIZE
                minHeight = SIZE
                circle {
                    centerX = 20.0
                    centerY = 20.0
                    radius = 10.0
                    fill = Color.GREEN
                }
                style {
                    backgroundColor += c(BACKGROUND_COLOR)
                    borderColor += box(c(BORDER_COLOR))
                }
            }
        } else if (hasCreature) {
            stackpane {
                minWidth = SIZE
                minHeight = SIZE
                circle {
                    centerX = 20.0
                    centerY = 20.0
                    radius = 20.0
                    fill = fillColor
                }
                label(labelText) {
                    textFill = Color.WHITE
                }
                style {
                    backgroundColor += c(BACKGROUND_COLOR)
                    borderColor += box(c(BORDER_COLOR))
                }
            }
        } else {
            label {
                textFill = fillColor
                minWidth = SIZE
                minHeight = SIZE
                style {
                    borderColor += box(c("#a1a1a1"))
                    backgroundColor += c(BACKGROUND_COLOR)
                }
            }
        }

    }


}