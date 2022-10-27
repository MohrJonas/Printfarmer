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
import com.godaddy.android.colorpicker.ClassicColorPicker
import mohr.jonas.printfarmer.stores.FilamentStore
import mohr.jonas.printfarmer.data.filament.FilamentTemplate
import mohr.jonas.printfarmer.data.filament.Material
import mohr.jonas.printfarmer.data.filament.toColor
import mohr.jonas.printfarmer.stores.PrinterStore
import mohr.jonas.printfarmer.ui.Colors
import mohr.jonas.printfarmer.ui.SubNavbarItems
import mohr.jonas.printfarmer.ui.components.Card

class FilamentPage : Page {

    @Composable
    override fun display(data: Any?) {
        var selectedSubNavbarItem by remember { mutableStateOf(SubNavbarItems.OVERVIEW) }
        var selectedFilament by remember { mutableStateOf<FilamentTemplate?>(null) }
        LaunchedEffect(Unit) {
            PrinterStore.updateStore()
            FilamentStore.updateStore()
        }
        NavigationRail(modifier = Modifier.fillMaxHeight().width(150.dp), backgroundColor = Colors.lightGray) {
            FilamentStore.filaments.forEach {
                NavigationRailItem(
                    icon = {
                        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(painterResource("material.png"), null, modifier = Modifier.size(24.dp).padding(end = 5.dp))
                            Text(it.name, color = Colors.mediumGray)
                        }
                    },
                    onClick = {
                        selectedSubNavbarItem = SubNavbarItems.OVERVIEW
                        selectedFilament = it
                    },
                    selected = selectedSubNavbarItem == SubNavbarItems.OVERVIEW && selectedFilament == it,
                    selectedContentColor = Color.Black,
                    unselectedContentColor = Colors.mediumGray,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            NavigationRailItem(
                icon = {
                    Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Icon(painterResource("add_FILL0_wght400_GRAD0_opsz48.png"), null, modifier = Modifier.size(24.dp).padding(end = 5.dp))
                        Text("Add Filament", color = Colors.mediumGray)
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
        if (selectedSubNavbarItem == SubNavbarItems.ADD) NewFilamentPage(onFilamentAdd = {
            selectedFilament = it
            selectedSubNavbarItem = SubNavbarItems.OVERVIEW
        }, onCancel = {
            selectedFilament = null
            selectedSubNavbarItem = SubNavbarItems.OVERVIEW
        })
        else if (selectedSubNavbarItem == SubNavbarItems.OVERVIEW && selectedFilament != null) FilamentOverviewPage(selectedFilament!!)
    }

    @Composable
    fun NewFilamentPage(onFilamentAdd: (FilamentTemplate) -> Unit, onCancel: () -> Unit) {
        var nameState by remember { mutableStateOf(TextFieldValue()) }
        var brandState by remember { mutableStateOf(TextFieldValue()) }
        var materialState by remember { mutableStateOf<Material?>(null) }
        var dropdownExpanded by remember { mutableStateOf(false) }
        var colorState by remember { mutableStateOf("#ffffff") }
        var nameErrorState by remember { mutableStateOf(false) }
        var brandErrorState by remember { mutableStateOf(false) }
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
                    TextField(brandState, modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp), isError = brandErrorState, colors = TextFieldDefaults.textFieldColors(
                        textColor = Colors.lightGray,
                        backgroundColor = Colors.mediumGray,
                        errorCursorColor = Colors.errorColor,
                        focusedIndicatorColor = Colors.lightGray,
                        cursorColor = Colors.lightGray
                    ), onValueChange = {
                        brandErrorState = false
                        brandState = it
                    }, singleLine = true, label = { Text("Brand", color = Colors.textColor) })
                    Box(modifier = Modifier.background(Colors.lightGray).padding(5.dp).clickable {
                        dropdownExpanded = true
                    }) {
                        Text(materialState?.name ?: "-----", color = Colors.mediumGray)
                        DropdownMenu(dropdownExpanded, onDismissRequest = {
                            dropdownExpanded = false
                        }) {
                            Material.values().forEach {
                                DropdownMenuItem(onClick = {
                                    materialState = it
                                    dropdownExpanded = false
                                }) {
                                    Text(it.name)
                                }
                            }
                        }
                    }
                    ClassicColorPicker(showAlphaBar = false, modifier = Modifier.aspectRatio(2f)) {
                        val color = it.toColor()
                        colorState = String.format("#%02x%02x%02x", (255 * color.red).toInt(), (255 * color.green).toInt(), (255 * color.blue).toInt())
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        Button(colors = ButtonDefaults.buttonColors(backgroundColor = Colors.errorColor), onClick = {
                            onCancel()
                        }, modifier = Modifier.padding(top = 10.dp, end = 10.dp).fillMaxWidth(0.5f)) {
                            Text("Cancel", maxLines = 1, overflow = TextOverflow.Ellipsis, color = Colors.textColor)
                        }
                        Button(colors = ButtonDefaults.buttonColors(backgroundColor = Colors.confirmColor), onClick = {
                            if (nameState.text.isBlank()) nameErrorState = true
                            if (brandState.text.isBlank()) brandErrorState = true
                            if (!nameErrorState && !brandErrorState && materialState != null) {
                                val filament = FilamentTemplate(
                                    nameState.text, brandState.text, materialState!!, colorState
                                )
                                FilamentStore.filaments.add(filament)
                                onFilamentAdd(filament)
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
    fun FilamentOverviewPage(filamentTemplate: FilamentTemplate) {
        Card {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                item { Box(modifier = Modifier.size(75.dp).padding(10.dp).clip(RoundedCornerShape(10.dp)).background(filamentTemplate.color.toColor())) }
                item { Text(filamentTemplate.name, color = Colors.mediumGray, fontSize = 20.sp, modifier = Modifier.padding(5.dp)) }
                item { Text(filamentTemplate.brand, color = Colors.mediumGray, fontSize = 15.sp, modifier = Modifier.padding(5.dp)) }
                item { Text(filamentTemplate.material.name, color = Colors.mediumGray, fontSize = 15.sp, modifier = Modifier.padding(5.dp)) }
            }
        }
    }
}