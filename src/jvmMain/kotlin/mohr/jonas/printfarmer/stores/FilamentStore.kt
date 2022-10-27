package mohr.jonas.printfarmer.stores

import androidx.compose.runtime.mutableStateListOf
import mohr.jonas.printfarmer.data.filament.FilamentClient
import mohr.jonas.printfarmer.data.filament.FilamentTemplate

//TODO obviously
object FilamentStore {

    @get:Synchronized
    @set:Synchronized
    var filaments = mutableStateListOf<FilamentTemplate>()

    fun updateStore() {
        filaments.clear()
        filaments.addAll(FilamentClient.getAllFilaments())
    }

}