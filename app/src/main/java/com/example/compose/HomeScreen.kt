// HomeScreen.kt - Main home screen
package com.example.compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.navigation.Screen
import com.example.compose.ui.components.*
import com.example.compose.ui.theme.*
import com.example.compose.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    navigateToScreen: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    val apiResult by viewModel.apiResult.collectAsState()
    val dimens = appDimens()

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 앱바
        TopAppBar()

        // 검색창
        SearchBar()

        // 스크롤 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(dimens.paddingLarge.dp)
        ) {
            // 폐렴 배너
            PneumoniaBanner()

            Spacer(modifier = Modifier.height(dimens.paddingLarge.dp))

            // 동네인기병원, 지금문연병원 버튼
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 6.dp)
                ) {
                    CategoryButton(
                        text = "동네 인기 병원(미구현)",
                        backgroundColor = PopularHospital,
                        onClick = {}
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                ) {
                    CategoryButton(
                        text = "지금 문연 병원(미구현)",
                        backgroundColor = OpenHospital,
                        onClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimens.paddingLarge.dp))

            // 우리아이 키/몸무게 배너
            ChildGrowthBanner()

            Spacer(modifier = Modifier.height(dimens.paddingLarge.dp))

            // 진료과로 병원 찾기
            Text(
                text = "진료과로 병원 찾기(미구현)",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 진료과 아이콘들
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    DepartmentItem(
                        name = "소아청소년과",
                        backgroundColor = PediatricsDept
                    )
                }

                item {
                    DepartmentItem(
                        name = "이비인후과",
                        backgroundColor = EntDept
                    )
                }

                item {
                    DepartmentItem(
                        name = "가정의학과",
                        backgroundColor = FamilyMedicineDept
                    )
                }

                item {
                    DepartmentItem(
                        name = "산부인과",
                        backgroundColor = ObGynDept
                    )
                }
            }

            // API 요청 영역
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(dimens.paddingLarge.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = apiResult,
                    fontSize = 18.sp,
                    modifier = Modifier.padding(dimens.paddingMedium.dp)
                )

                Button(
                    onClick = { viewModel.fetchApiData() }
                ) {
                    Text(text = "API 요청")
                }
            }
        }

        // 하단 네비게이션
        BottomNavigation(
            currentRoute = Screen.Home.route,
            onNavigate = navigateToScreen
        )
    }
}