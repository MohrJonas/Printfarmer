package mohr.jonas.printfarmer.data.printer

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import mohr.jonas.printfarmer.data.filament.FilamentTemplate
import kotlin.random.Random

class PrinterStats(val config: PrinterConfig) {
    var status = mutableStateOf<String?>(null)
    var queue = mutableStateOf<PrinterQueue?>(null)
    var extruderTemps = mutableStateListOf<Float>()
    var bedTemps = mutableStateListOf<Float>()
    var loadedFilament = mutableStateOf<FilamentTemplate?>(null)
    var currentPrintProgress = mutableStateOf<Float?>(null)
    var printerColor = Color.random()
}

fun Color.Companion.random() = Color(Random.nextInt(150, 220), Random.nextInt(150, 220), Random.nextInt(150, 220))