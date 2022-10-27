package mohr.jonas.printfarmer.data.printer

import kotlinx.serialization.Serializable

@Serializable
data class PrinterCapabilities(
    val maxHotendTemp: Int,
    val maxBedTemp: Int,
    val maxX: Int,
    val maxY: Int,
    val maxZ: Int,
    val chamber: Boolean
) {
    companion object {
        fun PrinterCapabilities.fulfills(requirements: PrinterCapabilities) =
            this.maxHotendTemp >= requirements.maxHotendTemp &&
                    this.maxBedTemp >= requirements.maxBedTemp &&
                    this.maxX >= requirements.maxX &&
                    this.maxY >= requirements.maxY &&
                    this.maxZ >= requirements.maxZ &&
                    this.chamber == requirements.chamber
    }
}
