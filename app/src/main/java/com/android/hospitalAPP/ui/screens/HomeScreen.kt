// HomeScreen.kt - Improved with Kakao Map API search
package com.android.hospitalAPP.ui.screens

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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.android.hospitalAPP.navigation.Screen
import com.android.hospitalAPP.ui.components.*
import com.android.hospitalAPP.ui.theme.*
import com.android.hospitalAPP.viewmodel.HomeViewModel
import androidx.compose.runtime.*
import com.google.accompanist.pager.*
import com.android.hospitalAPP.viewmodel.CommunityViewModel

@Composable
fun HomeScreen(
    navigateToScreen: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {


    val dimens = appDimens()

    // 현재 사용자 위치 (기본값: 서울시청)
    val currentLocation = remember { mutableStateOf("서울시청") }

    Column(modifier = Modifier.fillMaxSize()) {
        // 상단 앱바
        TopAppBar(location = currentLocation.value)

        // 검색창 - 개선된 버전 사용
        EnhancedSearchBar(
            onSearch = { query ->
                // 검색어를 이용해 병원 검색 결과 화면으로 이동
                navigateToScreen(Screen.HospitalSearchResult.createRoute(query))
            },
            modifier = Modifier.padding(horizontal = dimens.paddingLarge.dp)
        )

        val viewModel: CommunityViewModel = viewModel()
        val notices by viewModel.notices.collectAsState()
        val qnas by viewModel.posts.collectAsState()

        // 스크롤 영역
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(dimens.paddingLarge.dp)
        ) {
            // 공지 및 qna 배너
            Text(
                text = "공지사항",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            NoticeBanner(
                notices = notices,
                onItemClick = { bannerItem ->
                    // 공지사항 클릭 처리
                    navigateToScreen(Screen.NoticeDetail.createRoute(bannerItem.id))
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "QnA",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            QnaBanner(
                qnas = qnas,
                onItemClick = { bannerItem ->
                    // QnA 클릭 처리
                    navigateToScreen(Screen.PostDetail.createRoute(bannerItem.id))
                }
            )

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
                            // 인기 병원 검색 결과 화면으로 이동
                            navigateToScreen(Screen.HospitalSearchResult.createRoute("인기병원"))
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
                            // 문 연 병원 검색 결과 화면으로 이동
                            navigateToScreen(Screen.HospitalSearchResult.createRoute("문연병원"))
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(dimens.paddingLarge.dp))

            // 우리아이 키/몸무게 배너
            ChildGrowthBanner(navigateToScreen)

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
                            navigateToScreen(Screen.HospitalSearchResult.createRoute("소아청소년과"))
                        }
                    )
                }

                item {
                    DepartmentItem(
                        name = "이비인후과",
                        backgroundColor = EntDept,
                        onClick = {
                            navigateToScreen(Screen.HospitalSearchResult.createRoute("이비인후과"))
                        }
                    )
                }

                item {
                    DepartmentItem(
                        name = "가정의학과",
                        backgroundColor = FamilyMedicineDept,
                        onClick = {
                            navigateToScreen(Screen.HospitalSearchResult.createRoute("가정의학과"))
                        }
                    )
                }

                item {
                    DepartmentItem(
                        name = "산부인과",
                        backgroundColor = ObGynDept,
                        onClick = {
                            navigateToScreen(Screen.HospitalSearchResult.createRoute("산부인과"))
                        }
                    )
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
fun TopAppBar(location: String, navigateToScreen: (String) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // 위치 정보
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { /* 위치 선택 다이얼로그 표시 */ }
        ) {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "위치",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = location,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = "드롭다운",
                tint = Color.Black,
                modifier = Modifier.size(24.dp)
            )
        }

        // 오른쪽 아이콘들
        Row {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = "프로필",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {  }
            )

            Spacer(modifier = Modifier.width(16.dp))


            Icon(
                imageVector = Icons.Filled.Notifications,
                contentDescription = "알림",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {  }
            )


            Spacer(modifier = Modifier.width(16.dp))


            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "즐겨찾기",
                tint = Color.Black,
                modifier = Modifier
                    .size(24.dp)
                    .clickable {  }
            )

        }
    }
}


@OptIn(ExperimentalPagerApi::class)
@Composable
fun NoticeBanner(
    notices: List<CommunityViewModel.Notice>,
    onItemClick: (BannerItem) -> Unit
) {
    val dimens = appDimens()

    if (notices.isEmpty()) return

    val noticeItems = notices.take(5).map {
        BannerItem(
            id = it.id,
            title = it.title,
            comment = it.comment,
            type = BannerType.NOTICE
        )
    }

    val pagerState = rememberPagerState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.bannerHeight.dp)
            .clickable {
                val currentItem = noticeItems[pagerState.currentPage]
                onItemClick(currentItem)
            },
        shape = RoundedCornerShape(dimens.cornerRadius.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Purple80)
                .padding(dimens.paddingLarge.dp)
        ) {
            HorizontalPager(
                count = noticeItems.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val currentItem = noticeItems[page]

                Column(
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = currentItem.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(dimens.paddingMedium.dp))

                    Text(
                        text = currentItem.comment.take(30) + "...",
                        fontSize = 14.sp,
                        color = Color(0xEEFFFFFF)
                    )
                }
            }

            Text(
                text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color(0x80000000))
                    .padding(horizontal = dimens.paddingMedium.dp, vertical = 4.dp)
            )
        }
    }
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun QnaBanner(
    qnas: List<CommunityViewModel.Post>,
    onItemClick: (BannerItem) -> Unit
) {
    val dimens = appDimens()

    if (qnas.isEmpty()) return

    val qnaItems = qnas.take(5).map {
        BannerItem(
            id = it.id,
            title = it.title,
            comment = it.content,
            type = BannerType.QNA
        )
    }

    val pagerState = rememberPagerState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(dimens.bannerHeight.dp)
            .clickable {
                val currentItem = qnaItems[pagerState.currentPage]
                onItemClick(currentItem)
            },
        shape = RoundedCornerShape(dimens.cornerRadius.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PediatricsDept)
                .padding(dimens.paddingLarge.dp)
        ) {
            HorizontalPager(
                count = qnaItems.size,
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                val currentItem = qnaItems[page]

                Column(
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = currentItem.title,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(dimens.paddingMedium.dp))

                    Text(
                        text = currentItem.comment.take(30) + "...",
                        fontSize = 14.sp,
                        color = Color(0xEEFFFFFF)
                    )
                }
            }

            Text(
                text = "${pagerState.currentPage + 1}/${pagerState.pageCount}",
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .background(Color(0x80000000))
                    .padding(horizontal = dimens.paddingMedium.dp, vertical = 4.dp)
            )
        }
    }
}
data class BannerItem(
    val id: Int,
    val title: String,
    val comment: String,
    val type: BannerType // 🔥 추가
)

enum class BannerType {
    NOTICE, QNA
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
fun ChildGrowthBanner(navigateToScreen: (String) -> Unit) {
    val dimens = appDimens()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { navigateToScreen(Screen.HealthInfoInput.route) },
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
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
            ) {
                // NEW 배지
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
                    text = "환자 기본 정보 입력",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "혈액형과 키, 몸무게를 입력해보세요!!",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
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


