// Navigation.kt - Updated with hospital search
package com.example.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.compose.ui.screens.CommunityScreen
import com.example.compose.ui.screens.HomeScreen
import com.example.compose.ui.screens.MyDdocDocScreen
import com.example.compose.ui.screens.MyPageScreen
import com.example.compose.ui.screens.LoginPage
import com.example.compose.ui.screens.RegisterPage
import com.example.compose.ui.screens.WritePostScreen
import com.example.compose.ui.screens.ProfileManagementScreen
import com.example.compose.ui.screens.ReservationHistoryScreen
import com.example.compose.ui.screens.ChatBotScreen
import com.example.compose.ui.screens.HospitalSearchResultScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MyDdocDoc : Screen("mydocdoc")
    object Community : Screen("community")
    object MyPage : Screen("mypage")
    object Login : Screen("login")
    object Register : Screen("register")
    object WritePost : Screen("write_post")
    object ProfileManagement : Screen("profile_management")
    object ReservationHistory : Screen("reservation_history")
    object ChatBot : Screen("chatbot")
    object HospitalSearchResult : Screen("hospital_search_result/{query}") {
        fun createRoute(query: String) = "hospital_search_result/$query"
    }
}

@Composable
fun AppNavigation(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        // 기존 화면 유지
        composable(Screen.Home.route) {
            HomeScreen(
                navigateToScreen = { route ->
                    navController.navigate(route) {
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.MyDdocDoc.route) {
            MyDdocDocScreen(
                navigateToScreen = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Community.route) {
            CommunityScreen(
                navigateToScreen = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.MyPage.route) {
            MyPageScreen(
                navigateToScreen = { route ->
                    navController.navigate(route)
                }
            )
        }

        composable(Screen.Login.route) {
            LoginPage(
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterPage(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.WritePost.route) {
            WritePostScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onPostSuccess = {
                    // 글 작성 성공 시 커뮤니티 화면으로 돌아감
                    navController.navigate(Screen.Community.route) {
                        popUpTo(Screen.WritePost.route) { inclusive = true }
                    }
                }
            )
        }

        // 내 정보 관리 화면
        composable(Screen.ProfileManagement.route) {
            ProfileManagementScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 예약 내역 화면
        composable(Screen.ReservationHistory.route) {
            ReservationHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 챗봇 화면
        composable(Screen.ChatBot.route) {
            ChatBotScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // 병원 검색 결과 화면 추가
        composable(
            route = "hospital_search_result/{query}",
            arguments = listOf(
                navArgument("query") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            HospitalSearchResultScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                searchQuery = query
            )
        }
    }
}