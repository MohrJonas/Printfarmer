package mohr.jonas.printfarmer.stores

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import mohr.jonas.printfarmer.MoonrakerClient
import mohr.jonas.printfarmer.data.filament.FilamentClient
import mohr.jonas.printfarmer.data.printer.PrinterCapabilities
import mohr.jonas.printfarmer.data.printer.PrinterCapabilities.Companion.fulfills
import mohr.jonas.printfarmer.data.printer.PrinterConfig
import mohr.jonas.printfarmer.data.printer.PrinterStats

object PrinterStore {

    @get:Synchronized
    @set:Synchronized
    var configs = mutableStateListOf<PrinterConfig>()

    @get:Synchronized
    @set:Synchronized
    var printerStats = mutableStateListOf<PrinterStats>()

    @get:Synchronized
    @set:Synchronized
    var settings = mutableStateMapOf<String, Any>()

    suspend fun updateStore() {
        printerStats.forEach {
            it.queue.value = MoonrakerClient.getPrinterQueue(it.config.ip)
            it.status.value = MoonrakerClient.getPrinterStatus(it.config.ip)
            it.extruderTemps.add(MoonrakerClient.getExtruderTemp(it.config.ip))
            it.bedTemps.add(MoonrakerClient.getBedTemp(it.config.ip))
            it.currentPrintProgress.value = MoonrakerClient.getCurrentPrintProgress(it.config.ip)
            it.loadedFilament.value = FilamentClient.getFilament(it.config.ip)
        }
    }

    fun filterPrintersByCapability(requirement: PrinterCapabilities) = printerStats.filter { it.config.capabilities.fulfills(requirement) }

    fun writeToFile() {

    }
}