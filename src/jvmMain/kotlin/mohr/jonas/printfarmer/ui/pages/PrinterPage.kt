package mohr.jonas.printfarmer.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mohr.jonas.printfarmer.stores.FilamentStore
import mohr.jonas.printfarmer.data.filament.toColor
import mohr.jonas.printfarmer.data.printer.PrinterCapabilities
import mohr.jonas.printfarmer.data.printer.PrinterConfig
import mohr.jonas.printfarmer.data.printer.PrinterStats
import mohr.jonas.printfarmer.stores.PrinterStore
import mohr.jonas.printfarmer.ui.Colors
import mohr.jonas.printfarmer.ui.SubNavbarItems
import mohr.jonas.printfarmer.ui.components.Card
import mohr.jonas.printfarmer.ui.components.LineChart
import mohr.jonas.printfarmer.ui.components.LineData
import org.apache.commons.validator.routines.InetAddressValidator

class PrinterPage : Page {

    @Composable
    override fun display(data: Any?) {
        var selectedSubNavbarItem by remember { mutableStateOf(SubNavbarItems.OVERVIEW) }
        var selectedPrinter by remember { mutableStateOf<PrinterStats?>(null) }
        if (data is PrinterConfig) {
            selectedSubNavbarItem = SubNavbarItems.OVERVIEW
            selectedPrinter = PrinterStore.printerStats.find { it.config == data }
        }
        LaunchedEffect(Unit) {
            PrinterStore.updateStore()
            FilamentStore.updateStore()
        }
        NavigationRail(modifier = Modifier.fillMaxHeight().width(150.dp), backgroundColor = Colors.lightGray) {
            PrinterStore.printerStats.forEach {
                NavigationRailItem(
                    icon = {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(painterResource("3d-printing.png"), null, modifier = Modifier.size(24.dp).padding(end = 5.dp))
                            Text(it.config.name, color = Colors.mediumGray)
                        }
                    },
                    onClick = {
                        selectedSubNavbarItem = SubNavbarItems.OVERVIEW
                        selectedPrinter = it
                    },
                    selected = selectedSubNavbarItem == SubNavbarItems.OVERVIEW && selectedPrinter == it,
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Colors.mediumGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            NavigationRailItem(
                icon = {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Icon(painterResource("add_FILL0_wght400_GRAD0_opsz48.png"), null, modifier = Modifier.size(24.dp).padding(end = 5.dp))
                        Text("Add Printer", color = Colors.mediumGray)
                    }
                },
                onClick = {
                    selectedSubNavbarItem = SubNavbarItems.ADD
                },
                selected = selectedSubNavbarItem == SubNavbarItems.ADD,
                selectedContentColor = Color.Black,
                unselectedContentColor = Colors.mediumGray,
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (selectedSubNavbarItem == SubNavbarItems.ADD) NewPrinterPage(onPrinterAdd = {
            selectedPrinter = it
            selectedSubNavbarItem = SubNavbarItems.OVERVIEW
        }, onCancel = {
            selectedPrinter = null
            selectedSubNavbarItem = SubNavbarItems.OVERVIEW
        })
        else if (selectedSubNavbarItem == SubNavbarItems.OVERVIEW && selectedPrinter != null) PrinterOverviewPage(selectedPrinter!!)
    }

    @Composable
    fun NewPrinterPage(onPrinterAdd: (PrinterStats) -> Unit, onCancel: () -> Unit) {
        var nameState by remember { mutableStateOf(TextFieldValue()) }
        var ipState by remember { mutableStateOf(TextFieldValue()) }
        var xState by remember { mutableStateOf(TextFieldValue()) }
        var yState by remember { mutableStateOf(TextFieldValue()) }
        var zState by remember { mutableStateOf(TextFieldValue()) }
        var hotendState by remember { mutableStateOf(TextFieldValue()) }
        var bedState by remember { mutableStateOf(TextFieldValue()) }
        var chamberState by remember { mutableStateOf(false) }
        var nameErrorState by remember { mutableStateOf(false) }
        var ipErrorState by remember { mutableStateOf(false) }
        var hotendErrorState by remember { mutableStateOf(false) }
        var bedErrorState by remember { mutableStateOf(false) }
        var xErrorState by remember { mutableStateOf(false) }
        var yErrorState by remember { mutableStateOf(false) }
        var zErrorState by remember { mutableStateOf(false) }
        Row(modifier = Modifier.fillMaxSize(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Card {
                Column(modifier = Modifier.fillMaxHeight().fillMaxWidth(0.75f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    TextField(nameState, modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp), isError = nameErrorState, colors = TextFieldDefaults.textFieldColors(
                        textColor = Colors.lightGray,
                        backgroundColor = Colors.mediumGray,
                        errorCursorColor = Colors.errorColor,
                        focusedIndicatorColor = Colors.lightGray,
                        cursorColor = Colors.lightGray
                    ), onValueChange = {
                        nameErrorState = false
                        nameState = it
                    }, singleLine = true, label = { Text("Name", color = Colors.textColor) })
                    TextField(ipState, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), isError = ipErrorState, colors = TextFieldDefaults.textFieldColors(
                        textColor = Colors.lightGray,
                        backgroundColor = Colors.mediumGray,
                        errorCursorColor = Colors.errorColor,
                        focusedIndicatorColor = Colors.lightGray,
                        cursorColor = Colors.lightGray
                    ), onValueChange = {
                        ipErrorState = false
                        ipState = it
                    }, singleLine = true, label = { Text("IP-Address", color = Colors.textColor) })
                    TextField(hotendState, modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp), isError = hotendErrorState, colors = TextFieldDefaults.textFieldColors(
                        textColor = Colors.lightGray,
                        backgroundColor = Colors.mediumGray,
                        errorCursorColor = Colors.errorColor,
                        focusedIndicatorColor = Colors.lightGray,
                        cursorColor = Colors.lightGray
                    ), onValueChange = {
                        hotendErrorState = false
                        hotendState = it
                    }, singleLine = true, label = { Text("Max. hotend temp", color = Colors.textColor) })
                    TextField(bedState, modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp), isError = bedErrorState, colors = TextFieldDefaults.textFieldColors(
                        textColor = Colors.lightGray,
                        backgroundColor = Colors.mediumGray,
                        errorCursorColor = Colors.errorColor,
                        focusedIndicatorColor = Colors.lightGray,
                        cursorColor = Colors.lightGray
                    ), onValueChange = {
                        bedErrorState = false
                        bedState = it
                    }, singleLine = true, label = { Text("Max. bed temp", color = Colors.textColor) })
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 5.dp), horizontalArrangement = Arrangement.Center) {
                        TextField(xState, modifier = Modifier.fillMaxWidth().weight(1f), isError = xErrorState, colors = TextFieldDefaults.textFieldColors(
                            textColor = Colors.lightGray,
                            backgroundColor = Colors.mediumGray,
                            errorCursorColor = Colors.errorColor,
                            focusedIndicatorColor = Colors.lightGray,
                            cursorColor = Colors.lightGray
                        ), onValueChange = {
                            xErrorState = false
                            xState = it
                        }, singleLine = true, label = { Text("Max. X", color = Colors.textColor) })
                        TextField(yState,
                            modifier = Modifier.fillMaxWidth().weight(1f).padding(start = 5.dp, end = 5.dp),
                            isError = yErrorState,
                            colors = TextFieldDefaults.textFieldColors(
                                textColor = Colors.lightGray,
                                backgroundColor = Colors.mediumGray,
                                errorCursorColor = Colors.errorColor,
                                focusedIndicatorColor = Colors.lightGray,
                                cursorColor = Colors.lightGray
                            ),
                            onValueChange = {
                                yErrorState = false
                                yState = it
                            },
                            singleLine = true,
                            label = { Text("Max. Y", color = Colors.textColor) })
                        TextField(zState, modifier = Modifier.fillMaxWidth().weight(1f), isError = zErrorState, colors = TextFieldDefaults.textFieldColors(
                            textColor = Colors.lightGray,
                            backgroundColor = Colors.mediumGray,
                            errorCursorColor = Colors.errorColor,
                            focusedIndicatorColor = Colors.lightGray,
                            cursorColor = Colors.lightGray
                        ), onValueChange = {
                            zErrorState = false
                            zState = it
                        }, singleLine = true, label = { Text("Max. Z", color = Colors.textColor) })
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(chamberState, onCheckedChange = {
                            chamberState = it
                        })
                        Text("Enclosed chamber", color = Colors.mediumGray, fontSize = 15.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Button(colors = ButtonDefaults.buttonColors(backgroundColor = Colors.errorColor), onClick = {
                            onCancel()
                        }, modifier = Modifier.padding(top = 10.dp, end = 10.dp).fillMaxWidth(0.5f)) {
                            Text("Cancel", maxLines = 1, overflow = TextOverflow.Ellipsis, color = Colors.textColor)
                        }
                        Button(colors = ButtonDefaults.buttonColors(backgroundColor = Colors.confirmColor), onClick = {
                            if (PrinterStore.configs.any { it.name == nameState.text } || nameState.text.isBlank()) nameErrorState = true
                            if (!InetAddressValidator.getInstance().isValidInet4Address(ipState.text)) ipErrorState = true
                            if (!hotendState.text.isNumber()) hotendErrorState = true
                            if (!bedState.text.isNumber()) bedErrorState = true
                            if (!xState.text.isNumber()) xErrorState = true
                            if (!yState.text.isNumber()) yErrorState = true
                            if (!zState.text.isNumber()) zErrorState = true
                            if (!nameErrorState && !ipErrorState && !hotendErrorState && !bedErrorState && !xErrorState && !yErrorState && !zErrorState) {
                                val config = PrinterConfig(
                                    nameState.text, "http://${ipState.text}:7125", PrinterCapabilities(
                                        hotendState.text.toInt(),
                                        bedState.text.toInt(),
                                        xState.text.toInt(),
                                        yState.text.toInt(),
                                        zState.text.toInt(),
                                        chamberState,
                                    )
                                )
                                PrinterStore.configs.add(config)
                                val stats = PrinterStats(config)
                                PrinterStore.printerStats.add(stats)
                                onPrinterAdd(stats)
                            }
                        }, modifier = Modifier.padding(top = 10.dp, start = 10.dp).fillMaxWidth()) {
                            Text("Okay", maxLines = 1, overflow = TextOverflow.Ellipsis, color = Colors.textColor)
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun PrinterOverviewPage(printer: PrinterStats) {
        var showFilamentDropdown by remember { mutableStateOf(false) }
        Card {
            LazyColumn {
                item { Text(printer.config.name, color = Colors.mediumGray, fontSize = 20.sp, modifier = Modifier.padding(5.dp)) }
                item { Text(printer.status.value ?: "Loading", color = Colors.mediumGray, fontSize = 15.sp, modifier = Modifier.padding(5.dp)) }
                item { LinearProgressIndicator(printer.currentPrintProgress.value ?: 0f, color = printer.printerColor, modifier = Modifier.padding(5.dp)) }
                item {
                    Row(horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.size(75.dp).padding(10.dp).clip(RoundedCornerShape(10.dp))
                                .background(printer.loadedFilament.value?.color?.toColor() ?: Colors.mediumGray).clickable { showFilamentDropdown = true }
                        ) {
                            DropdownMenu(showFilamentDropdown, onDismissRequest = {
                                showFilamentDropdown = false
                            }) {
                                FilamentStore.filaments.forEach {
                                    DropdownMenuItem(onClick = {
                                        printer.loadedFilament.value = it
                                        showFilamentDropdown = false
                                    }) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Box(modifier = Modifier.size(30.dp).padding(5.dp).clip(RoundedCornerShape(5.dp)).background(it.color.toColor()))
                                            Text(it.name)
                                        }

                                    }
                                }
                            }
                        }
                        Text(printer.loadedFilament.value?.name ?: "Loading", color = Colors.mediumGray, modifier = Modifier.padding(5.dp))
                    }
                }
                item {
                    Box(modifier = Modifier.height(200.dp).padding(5.dp)) {
                        LineChart(LineData(printer.printerColor, printer.extruderTemps.withIndex().map { Pair(it.index.toFloat(), it.value) }))
                    }
                }
                item {
                    Box(modifier = Modifier.height(200.dp).padding(5.dp)) {
                        LineChart(LineData(printer.printerColor, printer.bedTemps.withIndex().map { Pair(it.index.toFloat(), it.value) }))
                    }
                }
            }
        }
    }

    private fun String.isNumber(): Boolean = Regex("[0-9]+").matches(this)
}