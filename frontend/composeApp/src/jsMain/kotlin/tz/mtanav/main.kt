package tz.mtanav

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import tz.mtanav.ui.map.LocationNavigator

external fun onWasmReady(onReady: () -> Unit)

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    onWasmReady {
        CanvasBasedWindow(title = "MTANAV", canvasElementId = "ComposeTarget") {
            App(locationNavigator = LocationNavigator())
        }
    }
}
