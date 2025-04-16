// Components.kt - Reusable UI components
package com.example.compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.navigation.Screen
import com.example.compose.ui.theme.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.data.User

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
                text = "주안동(미구현)",
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
fun SearchBar() {
    val dimens = appDimens()

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
                painter = painterResource(id = android.R.drawable.ic_menu_search),
                contentDescription = "Search",
                tint = TextSecondary,
                modifier = Modifier.size(dimens.iconSize.dp)
            )

            Spacer(modifier = Modifier.width(dimens.paddingMedium.dp))

            Text(
                text = "질병, 진료과, 병원을 검색해보세요.",
                fontSize = 14.sp,
                color = TextSecondary
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
                    text = "의사쌤이 알려드려요(미구현)",
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

@Preview
@Composable
fun PneumoniaBannerPreview() {
    PneumoniaBanner()
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

@Preview
@Composable
fun CategoryButtonPreview() {
    CategoryButton(text = "내과", backgroundColor = Color.Blue) {
        // do nothing
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
                Text(
                    text = "NEW",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PurpleGrey40,
                    modifier = Modifier
                        .background(BadgeBackground)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "우리 아이 키/몸무게(미구현)",
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

@Preview
@Composable
fun ChildGrowthBannerPreview() {
    ChildGrowthBanner()
}

@Composable
fun DepartmentItem(name: String, backgroundColor: Color) {
    val dimens = appDimens()

    Column(
        modifier = Modifier.width(dimens.departmentItemWidth.dp),
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

@Preview
@Composable
fun DepartmentItemPreview() {
    DepartmentItem(name = "내과", backgroundColor = Color.LightGray)
}

@Composable
fun BottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val dimens = appDimens()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = dimens.paddingMedium.dp),
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        BottomNavigationItem(
            text = "홈",
            iconRes = android.R.drawable.ic_menu_compass,
            isSelected = currentRoute == Screen.Home.route,
            onClick = { onNavigate(Screen.Home.route) }
        )

        BottomNavigationItem(
            text = "닥터 퓨처(AI)",
            iconRes = android.R.drawable.ic_menu_myplaces,
            isSelected = currentRoute == Screen.MyDdocDoc.route,
            onClick = { onNavigate(Screen.MyDdocDoc.route) }
        )

        BottomNavigationItem(
            text = "커뮤니티",
            iconRes = android.R.drawable.ic_menu_share,
            isSelected = currentRoute == Screen.Community.route,
            onClick = { onNavigate(Screen.Community.route) }
        )

        BottomNavigationItem(
            text = "마이페이지",
            iconRes = android.R.drawable.ic_menu_my_calendar,
            isSelected = currentRoute == Screen.MyPage.route,
            onClick = { onNavigate(Screen.MyPage.route) }
        )
    }
}

@Preview
@Composable
fun BottomNavigationPreview() {
    BottomNavigation(currentRoute = Screen.Home.route, onNavigate = {})
}

@Composable
fun BottomNavigationItem(
    text: String,
    iconRes: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val dimens = appDimens()

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(dimens.paddingSmall.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = if (isSelected) Color.Black else TextSecondary,
            modifier = Modifier.size(dimens.iconSize.dp)
        )

        Text(
            text = text,
            fontSize = 12.sp,
            color = if (isSelected) Color.Black else TextSecondary
        )
    }
}

// 마이페이지 컴포넌트 수정
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
        if (currentUser == null) {
            // 로그인 되지 않은 경우 로그인 버튼 표시
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
        } else {
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
                        text = currentUser.userId.first().toString().uppercase(),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // 사용자 ID
                Text(
                    text = currentUser.userId,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            }
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
                Text("로그인", fontSize = 16.sp, color = Color.Black)
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
                        text = "${currentUser.userId}님, 환영합니다",
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