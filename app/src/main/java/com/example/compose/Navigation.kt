// Navigation.kt - App navigation handling
package com.example.compose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.compose.ui.screens.CommunityScreen
import com.example.compose.ui.screens.HomeScreen
import com.example.compose.ui.screens.MyDdocDocScreen
import com.example.compose.ui.screens.MyPageScreen
import com.example.compose.ui.screens.LoginPage

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object MyDdocDoc : Screen("mydocdoc")
    object Community : Screen("community")
    object MyPage : Screen("mypage")
    object Login : Screen("login")  // 로그인 화면 추가
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
        composable(Screen.Home.route) {
            HomeScreen(
                navigateToScreen = { route ->
                    navController.navigate(route) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        popUpTo(Screen.Home.route) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
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
                    // 로그인 성공 시 홈 화면으로 이동
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    // 회원가입 화면으로 이동 (추후 구현)
                    // navController.navigate("register")
                }
            )
        }
    }
}