package com.example.compose.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.compose.data.PlaceSearchResult
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.LatLng
import com.kakao.vectormap.MapLifeCycleCallback
import com.kakao.vectormap.MapView
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelLayer
import com.kakao.vectormap.label.LabelLayerOptions
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import java.util.UUID
import com.example.compose.R
import com.kakao.vectormap.label.OrderingType

private const val TAG = "KakaoMapView"

@Composable
fun KakaoMapView(
    places: List<PlaceSearchResult> = emptyList(),
    selectedPlace: PlaceSearchResult? = null,
    onMapClick: () -> Unit = {},
    onMarkerClick: (PlaceSearchResult) -> Unit = {}
) {
    val context = LocalContext.current
    val mapView = remember { MapView(context) }
    val lifecycleOwner = LocalLifecycleOwner.current

    val mapState = remember { MapState() }
    var isMapReady by remember { mutableStateOf(false) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> mapView.resume()
                Lifecycle.Event.ON_PAUSE -> mapView.pause()
                Lifecycle.Event.ON_DESTROY -> mapView.finish()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = {
                mapView.apply {
                    start(object : MapLifeCycleCallback() {
                        override fun onMapDestroy() {}
                        override fun onMapError(error: Exception?) {
                            Log.e(TAG, "Map initialization error", error)
                        }
                    }, object : KakaoMapReadyCallback() {
                        override fun onMapReady(kakaoMap: KakaoMap) {
                            mapState.map = kakaoMap

                            kakaoMap.labelManager?.let { labelManager ->
                                // 먼저 스타일 등록
                                val defaultStyle = LabelStyle.from(R.drawable.ic_marker)
                                labelManager.addLabelStyles(LabelStyles.from("defaultStyle", defaultStyle))

                                val layerOptions = LabelLayerOptions.from("marker_layer")
                                    .setOrderingType(OrderingType.Rank)
                                    .setZOrder(1)

                                mapState.labelLayer = labelManager.addLayer(layerOptions)
                            }

                            val initialPosition = LatLng.from(37.566826, 126.9786567)
                            val cameraUpdate =
                                CameraUpdateFactory.newCenterPosition(initialPosition, 12)
                            kakaoMap.moveCamera(cameraUpdate)

                            isMapReady = true
                            Log.d(TAG, "Map is ready")
                        }
                    })
                }
                mapView
            }
        )
    }

    LaunchedEffect(isMapReady, places) {
        if (!isMapReady || mapState.labelLayer == null) return@LaunchedEffect

        try {
            // 레이어 상태 체크
            mapState.labelLayer?.let { layer ->
                val layerId = layer.getLayerId()
                val labelCount = layer.getLabelCount()
                val zOrder = layer.getZOrder()
                Log.d(TAG, "Layer ID: $layerId")
                Log.d(TAG, "Initial Label Count: $labelCount")
                Log.d(TAG, "Layer Z-Order: $zOrder")
            }

            mapState.labelLayer?.removeAll()
            mapState.markerMap.clear()

            places.forEach { place ->
                try {
                    val position = LatLng.from(place.latitude, place.longitude)
                    val labelId = UUID.randomUUID().toString()

                    // 마커 생성 부분도 약간 수정
                    val labelStyle = LabelStyle.from(R.drawable.ic_marker)
                    val labelStyles = mapState.map?.labelManager?.addLabelStyles(
                        LabelStyles.from(labelStyle)
                    ) // labelManager를 통해 스타일 추가

                    // labelOptions에서 직접 스타일 사용
                    val labelOptions = LabelOptions.from(labelId, position).apply {
                        styles = mapState.map?.labelManager?.getLabelStyles("defaultStyle")
                        setRank(1)
                        setVisible(true)
                    }
                    // 생성 후 상태 확인
                    Log.d(TAG, """
                        Label Options State for ${place.name}:
                        - ID: $labelId
                        - Position: $position
                        - Visible: ${labelOptions.isVisible()}
                        - Has Styles: ${labelOptions.styles != null}
                    """.trimIndent())

                    mapState.labelLayer?.addLabel(labelOptions)?.let { label ->
                        label.show()

                        // 라벨이 실제로 레이어에 있는지 확인
                        val isLabelInLayer = mapState.labelLayer?.hasLabel(labelId) ?: false
                        val currentLabelCount = mapState.labelLayer?.getLabelCount() ?: 0

                        Log.d(TAG, """
                        Marker ${place.name}:
                        - Label ID: $labelId
                        - Is in layer: $isLabelInLayer
                        - Current label count: $currentLabelCount
                    """.trimIndent())

                        mapState.markerMap[labelId] = place
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error adding marker for place: ${place.name}", e)
                }
            }

            // 최종 상태 확인
            mapState.labelLayer?.let { layer ->
                val finalLabelCount = layer.getLabelCount()
                val isLayerVisible = layer.isVisible()
                Log.d(TAG, """
                Final Layer State:
                - Label Count: $finalLabelCount
                - Is Visible: $isLayerVisible
            """.trimIndent())
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error in marker update process", e)
        }
    }

    LaunchedEffect(selectedPlace) {
        if (!isMapReady || selectedPlace == null) return@LaunchedEffect

        try {
            val position = LatLng.from(selectedPlace.latitude, selectedPlace.longitude)
            val cameraUpdate = CameraUpdateFactory.newCenterPosition(position, 15)
            mapState.map?.moveCamera(cameraUpdate, CameraAnimation.from(300))
            Log.d(TAG, "Camera moved to selected place: ${selectedPlace.name}")
        } catch (e: Exception) {
            Log.e(TAG, "Error moving camera to selected place", e)
        }
    }
}

private class MapState {
    var map: KakaoMap? = null
    var labelLayer: LabelLayer? = null
    val markerMap = mutableMapOf<String, PlaceSearchResult>()
}