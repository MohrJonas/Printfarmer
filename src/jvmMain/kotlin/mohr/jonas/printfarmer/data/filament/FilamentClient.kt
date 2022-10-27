package mohr.jonas.printfarmer.data.filament

import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.nio.file.Files
import java.nio.file.Path

object FilamentClient {

    private val path = Path.of(System.getenv("FILAMENT_PATH"))
    private val filaments: MutableList<AssociatedFilamentClient> = if (Files.exists(path))
        Json.decodeFromString(Files.readString(path))
    else mutableListOf()

    fun getAllFilaments() = filaments.map { it.filamentTemplate }

    fun getFilament(ip: String) =
        filaments.firstOrNull { it.printerIps.contains(ip) }?.filamentTemplate ?: FilamentTemplate("Empty", "", Material.PLA, "#dddddd")


    fun addFilament(filamentTemplate: FilamentTemplate) {
        filaments.add(AssociatedFilamentClient(filamentTemplate, mutableListOf()))
    }

    fun setPrinterFilament(filamentTemplate: FilamentTemplate, ip: String) {
        filaments.find { it.filamentTemplate == filamentTemplate }!!.printerIps.add(ip)
    }

    fun removePrinterFilament(filamentTemplate: FilamentTemplate, ip: String) {
        filaments.find { it.filamentTemplate == filamentTemplate }!!.printerIps.remove(ip)
    }

    fun removeFilament(filamentTemplate: FilamentTemplate) {
        filaments.removeIf { it.filamentTemplate == filamentTemplate }
    }
}

@Serializable
private data class AssociatedFilamentClient(val filamentTemplate: FilamentTemplate, val printerIps: MutableList<String>)