// HomeScreen.kt - Modified with search functionality
package com.example.compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.navigation.Screen
import com.example.compose.ui.components.*
import com.example.compose.ui.theme.*
import com.example.compose.viewmodel.HomeViewModel

// Required imports for the SearchBar function
import androidx.compose.material3.TextField
import androidx.compose.material3.Text
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.text.input.ImeAction

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

        // 검색창 - 수정된 부분
        SearchBar(onSearch = { query ->
            // 검색어가 "병원"을 포함하면 병원 검색 결과 화면으로 이동
            if (query.contains("병원")) {
                navigateToScreen("hospital_search_result/$query")
            }
        })

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
                        text = "동네 인기 병원",
                        backgroundColor = PopularHospital,
                        onClick = {
                            // 병원 검색 결과 화면으로 이동
                            navigateToScreen("hospital_search_result/인기병원")
                        }
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 6.dp)
                ) {
                    CategoryButton(
                        text = "지금 문연 병원",
                        backgroundColor = OpenHospital,
                        onClick = {
                            // 병원 검색 결과 화면으로 이동
                            navigateToScreen("hospital_search_result/문연병원")
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimens.paddingLarge.dp))

            // 우리아이 키/몸무게 배너
            ChildGrowthBanner()

            Spacer(modifier = Modifier.height(dimens.paddingLarge.dp))

            // 진료과로 병원 찾기
            Text(
                text = "진료과로 병원 찾기",
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
                        backgroundColor = PediatricsDept,
                        onClick = {
                            // 병원 검색 결과 화면으로 이동
                            navigateToScreen("hospital_search_result/소아청소년과")
                        }
                    )
                }

                item {
                    DepartmentItem(
                        name = "이비인후과",
                        backgroundColor = EntDept,
                        onClick = {
                            navigateToScreen("hospital_search_result/이비인후과")
                        }
                    )
                }

                item {
                    DepartmentItem(
                        name = "가정의학과",
                        backgroundColor = FamilyMedicineDept,
                        onClick = {
                            navigateToScreen("hospital_search_result/가정의학과")
                        }
                    )
                }

                item {
                    DepartmentItem(
                        name = "산부인과",
                        backgroundColor = ObGynDept,
                        onClick = {
                            navigateToScreen("hospital_search_result/산부인과")
                        }
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

@Composable
fun TopAppBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 위치 정보
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Location",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = "주안동",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "Dropdown",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        // 오른쪽 아이콘들
        Row {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "Profile",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable { }
                    .padding(end = 16.dp)
            )

            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "Notifications",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 16.dp)
            )

            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Star",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun SearchBar(onSearch: (String) -> Unit = {}) {
    val dimens = appDimens()
    var searchText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .padding(horizontal = dimens.paddingLarge.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = SearchBarBackground),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = dimens.paddingLarge.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = TextSecondary,
                modifier = Modifier.size(dimens.iconSize.dp)
            )

            Spacer(modifier = Modifier.width(dimens.paddingMedium.dp))

            // 표준 TextField 사용
            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                placeholder = {
                    Text(
                        text = "질병, 진료과, 병원을 검색해보세요.",
                        fontSize = 14.sp,
                        color = TextSecondary
                    )
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                singleLine = true,
                maxLines = 1,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        if (searchText.isNotEmpty()) {
                            onSearch(searchText)
                            searchText = ""  // 검색 후 입력 필드 초기화
                        }
                    }
                )
            )
        }
    }
}

@Composable
fun PneumoniaBanner() {
    val dimens = appDimens()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.bannerHeight.dp),
        shape = RoundedCornerShape(dimens.cornerRadius.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple80)
                .padding(dimens.paddingLarge.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Text(
                    text = "폐렴은 꼭 입원",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "해야 하는 걸까요?",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(dimens.paddingMedium.dp))

                Text(
                    text = "의사쌤이 알려드려요",
                    fontSize = 14.sp,
                    color = Color(0xEEFFFFFF)
                )
            }

            Text(
                text = "1/5",
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color(0x80000000))
                    .padding(horizontal = dimens.paddingMedium.dp, vertical = 4.dp)
            )

            Row(
                modifier = Modifier.align(Alignment.BottomEnd)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Pink40)
                )

                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .offset(x = (-15).dp, y = 10.dp)
                        .clip(CircleShape)
                        .background(Purple40)
                )
            }
        }
    }
}

@Composable
fun CategoryButton(
    text: String,
    backgroundColor: Color,
    onClick: () -> Unit
) {
    val dimens = appDimens()

    Card(
        modifier = Modifier
            .height(dimens.buttonHeight.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(dimens.buttonCornerRadius.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimens.paddingMedium.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(dimens.iconSize.dp)
                    .clip(CircleShape)
                    .background(backgroundColor)
            )

            Spacer(modifier = Modifier.width(dimens.paddingMedium.dp))

            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
fun ChildGrowthBanner() {
    val dimens = appDimens()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.growthBannerHeight.dp),
        shape = RoundedCornerShape(dimens.cornerRadius.dp),
        colors = CardDefaults.cardColors(containerColor = BannerBackground),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(dimens.paddingLarge.dp)
        ) {
            Column(
                modifier = Modifier.align(Alignment.CenterStart)
            ) {
                // NEW 배지 - Text를 Box 안에 넣어 배경을 적용
                Box(
                    modifier = Modifier
                        .background(BadgeBackground)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "NEW",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "우리 아이 키/몸무게",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "또래 중 몇 등인지 확인해보세요!",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }

            // 이미지가 들어갈 자리
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
fun DepartmentItem(
    name: String,
    backgroundColor: Color,
    onClick: () -> Unit = {}
) {
    val dimens = appDimens()

    Column(
        modifier = Modifier
            .width(dimens.departmentItemWidth.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.size(dimens.departmentIconSize.dp),
            shape = RoundedCornerShape(dimens.buttonCornerRadius.dp),
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            elevation = CardDefaults.cardElevation(0.dp)
        ) { }

        Spacer(modifier = Modifier.height(dimens.paddingMedium.dp))

        Text(
            text = name,
            fontSize = 12.sp,
            color = Color.Black,
            textAlign = TextAlign.Center
        )
    }
}