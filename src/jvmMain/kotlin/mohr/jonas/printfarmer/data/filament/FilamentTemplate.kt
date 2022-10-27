package mohr.jonas.printfarmer.data.filament

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

@Serializable
data class FilamentTemplate(val name: String, val brand: String, val material: Material, val color: String) {

    companion object {
        val empty = FilamentTemplate("Empty", "Empty", Material.PLA, "#ffffff")
    }

}

fun String.toColor() = java.awt.Color.decode(this).let { Color(it.red, it.green, it.blue, it.alpha) }