// MyPageScreen.kt
package com.example.compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.navigation.Screen
import com.example.compose.ui.components.*
import com.example.compose.viewmodel.HomeViewModel
import com.example.compose.data.UserRepository

@Composable
fun MyPageScreen(
    navigateToScreen: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val apiResult by viewModel.apiResult.collectAsState()

    // UserRepository의 로그인 상태 감시
    val userRepository = UserRepository.getInstance()
    val currentUser by userRepository.currentUser.collectAsState()

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
            onLoginClick = { navigateToScreen(Screen.Login.route) },
            onLogoutClick = {
                userRepository.logoutUser()
                // 필요한 경우 추가 로그아웃 처리
            }
        )

        // 하단 네비게이션
        BottomNavigation(
            currentRoute = Screen.MyPage.route,
            onNavigate = navigateToScreen
        )
    }
}