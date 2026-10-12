package tz.mtanav

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import tz.mtanav.ui.map.LocationNavigator

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val locationNavigator = LocationNavigator()
    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        App(locationNavigator = locationNavigator)
    }
}
