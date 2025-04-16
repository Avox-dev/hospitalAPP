// MyDdocDocScreen.kt
package com.example.compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.navigation.Screen
import com.example.compose.ui.components.BottomNavigation
import com.example.compose.viewmodel.HomeViewModel

@Composable
fun MyDdocDocScreen(
    navigateToScreen: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 상단 영역 - "나의 똑닥" 텍스트만 표시
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "나의 똑닥",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 하단 네비게이션 - 최하단에 고정
        BottomNavigation(
            currentRoute = Screen.Home.route,
            onNavigate = navigateToScreen
        )
    }
}

@Preview
@Composable
fun MyDdocDocScreenPreview() {
    MyDdocDocScreen(
        navigateToScreen = {}
    )
}