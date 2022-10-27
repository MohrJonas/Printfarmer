package mohr.jonas.printfarmer

import mohr.jonas.printfarmer.data.printer.PrinterCapabilities

object GCodeParser {

    private val hotendTempRegex = Regex("^M10[49] S([0-9]+)\$")
    private val bedTempRegex = Regex("^M1[49]0 S([0-9]+)\$")
    private val chamberRegex = Regex("^; chamber_temperature = [0-9]+\$")
    private val moveRegex = Regex("^G[01] (?:X([0-9]+\\.?[0-9]*))? ?(?:Y([0-9]+\\.?[0-9]*))? ?(?:Z([0-9]+\\.?[0-9]*))? ?(?:[EF][0-9]+\\.?[0-9]*)?\$")

    fun getRequiredCapabilities(lines: List<String>): PrinterCapabilities {
        var hotendTemp = Int.MIN_VALUE
        var bedTemp = Int.MIN_VALUE
        var chamber = false
        var x = Float.MIN_VALUE
        var y = Float.MIN_VALUE
        var z = Float.MIN_VALUE
        lines.forEach {
            if (hotendTempRegex.matches(it)) hotendTemp = maxOf(hotendTemp, hotendTempRegex.find(it)!!.groupValues[1].toInt())
            if (bedTempRegex.matches(it)) bedTemp = maxOf(bedTemp, bedTempRegex.find(it)!!.groupValues[1].toInt())
            if (chamberRegex.matches(it)) chamber = true
            if (moveRegex.matches(it)) {
                val matches = moveRegex.find(it)!!.groupValues
                if (matches[1].isNotBlank()) x = maxOf(x, matches[1].toFloat())
                if (matches[2].isNotBlank()) y = maxOf(y, matches[2].toFloat())
                if (matches[3].isNotBlank()) z = maxOf(z, matches[3].toFloat())
            }
        }
        return PrinterCapabilities(hotendTemp, bedTemp, x.toInt(), y.toInt(), z.toInt(), chamber)
    }

}