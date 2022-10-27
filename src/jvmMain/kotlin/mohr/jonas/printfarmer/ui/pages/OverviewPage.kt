package mohr.jonas.printfarmer.ui.pages

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import mohr.jonas.printfarmer.stores.FilamentStore
import mohr.jonas.printfarmer.stores.PrinterStore
import mohr.jonas.printfarmer.ui.Colors
import mohr.jonas.printfarmer.ui.components.Card
import mohr.jonas.printfarmer.ui.components.LineChart
import mohr.jonas.printfarmer.ui.components.LineData

class OverviewPage : Page {

    @Composable
    override fun display(data: Any?) {
        LaunchedEffect(Unit) {
            PrinterStore.updateStore()
            FilamentStore.updateStore()
        }
        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Card {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                        PrinterStore.printerStats.forEach {
                            if (it.currentPrintProgress.value != null) Row(
                                modifier = Modifier.fillMaxWidth().padding(5.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(it.config.name, color = Colors.mediumGray, modifier = Modifier.fillMaxWidth().weight(1f))
                                LinearProgressIndicator(
                                    it.currentPrintProgress.value!!, color = it.printerColor, modifier = Modifier.fillMaxWidth().weight(1f, true)
                                )
                            }
                        }
                    }
                }
            }
            item {
                PrinterStore.printerStats.filter { it.extruderTemps.isNotEmpty() }.let {
                    if (it.isEmpty()) return@let
                    Card(modifier = Modifier.height(200.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text("Extruder", color = Colors.mediumGray, modifier = Modifier.padding(5.dp))
                            LineChart(*it.map { LineData(it.printerColor, it.extruderTemps.withIndex().map { Pair(it.index.toFloat(), it.value) }) }
                                .chunked(1200)[0].toTypedArray())
                        }
                    }
                }
            }
            item {
                PrinterStore.printerStats.filter { it.bedTemps.isNotEmpty() }.let {
                    if (it.isEmpty()) return@let
                    Card(modifier = Modifier.height(200.dp)) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                            Text("Bed", color = Colors.mediumGray, modifier = Modifier.padding(5.dp))
                            LineChart(*it.map { LineData(it.printerColor, it.bedTemps.withIndex().map { Pair(it.index.toFloat(), it.value) }) }
                                .chunked(1200)[0].toTypedArray())
                        }
                    }
                }
            }
        }
    }
}