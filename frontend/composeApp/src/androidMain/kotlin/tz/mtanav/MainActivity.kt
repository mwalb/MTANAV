package tz.mtanav

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import tz.mtanav.ui.map.LocationNavigator

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locationNavigator = LocationNavigator(this)
        setContent {
            App(locationNavigator = locationNavigator)
        }
    }
}
