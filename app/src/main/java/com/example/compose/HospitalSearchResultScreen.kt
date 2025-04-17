// HospitalSearchResultScreen.kt
package com.example.compose.ui.screens

import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.compose.ui.theme.Purple80
import com.example.compose.KakaoMap.KakaoMap

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HospitalSearchResultScreen(
    onNavigateBack: () -> Unit,
    searchQuery: String
) {
    val hospitals = remember {
        // 임시 데이터 - 실제로는 API를 통해 가져올 것입니다
        listOf(
            Hospital(
                id = "1",
                name = "서울 어린이병원",
                address = "서울특별시 서초구 헌릉로 260",
                phone = "02-570-8000",
                rating = 4.5f,
                distance = "1.2km",
                latitude = 37.4653693,
                longitude = 127.0457738
            ),
            Hospital(
                id = "2",
                name = "연세세브란스병원",
                address = "서울특별시 서대문구 연세로 50-1",
                phone = "02-2228-0114",
                rating = 4.3f,
                distance = "2.5km",
                latitude = 37.5623289,
                longitude = 126.9379873
            ),
            Hospital(
                id = "3",
                name = "삼성서울병원",
                address = "서울특별시 강남구 일원로 81",
                phone = "02-3410-2114",
                rating = 4.7f,
                distance = "3.1km",
                latitude = 37.4881094,
                longitude = 127.0854771
            )
        )
    }

    // 선택된 병원 정보
    var selectedHospital by remember { mutableStateOf(hospitals[0]) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("'$searchQuery' 검색 결과") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 지도 영역
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                //카카오맵 함수
                KakaoMap()

                // 검색 결과 카운트 표시
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Text(
                        text = "검색 결과 ${hospitals.size}개",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // 병원 목록
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.White)
            ) {
                items(hospitals) { hospital ->
                    HospitalItem(
                        hospital = hospital,
                        isSelected = hospital.id == selectedHospital.id,
                        onClick = { selectedHospital = hospital }
                    )
                }
            }
        }
    }
}

@Composable
fun HospitalItem(
    hospital: Hospital,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFFF0F0F0) else Color.White
        ),
        elevation = CardDefaults.cardElevation(if (isSelected) 4.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 병원명 및 평점
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = hospital.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                // 평점 표시
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "평점",
                        tint = Color(0xFFFFA000),
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Text(
                        text = hospital.rating.toString(),
                        fontSize = 14.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // 거리 표시
                    Text(
                        text = hospital.distance,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 주소
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "주소",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = hospital.address,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // 전화번호
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "전화번호",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = hospital.phone,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 예약 버튼
            if (isSelected) {
                Button(
                    onClick = { /* 예약 화면으로 이동 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Purple80
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "예약하기",
                        color = Color.Black
                    )
                }
            }
        }
    }
}

data class Hospital(
    val id: String,
    val name: String,
    val address: String,
    val phone: String,
    val rating: Float,
    val distance: String,
    val latitude: Double,
    val longitude: Double
)