package com.terra.mobile.composable

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ToggleOff
import androidx.compose.material.icons.filled.ToggleOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.Polygon
import com.google.maps.android.compose.rememberCameraPositionState
import com.terra.mobile.data.UserState
import com.terra.mobile.map.MapEvent
import com.terra.mobile.model.EarthquakeRequest
import com.terra.mobile.model.SoilAriaRequest
import com.terra.mobile.model.SoilCode
import com.terra.mobile.model.toEarthquakePolygonDTOList
import com.terra.mobile.model.toSoilPolygonDTOList
import com.terra.mobile.view.model.MapsViewModel
import com.terra.mobile.view.model.UserViewModel
import kotlin.random.Random

val mapFill: Color = Color(245, 189, 133, 136)
val mapLine: Color = Color(0, 0, 0, 89)

@Composable
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
fun MapScreen(
    mapViewModel: MapsViewModel = viewModel(), userModel: UserViewModel
) {
    val scaffoldState = remember { SnackbarHostState() }
    val uiSettings = remember {
        MapUiSettings(zoomControlsEnabled = false)
    }
    val singapore = LatLng(40.79, -73.18)//bg 42.7339, 25.4858)//NY
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(singapore, 8f)
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                mapViewModel.onEvent(MapEvent.ToggleFalloutMap)
            }) {
                Icon(
                    imageVector = if (mapViewModel.state.isEarthquakeMap) {
                        Icons.Default.ToggleOff
                    } else Icons.Default.ToggleOn, contentDescription = "Toggle Fallout map"
                )
            }
        }) {
        //val singapore = LatLng(-23.684, 133.903)//Avstraliq
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            properties = mapViewModel.state.properties,
            uiSettings = uiSettings,
            onMapLongClick = {
                //TODO Think of what to a long click will do
                mapViewModel.onEvent(MapEvent.OnMapLongClick(it))
            },
            cameraPositionState = cameraPositionState
        ) {

            val polyline1 = listOf(
                LatLng(-35.016, 143.321), LatLng(-34.747, 145.592), LatLng(-34.364, 147.891)
            )
            if (!cameraPositionState.isMoving) {
                if (!mapViewModel.state.isEarthquakeMap) {
                    Polygon(points = polyline1)
                    val soilAria = SoilAriaRequest(
                        cameraPositionState.position.target.longitude,
                        cameraPositionState.position.target.latitude,
                        2.0
                    )
                    Log.w("Camera aria", soilAria.toString())

                    mapViewModel.getSoil(
                        (userModel.userUiState as UserState.Success).getTokken(),
                        soilAria
                    )
                    var soilState = remember {
                        mutableStateOf(mapViewModel.state)
                    }
                    if (!soilState.value._soil.isEmpty()) {
                        var poligons = remember { mutableStateOf(mapViewModel.state._soil) }
                        val soilPolygonDTO = poligons.value.toSoilPolygonDTOList()
                        Log.w("Polygons", soilPolygonDTO.toString())
                        soilPolygonDTO.forEach { poligon ->
                            getColorForValue(poligon.soilNumber)?.let { it1 ->
                                Polygon(
                                    points = poligon.coordinates,
                                    fillColor = it1,
                                    strokeColor = mapLine
                                )
                            }
                            Marker(
                                position = LatLng(
                                    poligon.centerPoint.latitude, poligon.centerPoint.longitude
                                ),
                                title = "Soil Number: ${poligon.soilNumber}, Soil Type: ${poligon.soilType}",
                                snippet = "Long click to ...",
                                onInfoWindowLongClick = {
                                },
                                onClick = {
                                    it.showInfoWindow()
                                    true
                                },
                                icon = BitmapDescriptorFactory.defaultMarker(
                                    BitmapDescriptorFactory.HUE_GREEN
                                )
                            )
                        }
                    }
                }else{
                    val earthquakeAria = EarthquakeRequest(
                        cameraPositionState.position.target.longitude,
                        cameraPositionState.position.target.latitude,
                        10.0
                    )

                    mapViewModel.getEarthquakel(
                        (userModel.userUiState as UserState.Success).getTokken(),
                        earthquakeAria
                    )
                    var earthquakeState = remember {
                        mutableStateOf(mapViewModel.state)
                    }
                    if (!earthquakeState.value._earthquakel.isEmpty()) {
                        var poligons = remember { mutableStateOf(mapViewModel.state._earthquakel) }
                        Log.w("poligons.value", poligons.value.toString())
                        val earthquakePolygonDTO = poligons.value.toEarthquakePolygonDTOList()
                        Log.w("Polygons", earthquakePolygonDTO.toString())
                        earthquakePolygonDTO.forEach { poligon ->
                            getColorForValue(poligon.magnitude)?.let { it1 ->
                                Polygon(
                                    points = poligon.coordinates,
                                    fillColor = it1,
                                    strokeColor = mapLine
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}


fun getColorForValue(inputValue: Int): Color? {
    if(SoilCode.values().any { it.soilType==inputValue }){
        return SoilCode.values().find { it.soilType == inputValue }?.color
    }
    val random = Random.Default
    return Color(random.nextInt(), random.nextInt(), random.nextInt(), 136)
}