package com.example.weatherapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.ui.theme.SplashScreen

import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.theme.WeatherPage
import com.example.weatherapp.ui.theme.WeatherViewModel
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val weatherViewModel=ViewModelProvider(this)[WeatherViewModel::class.java]
        setContent {
            var showSplash by remember { mutableStateOf(true) }
            LaunchedEffect(true) {
                delay(2000)
                showSplash = false
            }
            Crossfade(targetState = showSplash, label = "SplashToHomeTransition") { splash ->
                if (splash) {
                    SplashScreen{
                        showSplash=false
                    }
                } else {
                    WeatherPage(weatherViewModel)
                }
            }

        }
    }
}

