package com.example.weatherapp.ui.theme

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.ElectricMeter
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbCloudy
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.api.NetworkResponse
import com.example.weatherapp.api.WeatherModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherPage(viewModel: WeatherViewModel) {

    var city by remember {
        mutableStateOf("")
    }

    val weatherResult = viewModel.weatherResult.observeAsState()


    val keyBoardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(color = Color(0xffe8fbff))
            .fillMaxHeight()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Weather App",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Get real-time weather information for any location",
                fontSize = 16.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .background(
                        color = Color(0xFFF9FFFF),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .border(
                        width = 2.dp,
                        color = Color(0xFF00BCD4),
                        shape = RoundedCornerShape(28.dp)
                    )
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = city,
                        onValueChange = { city = it },
                        placeholder = { Text("Search location...") },
                        colors = TextFieldDefaults.textFieldColors(
                            containerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )

                    IconButton(onClick = {
                        viewModel.getData(city)
                        keyBoardController?.hide()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White,
                            modifier = Modifier
                                .size(32.dp)
                                .background(
                                    color = Color(0xFF00BCD4),
                                    shape = CircleShape
                                )
                                .padding(6.dp)
                        )
                    }
                }
            }
        }
        when (val result = weatherResult.value) {
            is NetworkResponse.Error -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = result.message, color = Color.Red,

                        fontSize = 16.sp
                    )
                }
            }

            NetworkResponse.Loading -> {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            is NetworkResponse.Success -> {
                AnimatedVisibility(
                    visible = true, // Show only when data is available
                    enter = fadeIn() + slideInVertically(initialOffsetY = { fullHeight -> fullHeight }),
                    exit = fadeOut()
                ) {
                    WeatherDetails(result.data)
                }

            }

            null -> {}
        }

    }
}


@Composable
fun WeatherDetails(data: WeatherModel) {
    val location = data.location
    val current = data.current
    val condition = current.condition
    val temperatureCelsius = current.temp_c.toDouble()
    val humidity = current.humidity.toDouble()

    val suggestion = when {
        temperatureCelsius > 35 -> "It's very hot! Best to stay indoors during the afternoon."
        temperatureCelsius in 25.0..35.0 && humidity > 70 -> "Hot and humid — keep yourself hydrated!"
        temperatureCelsius in 18.0..25.0 -> "Perfect weather — plan something outdoors!"
        temperatureCelsius < 15 -> "Chilly weather. Time to layer up!"
        else -> "This place is suitable for your next journey."
    }

    // Weather card content
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        shape = RoundedCornerShape(15.dp),
        colors = CardColors(
            containerColor = Color(0xFF303e52),
            contentColor = Color.White,
            disabledContainerColor = Color.White,
            disabledContentColor = Color.White
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${location.name}, ${location.country}",
                fontSize = 20.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(5.dp))
            Text(
                text = location.localtime,
                fontSize = 14.sp,
                color = Color.LightGray,
                fontFamily = FontFamily.SansSerif
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${current.temp_c}°",
                        fontSize = 42.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(text = condition.text, color = Color.LightGray)
                    Text(
                        text = "Feels like ${current.feelslike_c}°C",
                        color = Color.Gray,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Column(modifier = Modifier.fillMaxWidth()) {
                WeatherGridItem("Humidity", "${current.humidity}%", Icons.Default.WaterDrop)
                WeatherGridItem(
                    "Wind",
                    "${current.wind_kph} km/h (${current.wind_dir})",
                    Icons.Default.Air
                )
                WeatherGridItem("Visibility", "${current.vis_km} km", Icons.Default.RemoveRedEye)
                WeatherGridItem(
                    "Pressure",
                    "${current.pressure_mb} mb",
                    Icons.Default.ElectricMeter
                )
                WeatherGridItem("UV Index", "${current.uv}", Icons.Default.WbSunny)
                WeatherGridItem("Cloud Cover", "${current.cloud}%", Icons.Default.WbCloudy)
            }

            LocationMap(location.lat.toDouble(), location.lon.toDouble())

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = suggestion,
                fontSize = 14.sp,
                color = Color.White
            )
        }
    }
}

@Composable
fun WeatherGridItem(label: String, value: String, icon: ImageVector) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardColors(
            containerColor = Color(0xFF344256),

            disabledContainerColor = Color.White,
            contentColor = Color.White,
            disabledContentColor = Color.White,
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = label,
                tint = Color.Cyan,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(text = label, color = Color.LightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun LocationMap(lat: Double, lon: Double) {
    val cameraPosition = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(lat, lon), 12f)
    }
    GoogleMap(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        cameraPositionState = cameraPosition
    ) {
        Marker(
            state = MarkerState(position = LatLng(lat, lon)),
            title = "Selected Location"
        )
    }
}