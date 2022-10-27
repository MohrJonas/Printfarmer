package mohr.jonas.printfarmer.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import mohr.jonas.printfarmer.ui.Colors

@Composable
fun Card(modifier: Modifier = Modifier, children: @Composable () -> Unit) {
    Box(modifier = modifier.fillMaxWidth().padding(10.dp).clip(RoundedCornerShape(10.dp)).background(Colors.lightGray), contentAlignment = Alignment.Center) {
        children()
    }
}