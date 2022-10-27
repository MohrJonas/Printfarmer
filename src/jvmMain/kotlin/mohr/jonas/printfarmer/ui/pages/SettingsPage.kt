package mohr.jonas.printfarmer.ui.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import mohr.jonas.printfarmer.stores.FilamentStore
import mohr.jonas.printfarmer.stores.PrinterStore

class SettingsPage : Page {

    @Composable
    override fun display(data: Any?) {
        LaunchedEffect(Unit) {
            PrinterStore.updateStore()
            FilamentStore.updateStore()
        }
    }
}