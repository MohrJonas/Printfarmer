package mohr.jonas.printfarmer.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import mohr.jonas.printfarmer.ui.Colors
import kotlin.math.abs

@Composable
fun LineChart(vararg lines: LineData) {
    if (lines.isEmpty()) return
    val maxXRange = Pair(0f, lines.maxOf { it.dataPoints.maxOfOrNull { it.first } ?: 1f })
    val maxYRange = Pair(0f, lines.maxOf { it.dataPoints.maxOfOrNull { it.second } ?: 1f })
    Canvas(modifier = Modifier.fillMaxSize()) {
        val xScale = (size.width / maxXRange.length())
        val yScale = (size.height / maxYRange.length())
        val path = Path()
        val stepSize = size.height / 4f
        for (i in 0..4) {
            path.moveTo(0f, i * stepSize)
            path.lineTo(size.width, i * stepSize)
        }
        drawPath(path, Colors.mediumGray, style = Stroke(width = 2.dp.toPx()))
        lines.forEach {
            val path = Path()
            path.moveTo(0f, size.height - it.dataPoints[0].second * yScale)
            it.dataPoints.forEach {
                path.lineTo(it.first * xScale, size.height - it.second * yScale)
            }
            drawPath(path, it.lineColor, style = Stroke(width = 2.dp.toPx()))
        }
    }
}

fun Pair<Float, Float>.length() = abs(this.first - this.second)

data class LineData(val lineColor: Color, val dataPoints: List<Pair<Float, Float>>)