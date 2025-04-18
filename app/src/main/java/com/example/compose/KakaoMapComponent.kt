// KakaoMapComponent.kt
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

private const val TAG = "KakaoMapView"

/**
 * 카카오맵을 표시하고 검색 결과 마커를 찍는 컴포저블
 * @param places 검색된 장소 리스트
 * @param selectedPlace 현재 선택된 장소
 * @param onMapClick 지도 클릭 이벤트 핸들러
 * @param onMarkerClick 마커 클릭 이벤트 핸들러
 */
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

    // 지도 객체와 마커 레이어 관리
    val mapState = remember { MapState() }

    // 맵 초기화 상태 추적
    var isMapReady by remember { mutableStateOf(false) }

    // 라이프사이클 관리
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
                    start(
                        object : MapLifeCycleCallback() {
                            override fun onMapResumed() {
                                Log.d(TAG, "onMapResumed called")
                            }
                            override fun onMapDestroy() {
                                Log.d(TAG, "onMapDestroy called")
                            }
                            override fun onMapError(error: Exception?) {
                                Log.e(TAG, "onMapError: ${error?.message}", error)
                            }
                        },
                        object : KakaoMapReadyCallback() {
                            override fun onMapReady(kakaoMap: KakaoMap) {
                                Log.d(TAG, "onMapReady called - map initialized")
                                // 지도 객체 저장
                                mapState.map = kakaoMap

                                // LabelLayerOptions 생성 - ID 지정
                                val layerOptions = LabelLayerOptions.from("marker_layer")
                                Log.d(TAG, "Creating label layer with options: $layerOptions")

                                // 레이어 추가
                                val labelManager = kakaoMap.labelManager
                                if (labelManager == null) {
                                    Log.e(TAG, "labelManager is null!")
                                    return
                                }

                                val labelLayer = labelManager.addLayer(layerOptions)
                                if (labelLayer == null) {
                                    Log.e(TAG, "Failed to create label layer!")
                                    return
                                }

                                Log.d(TAG, "Label layer successfully created: ${labelLayer.layerId}")
                                // 생성한 레이어를 mapState에 저장해야 함
                                mapState.labelLayer = labelLayer

                                val initialPosition = LatLng.from(37.566826, 126.9786567)
                                val cameraUpdate = CameraUpdateFactory.newCenterPosition(initialPosition, 12)
                                kakaoMap.moveCamera(cameraUpdate)
                                Log.d(TAG, "Initial camera position set")

                                // 맵 초기화 완료 표시
                                isMapReady = true

                                // 선택된 장소가 있으면 카메라 이동
                                selectedPlace?.let {
                                    val position = LatLng.from(it.latitude, it.longitude)
                                    Log.d(TAG, "Moving camera to selected place: ${it.name}")
                                    val selectedCameraUpdate = CameraUpdateFactory.newCenterPosition(position, 15)
                                    kakaoMap.moveCamera(selectedCameraUpdate, CameraAnimation.from(300))
                                }
                            }
                        }
                    )
                }
                mapView
            }
        )
    }

    // 맵이 준비되고 장소가 있을 때만 마커 업데이트
    LaunchedEffect(isMapReady, places) {
        if (!isMapReady) {
            Log.d(TAG, "Map not ready yet, waiting for initialization")
            return@LaunchedEffect
        }

        Log.d(TAG, "Map is ready and places updated: ${places.size} places")

        if (places.isEmpty()) {
            Log.d(TAG, "No places to display, skipping marker update")
            return@LaunchedEffect
        }

        if (mapState.map == null) {
            Log.e(TAG, "Map is null, cannot add markers")
            return@LaunchedEffect
        }

        if (mapState.labelLayer == null) {
            Log.e(TAG, "Label layer is null, cannot add markers")
            return@LaunchedEffect
        }

        // 기존 마커 삭제
        mapState.labelLayer?.removeAll()
        mapState.markerMap.clear()
        Log.d(TAG, "Cleared existing markers")

        // 검색 결과 마커 추가
        places.forEachIndexed { index, place ->
            val position = LatLng.from(place.latitude, place.longitude)
            Log.d(TAG, "Adding marker for place[$index]: ${place.name} at position: $position")

            // ID 생성
            val labelId = UUID.randomUUID().toString()

            // 마커와 장소 정보 매핑
            mapState.markerMap[labelId] = place

            // 마커 스타일 생성 - 기본 스타일 사용 (아이콘 리소스 없이도 마커가 표시됨)
            try {
                // 기본 스타일 사용하여 마커 생성
                val labelStyle = LabelStyle.from(R.drawable.outline_label_24)

                val labelStyles = LabelStyles.from(labelStyle)


                // 마커 추가 - 레이블 ID 포함
                val labelOptions = LabelOptions.from(labelId, position).apply {
                    styles = labelStyles
                    // 마커 클릭 이벤트 설정
                    clickable = true
                }

                val addedLabel = mapState.labelLayer?.addLabel(labelOptions)
                if (addedLabel == null) {
                    Log.e(TAG, "Failed to add label for place: ${place.name}")
                } else {
                    Log.d(TAG, "Successfully added label for place: ${place.name}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error adding label for place: ${place.name}", e)
            }
        }

        // 이전 방식과 동일하게 유지 (마커 클릭 이벤트 별도 처리 없음)
        Log.d(TAG, "마커 설정 완료 - 클릭 이벤트는 별도로 처리되지 않음")

        Log.d(TAG, "Finished adding ${places.size} markers to map")
    }

    // 선택된 장소가 변경되면 해당 위치로 카메라 이동
    LaunchedEffect(isMapReady, selectedPlace) {
        if (!isMapReady) {
            Log.d(TAG, "Map not ready yet for camera movement")
            return@LaunchedEffect
        }

        Log.d(TAG, "LaunchedEffect(selectedPlace) triggered: $selectedPlace")

        selectedPlace?.let {
            val position = LatLng.from(it.latitude, it.longitude)
            Log.d(TAG, "Moving camera to selected place: ${it.name} at position: $position")

            val cameraUpdate = CameraUpdateFactory.newCenterPosition(position, 15)
            mapState.map?.moveCamera(cameraUpdate, CameraAnimation.from(300))
        }
    }
}

/**
 * 지도 상태를 관리하는 클래스
 */
private class MapState {
    var map: KakaoMap? = null
    var labelLayer: LabelLayer? = null
    val markerMap = mutableMapOf<String, PlaceSearchResult>() // 마커 ID와 장소 정보 매핑
}