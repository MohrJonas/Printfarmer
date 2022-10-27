package mohr.jonas.printfarmer.ui.components

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.NavigationRail
import androidx.compose.material.NavigationRailItem
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import mohr.jonas.printfarmer.ui.Colors

@Composable
fun <T> Navbar(selected: T, items: List<T>, onValueChange: (T) -> Unit, transformer: @Composable (T) -> Painter) {
    NavigationRail(modifier = Modifier.fillMaxHeight().width(70.dp), backgroundColor = Colors.lightGray) {
        items.forEach {
            val icon = transformer(it)
            NavigationRailItem(
                icon = { Icon(icon, null, modifier = Modifier.size(32.dp)) },
                onClick = { onValueChange(it) },
                selected = selected == it,
                selectedContentColor = Color.Black,
                unselectedContentColor = Colors.mediumGray
            )
        }
    }
}