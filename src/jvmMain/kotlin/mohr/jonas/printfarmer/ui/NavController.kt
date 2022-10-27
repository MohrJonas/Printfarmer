package mohr.jonas.printfarmer.ui

import androidx.compose.runtime.mutableStateOf
import mohr.jonas.printfarmer.ui.pages.*

object NavController {

    val pages = mutableListOf<Page>()

    init {
        pages.add(OverviewPage())
        pages.add(QueuingPage())
        pages.add(PrinterPage())
        pages.add(FilamentPage())
        pages.add(SettingsPage())
    }

    fun transition(page: Page, data: Any?) {
        currentPage.value = page
        currentPageData = data
    }

    var currentPage = mutableStateOf(pages.getByClass(OverviewPage::class.java))
    var currentPageData: Any? = null

    fun MutableList<Page>.getByClass(clazz: Class<out Page>) = find { it.javaClass == clazz }!!

}