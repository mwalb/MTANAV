package tz.mtanav

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import tz.mtanav.ui.map.LocationNavigator

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    CanvasBasedWindow(title = "MTANAV", canvasElementId = "ComposeTarget") {
        App(locationNavigator = LocationNavigator())
    }
}
