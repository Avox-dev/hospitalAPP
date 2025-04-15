// MyPageScreen.kt
package com.example.compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.navigation.Screen
import com.example.compose.ui.components.*
import com.example.compose.ui.theme.*
import com.example.compose.viewmodel.HomeViewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.background


@Composable
fun MyPageScreen(
    navigateToScreen: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val apiResult by viewModel.apiResult.collectAsState()
    val dimens = appDimens()

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 앱바에 navigateToScreen 전달
        TopBar(navigateToScreen = navigateToScreen)

        // Quick Menu Icons
        QuickMenuSection()
        // Menu List in ScrollView
        MenuListSection(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        )


        // 하단 네비게이션
        BottomNavigation(
            currentRoute = Screen.Home.route,
            onNavigate = navigateToScreen
        )
    }
}
@Composable
fun TopBar(navigateToScreen: (String) -> Unit) {
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

        Button(
            onClick = {
                // 로그인 화면으로 이동
                navigateToScreen("login")
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD0BCFF))
        ) {
            Text(
                text = "로그인",
                fontSize = 14.sp,
                color = Color.Black
            )
        }

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
            .padding(horizontal = 16.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Family Management
        QuickMenuItem(
            text = "가족관리"
        )

        // Membership Management
        QuickMenuItem(
            text = "멤버십 관리"
        )

        // Events/Promos
        QuickMenuItem(
            text = "이벤트·투표"
        )

        // Customer Center
        QuickMenuItem(
            text = "고객센터"
        )
    }
}

@Composable
fun QuickMenuItem(text: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(60.dp),
            contentAlignment = Alignment.Center
        ) {
            // Placeholder for icon
            Box(
                modifier = Modifier
                    .size(40.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Black
        )
    }
}

@Composable
fun MenuListSection(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 24.dp)
    ) {
        // Child Development Section
        CategoryHeader(text = "자녀성장")

        MenuItem(text = "우리아이 키·몸무게")

        Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))

        MenuItemWithBadge(text = "영유아검진 관리", showBadge = true)

        Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))

        MenuItemWithBadge(text = "예방접종 관리", showBadge = true)

        // Section Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFF7F7F7))
        )

        // Diagnosis Section
        CategoryHeader(text = "진료")

        MenuItem(text = "진료내역")

        Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))

        MenuItem(text = "편한 병원")

        Divider(thickness = 1.dp, color = Color(0xFFEEEEEE))

        MenuItem(text = "접수·예약 캘린더")

        // Section Divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(Color(0xFFF7F7F7))
        )

        // Service and Terms Section
        CategoryHeader(text = "서류 및 결제")
    }
}

@Composable
fun CategoryHeader(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        color = Color(0xFF666666),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
fun MenuItem(text: String) {
    Text(
        text = text,
        fontSize = 16.sp,
        color = Color.Black,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to item */ }
            .padding(horizontal = 16.dp, vertical = 20.dp)
    )
}

@Composable
fun MenuItemWithBadge(text: String, showBadge: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* Navigate to item */ }
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            color = Color.Black
        )

        if (showBadge) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Red)
                    .padding(horizontal = 8.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "NEW",
                    fontSize = 12.sp,
                    color = Color.White
                )
            }
        }
    }
}
@Preview
@Composable
fun MyPageScreenPreview() {
    MyPageScreen(
        navigateToScreen = {})
}