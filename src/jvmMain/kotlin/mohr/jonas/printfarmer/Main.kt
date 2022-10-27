package mohr.jonas.printfarmer

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import mohr.jonas.printfarmer.data.printer.PrinterCapabilities
import mohr.jonas.printfarmer.data.printer.PrinterConfig
import mohr.jonas.printfarmer.data.printer.PrinterStats
import mohr.jonas.printfarmer.stores.FilamentStore
import mohr.jonas.printfarmer.stores.PrinterStore
import mohr.jonas.printfarmer.ui.NavController
import mohr.jonas.printfarmer.ui.NavController.getByClass
import mohr.jonas.printfarmer.ui.NavbarItems
import mohr.jonas.printfarmer.ui.components.Navbar
import mohr.jonas.printfarmer.ui.pages.*
import kotlin.time.Duration.Companion.seconds

@Composable
@Preview
fun App() {
    MaterialTheme {
        LaunchedEffect(Unit) {
            val config = PrinterConfig("PrinterName", "http://localhost:7125", PrinterCapabilities(200, 200, 200, 200, 200, false))
            val config2 = PrinterConfig("Yggdrasil", "http://192.168.178.144:7125", PrinterCapabilities(310, 110, 220, 220, 130, true))
            PrinterStore.configs.add(config)
            PrinterStore.configs.add(config2)
            PrinterStore.printerStats.add(PrinterStats(config))
            PrinterStore.printerStats.add(PrinterStats(config2))
            while (true) {
                PrinterStore.updateStore()
                FilamentStore.updateStore()
                delay(5.seconds)
            }
        }
        var selectedNavbarItem by remember { mutableStateOf(NavbarItems.OVERVIEW) }
        Row(modifier = Modifier.fillMaxSize()) {
            Navbar(selectedNavbarItem, NavbarItems.values().toList(), onValueChange = {
                selectedNavbarItem = it
            }, transformer = {
                when (it) {
                    NavbarItems.OVERVIEW -> painterResource("home_FILL0_wght400_GRAD0_opsz48.png")
                    NavbarItems.QUEUE -> painterResource("queue.png")
                    NavbarItems.PRINTERS -> painterResource("3d-printing.png")
                    NavbarItems.FILAMENTS -> painterResource("material.png")
                    NavbarItems.SETTINGS -> painterResource("settings_FILL0_wght400_GRAD0_opsz48.png")
                }
            })
            when (selectedNavbarItem) {
                NavbarItems.OVERVIEW -> NavController.transition(NavController.pages.getByClass(OverviewPage::class.java), null)
                NavbarItems.QUEUE -> NavController.transition(NavController.pages.getByClass(QueuingPage::class.java), null)
                NavbarItems.PRINTERS -> NavController.transition(NavController.pages.getByClass(PrinterPage::class.java), null)
                NavbarItems.FILAMENTS -> NavController.transition(NavController.pages.getByClass(FilamentPage::class.java), null)
                NavbarItems.SETTINGS -> NavController.transition(NavController.pages.getByClass(SettingsPage::class.java), null)
            }
            NavController.currentPage.value.display(NavController.currentPageData)
        }
    }
}

fun main() = application {
    Window(
        onCloseRequest = {
            PrinterStore.writeToFile()
            exitApplication()
        },
        title = "Printfarmer",
        icon = painterResource("farmer.png")
    ) { App() }
}
