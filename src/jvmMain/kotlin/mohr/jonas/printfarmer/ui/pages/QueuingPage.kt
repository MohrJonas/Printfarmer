package mohr.jonas.printfarmer.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import mohr.jonas.printfarmer.GCodeParser
import mohr.jonas.printfarmer.MoonrakerClient
import mohr.jonas.printfarmer.data.EnqueuingStrategy
import mohr.jonas.printfarmer.data.EnqueuingStrategy.Companion.name
import mohr.jonas.printfarmer.data.printer.PrinterCapabilities
import mohr.jonas.printfarmer.data.printer.PrinterCapabilities.Companion.fulfills
import mohr.jonas.printfarmer.data.printer.PrinterStats
import mohr.jonas.printfarmer.stores.FilamentStore
import mohr.jonas.printfarmer.stores.PrinterStore
import mohr.jonas.printfarmer.ui.Colors
import mohr.jonas.printfarmer.ui.components.Card
import java.nio.file.Files
import java.nio.file.Path
import javax.swing.JFileChooser
import javax.swing.filechooser.FileNameExtensionFilter
import kotlin.io.path.absolutePathString
import kotlin.io.path.name

class QueuingPage : Page {

    @Composable
    override fun display(data: Any?) {
        var selectedStrategy by remember { mutableStateOf(EnqueuingStrategy.SPECIFIC) }
        var selectedFilePath by remember { mutableStateOf<Path?>(null) }
        var selectedPrinterStats by remember { mutableStateOf<PrinterStats?>(null) }
        var ignoreRequirements by remember { mutableStateOf(false) }
        var onlyUpload by remember { mutableStateOf(false) }
        var requirements by remember { mutableStateOf<PrinterCapabilities?>(null) }
        var showWarningNoPrinter by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            PrinterStore.updateStore()
            FilamentStore.updateStore()
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Card {
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Button(colors = ButtonDefaults.buttonColors(backgroundColor = selectedFilePath.let { if (it == null) Colors.mediumGray else Colors.confirmColor }),
                            modifier = Modifier.padding(5.dp),
                            onClick = {
                                JFileChooser().let {
                                    val filter = FileNameExtensionFilter("GCode", "gcode")
                                    it.fileFilter = filter
                                    it.isAcceptAllFileFilterUsed = false
                                    if (it.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                                        selectedFilePath = it.selectedFile.toPath()
                                        selectedStrategy = EnqueuingStrategy.SPECIFIC
                                        selectedPrinterStats = null
                                        requirements = null
                                    }
                                }
                            }) { Text("Select", maxLines = 1, overflow = TextOverflow.Ellipsis, color = Colors.lightGray) }
                        selectedFilePath?.let {
                            Text(
                                it.absolutePathString(), maxLines = 1, overflow = TextOverflow.Ellipsis, color = Colors.mediumGray, modifier = Modifier.padding(bottom = 5.dp)
                            )
                        }
                    }
                }
            }
            selectedFilePath?.let {
                requirements = GCodeParser.getRequiredCapabilities(Files.readAllLines(it))
                requirements?.let {
                    item {
                        Card {
                            Column {
                                Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painterResource("nozzle.png"), null, modifier = Modifier.size(32.dp).padding(end = 5.dp))
                                    Text("${it.maxHotendTemp}°C", color = Colors.mediumGray)
                                }
                                Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painterResource("heat.png"), null, modifier = Modifier.size(32.dp).padding(end = 5.dp))
                                    Text("${it.maxBedTemp}°C", color = Colors.mediumGray)
                                }
                                Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painterResource("axis.png"), null, modifier = Modifier.size(32.dp).padding(end = 5.dp))
                                    Text("X: ${it.maxX}mm Y: ${it.maxY}mm Z: ${it.maxZ}mm", color = Colors.mediumGray)
                                }
                                Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(painterResource("cage.png"), null, modifier = Modifier.size(32.dp).padding(end = 5.dp))
                                    Text(if (it.chamber) "✓" else "╳", color = Colors.mediumGray)
                                }
                            }
                        }
                    }
                    item {
                        Card {
                            Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(ignoreRequirements, onCheckedChange = {
                                    ignoreRequirements = it
                                    selectedPrinterStats = null
                                })
                                Text("Ignore requirements", color = Colors.mediumGray)
                            }
                        }
                    }
                }
            }
            requirements?.let {
                if (showWarningNoPrinter) item {
                    Card {
                        Text("No printers matching requirements", color = Colors.errorColor, fontSize = 25.sp, modifier = Modifier.padding(5.dp))
                    }
                }
                item {
                    Card {
                        var dropdownExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.background(Colors.lightGray).padding(5.dp).clickable {
                            dropdownExpanded = true
                        }) {
                            Text(selectedStrategy.name(), color = Colors.mediumGray)
                            DropdownMenu(dropdownExpanded, onDismissRequest = {
                                dropdownExpanded = false
                            }) {
                                EnqueuingStrategy.values().forEach {
                                    DropdownMenuItem(onClick = {
                                        selectedStrategy = it
                                        dropdownExpanded = false
                                        showWarningNoPrinter = false
                                        when (selectedStrategy) {
                                            EnqueuingStrategy.RANDOM -> {
                                                if (ignoreRequirements) {
                                                    if (PrinterStore.printerStats.isEmpty()) showWarningNoPrinter = true
                                                    else selectedPrinterStats = PrinterStore.printerStats.random()
                                                } else {
                                                    if (PrinterStore.filterPrintersByCapability(requirements!!).isEmpty()) showWarningNoPrinter = true
                                                    else selectedPrinterStats = PrinterStore.filterPrintersByCapability(requirements!!).random()
                                                }
                                            }

                                            EnqueuingStrategy.SHORTEST_QUEUE -> {
                                                if (ignoreRequirements) {
                                                    if (PrinterStore.printerStats.isEmpty()) showWarningNoPrinter = true
                                                    else selectedPrinterStats = PrinterStore.printerStats.minByOrNull { it.queue.value?.queuedJobs?.size ?: Int.MAX_VALUE }
                                                } else {
                                                    if (PrinterStore.filterPrintersByCapability(requirements!!).isEmpty()) showWarningNoPrinter = true
                                                    else selectedPrinterStats = PrinterStore.filterPrintersByCapability(requirements!!)
                                                        .minByOrNull { it.queue.value?.queuedJobs?.size ?: Int.MAX_VALUE }
                                                }
                                            }
                                        }
                                    }) {
                                        Text(it.name())
                                    }
                                }
                            }
                        }
                    }
                }
                if (selectedStrategy == EnqueuingStrategy.SPECIFIC) item {
                    Card {
                        var dropdownExpanded by remember { mutableStateOf(false) }
                        Box(modifier = Modifier.background(Colors.lightGray).padding(5.dp).clickable {
                            dropdownExpanded = true
                        }) {
                            Text(selectedPrinterStats?.config?.name ?: "-----", color = Colors.mediumGray)
                            DropdownMenu(dropdownExpanded, onDismissRequest = {
                                dropdownExpanded = false
                            }) {
                                (if (ignoreRequirements) PrinterStore.printerStats else PrinterStore.filterPrintersByCapability(requirements!!)).forEach {
                                    DropdownMenuItem(onClick = {
                                        selectedPrinterStats = it
                                        dropdownExpanded = false
                                    }) {
                                        Text(it.config.name)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            selectedPrinterStats?.let {
                item {
                    Card {
                        println(selectedStrategy)
                        println(selectedPrinterStats)
                        Text("Selected printer: ${selectedPrinterStats!!.config.name}", color = Colors.mediumGray, modifier = Modifier.padding(5.dp))
                    }
                }
            }
            item {
                Card {
                    Row(modifier = Modifier.padding(5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(onlyUpload, onCheckedChange = {
                            onlyUpload = it
                        })
                        Text("Only upload", color = Colors.mediumGray)
                    }
                }
            }
            if (selectedPrinterStats != null && requirements != null && (ignoreRequirements || selectedPrinterStats!!.config.capabilities.fulfills(requirements!!)) && selectedFilePath != null) item {
                Card {
                    Button(colors = ButtonDefaults.buttonColors(backgroundColor = Colors.confirmColor), onClick = {
                        GlobalScope.launch {
                            MoonrakerClient.uploadFile(selectedPrinterStats!!.config.ip, selectedFilePath!!)
                            if (!onlyUpload) MoonrakerClient.queuePrint(selectedPrinterStats!!.config.ip, selectedFilePath!!.name)
                        }
                    }) {
                        Text("Done", maxLines = 1, overflow = TextOverflow.Ellipsis, color = Colors.textColor)
                    }
                }
            }
        }
    }
}