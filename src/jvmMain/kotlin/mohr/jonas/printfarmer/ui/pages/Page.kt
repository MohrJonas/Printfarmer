package mohr.jonas.printfarmer.ui.pages

import androidx.compose.runtime.Composable

interface Page {

    @Composable
    fun display(data: Any?)

}