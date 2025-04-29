// MyPageScreen.kt
package com.example.compose.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.ViewModelProvider
import com.example.compose.data.User
import com.example.compose.navigation.Screen
import com.example.compose.ui.components.*
import com.example.compose.viewmodel.HomeViewModel
import com.example.compose.data.UserRepository

private const val TAG = "MyPageScreen"

@Composable
fun MyPageScreen(
    navigateToScreen: (String) -> Unit,
    viewModel: HomeViewModel = viewModel(factory = ViewModelProvider.AndroidViewModelFactory.getInstance(
        LocalContext.current.applicationContext as android.app.Application
    ))
) {
    Log.d(TAG, "MyPageScreen 컴포넌트 렌더링")

    // UserRepository의 로그인 상태 감시
    val userRepository = UserRepository.getInstance()
    val currentUser by userRepository.currentUser.collectAsState()

    // 자동 로그인 정보 존재 여부 확인
    val hasAutoLoginInfo by viewModel.hasAutoLoginInfo.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 앱바에 사용자 정보 전달
        MyPageTopBar(
            navigateToScreen = navigateToScreen,
            currentUser = currentUser
        )

        // Quick Menu Icons
        QuickMenuSection()

        // Menu List in ScrollView
        MenuListSection(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            currentUser = currentUser,
            hasAutoLoginInfo = hasAutoLoginInfo,
            onLoginClick = {
                // 자동 로그인 정보가 있으면 바로 로그인 실행
                if (hasAutoLoginInfo) {
                    Log.d(TAG, "자동 로그인 정보가 있어 로그인 함수 실행")
                    viewModel.executeAutoLogin()
                } else {
                    // 자동 로그인 정보가 없으면 로그인 화면으로 이동
                    Log.d(TAG, "자동 로그인 정보가 없어 로그인 화면으로 이동")
                    navigateToScreen(Screen.Login.route)
                }
            },
            onLogoutClick = {
                Log.d(TAG, "로그아웃 버튼 클릭")
                viewModel.logout()
            },
            navigateToScreen = navigateToScreen
        )

        // 하단 네비게이션
        BottomNavigation(
            currentRoute = Screen.MyPage.route,
            onNavigate = navigateToScreen
        )
    }
}

// 마이페이지 컴포넌트 수정 - 로그인 버튼 제거
@Composable
fun MyPageTopBar(
    navigateToScreen: (String) -> Unit,
    currentUser: User? = null // 로그인된 사용자 정보
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "마이페이지",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        // 로그인 상태에 따라 다른 UI 표시
        if (currentUser != null) {
            // 로그인된 경우 사용자 ID 표시
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 사용자 프로필 아이콘 (원형)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFD0BCFF)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentUser.userName.first().toString().uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 사용자 ID
                Text(
                    text = currentUser.userName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
        }
        // 비로그인 상태에서는 로그인 버튼을 표시하지 않음

        // 나머지 코드는 그대로 유지
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.size(24.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Box(modifier = Modifier.size(24.dp))
    }
}

@Composable
fun QuickMenuSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 가족관리
        QuickMenuItem(
            text = "가족관리",
            iconRes = android.R.drawable.ic_menu_my_calendar,
            onClick = { /* 가족관리 화면으로 이동 */ }
        )

        // 멤버십 관리
        QuickMenuItem(
            text = "멤버십 관리",
            iconRes = android.R.drawable.ic_menu_today,
            onClick = { /* 멤버십 관리 화면으로 이동 */ }
        )

        // 이벤트·투표
        QuickMenuItem(
            text = "이벤트·투표",
            iconRes = android.R.drawable.ic_menu_gallery,
            onClick = { /* 이벤트·투표 화면으로 이동 */ }
        )

        // 고객센터
        QuickMenuItem(
            text = "고객센터",
            iconRes = android.R.drawable.ic_menu_help,
            onClick = { /* 고객센터 화면으로 이동 */ }
        )
    }
}

@Composable
fun QuickMenuItem(
    text: String,
    iconRes: Int,
    onClick: () -> Unit = {}
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(horizontal = 4.dp)
            .clickable(onClick = onClick)
    ) {
        // 아이콘 박스
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = text,
                tint = Color(0xFFD0BCFF),
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 텍스트
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.width(70.dp)
        )

        // 아직 구현되지 않은 기능임을 나타내는 배지
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .background(
                    color = Color(0xFFE0E0E0),
                    shape = RoundedCornerShape(4.dp)
                )
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(
                text = "미구현",
                fontSize = 8.sp,
                color = Color.Gray
            )
        }
    }
}

// MenuListSection 컴포넌트 수정
@Composable
fun MenuListSection(
    modifier: Modifier = Modifier,
    currentUser: User? = null,
    hasAutoLoginInfo: Boolean = false,
    onLoginClick: () -> Unit = {},
    onLogoutClick: () -> Unit = {},
    navigateToScreen: (String) -> Unit = {}
) {
    Column(modifier = modifier.padding(16.dp)) {
        // 로그인 상태에 따라 다른 UI 표시
        if (currentUser == null) {
            // 로그인 버튼 표시
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD0BCFF)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                // 자동 로그인 정보 유무에 따라 버튼 텍스트 변경
                Text(
                    text = if (hasAutoLoginInfo) "자동 로그인" else "로그인",
                    fontSize = 16.sp,
                    color = Color.Black
                )
            }
        } else {
            // 사용자 정보 표시
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "${currentUser.userName}님, 환영합니다",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // 로그아웃 버튼
                    TextButton(
                        onClick = onLogoutClick,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "로그아웃",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 마이페이지 메뉴 항목들...
        // 내 정보 관리 클릭 시 내 정보 관리 화면으로 이동
        MenuItemCard(
            title = "내 정보 관리",
            subtitle = "개인정보 수정, 비밀번호 변경",
            onClick = { navigateToScreen(Screen.ProfileManagement.route) }
        )

        // 예약 내역 클릭 시 예약 내역 화면으로 이동하도록 수정
        MenuItemCard(
            title = "예약 내역",
            subtitle = "나의 병원 예약 조회 및 관리",
            onClick = { navigateToScreen(Screen.ReservationHistory.route) }
        )

        MenuItemCard(
            title = "알림 설정(미구현)",
            subtitle = "앱 알림 설정 및 관리"
        )

        MenuItemCard(
            title = "고객 센터(미구현)",
            subtitle = "문의하기, 공지사항, FAQ"
        )

        TextButton(
            onClick = { navigateToScreen(Screen.WithdrawAccount.route) },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "회원탈퇴",
                fontSize = 14.sp,
                color = Color(0xFFD0BCFF)
            )
        }
    }
}

// 메뉴 아이템 카드 컴포넌트
@Composable
fun MenuItemCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun MyPageTopBarPreview() {
    MyPageTopBar(navigateToScreen = {})
}

@Preview
@Composable
fun QuickMenuSectionPreview() {
    QuickMenuSection()
}

@Preview
@Composable
fun MenuListSectionPreview() {
    MenuListSection()
}