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

@Composable
fun MyPageScreen(
    navigateToScreen: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val apiResult by viewModel.apiResult.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 앱바에 navigateToScreen 전달
        MyPageTopBar(navigateToScreen = navigateToScreen)

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
            currentRoute = Screen.MyPage.route,
            onNavigate = navigateToScreen
        )
    }
}