// ReservationHistoryScreen.kt
package com.example.compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.data.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReservationHistoryScreen(
    onNavigateBack: () -> Unit
) {
    // UserRepository에서 현재 로그인한 사용자 정보 가져오기
    val userRepository = UserRepository.getInstance()
    val currentUser by userRepository.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("예약 내역") },
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
        if (currentUser == null) {
            // 로그인되지 않은 경우
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인이 필요한 서비스입니다.",
                    fontSize = 18.sp
                )
            }
        } else {
            // 로그인된 경우, 예약 내역 표시
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // 임시 예약 데이터 생성
                val reservations = remember {
                    listOf(
                        Reservation(
                            id = "R001",
                            hospitalName = "서울 어린이 병원",
                            doctorName = "김의사",
                            department = "소아청소년과",
                            date = "2025년 4월 17일",
                            time = "오전 10:30",
                            status = "예약 확정"
                        ),
                        Reservation(
                            id = "R002",
                            hospitalName = "행복 가정의학과",
                            doctorName = "이의사",
                            department = "가정의학과",
                            date = "2025년 4월 15일",
                            time = "오후 2:00",
                            status = "방문 완료"
                        ),
                        Reservation(
                            id = "R003",
                            hospitalName = "미소 치과 의원",
                            doctorName = "박의사",
                            department = "치과",
                            date = "2025년 4월 10일",
                            time = "오후 5:30",
                            status = "방문 완료"
                        )
                    )
                }

                if (reservations.isEmpty()) {
                    // 예약 내역이 없는 경우
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "예약 내역이 없습니다.",
                                fontSize = 18.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = { /* 병원 검색 화면으로 이동 */ },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD0BCFF)
                                ),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "병원 검색하기",
                                    color = Color.Black
                                )
                            }
                        }
                    }
                } else {
                    // 예약 내역이 있는 경우
                    Text(
                        text = "총 ${reservations.size}건의 예약이 있습니다.",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // 예약 목록
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(reservations) { reservation ->
                            ReservationCard(reservation = reservation)
                        }
                    }
                }
            }
        }
    }
}

// 예약 데이터 클래스
data class Reservation(
    val id: String,
    val hospitalName: String,
    val doctorName: String,
    val department: String,
    val date: String,
    val time: String,
    val status: String
)

// 예약 카드 컴포넌트
@Composable
fun ReservationCard(
    reservation: Reservation
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // 예약 상태
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = reservation.department,
                    fontSize = 14.sp,
                    color = Color.Gray
                )

                // 상태 표시 (예약 확정, 방문 완료 등)
                Box(
                    modifier = Modifier
                        .background(
                            color = when (reservation.status) {
                                "예약 확정" -> Color(0xFFE1F5FE)
                                "방문 완료" -> Color(0xFFE8F5E9)
                                else -> Color(0xFFFCE4EC)
                            },
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = reservation.status,
                        fontSize = 12.sp,
                        color = when (reservation.status) {
                            "예약 확정" -> Color(0xFF0288D1)
                            "방문 완료" -> Color(0xFF388E3C)
                            else -> Color(0xFFD81B60)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 병원명
            Text(
                text = reservation.hospitalName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 예약 정보 표시
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "날짜",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${reservation.date} ${reservation.time}",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 의사
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "의사",
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "${reservation.doctorName} 의사",
                    fontSize = 14.sp,
                    color = Color.DarkGray
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 버튼 영역
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (reservation.status == "예약 확정") {
                    // 취소 버튼 (예약 확정 상태일 때만 표시)
                    OutlinedButton(
                        onClick = { /* 취소 기능 */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color.Gray
                        )
                    ) {
                        Text("예약 취소")
                    }

                    // 변경 버튼
                    Button(
                        onClick = { /* 변경 기능 */ },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD0BCFF)
                        )
                    ) {
                        Text(
                            text = "예약 변경",
                            color = Color.Black
                        )
                    }
                } else if (reservation.status == "방문 완료") {
                    // 리뷰 작성 버튼 (방문 완료일 때 표시)
                    Button(
                        onClick = { /* 리뷰 작성 기능 */ },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD0BCFF)
                        )
                    ) {
                        Text(
                            text = "리뷰 작성하기",
                            color = Color.Black
                        )
                    }
                }
            }
        }
    }
}