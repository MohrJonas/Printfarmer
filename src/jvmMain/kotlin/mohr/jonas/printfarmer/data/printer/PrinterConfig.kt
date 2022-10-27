package mohr.jonas.printfarmer.data.printer

import kotlinx.serialization.Serializable

@Serializable
data class PrinterConfig(
    val name: String,
    val ip: String,
    val capabilities: PrinterCapabilities
)
