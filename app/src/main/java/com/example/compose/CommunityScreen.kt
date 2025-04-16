// CommunityScreen.kt
package com.example.compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.navigation.Screen
import com.example.compose.ui.components.BottomNavigation
import com.example.compose.viewmodel.CommunityViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navigateToScreen: (String) -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    val posts by viewModel.posts.collectAsState()
    val notices by viewModel.notices.collectAsState()

    // 현재 선택된 탭 (QnA 또는 공지사항)
    var selectedTab by remember { mutableStateOf(0) }

    // 카테고리 필터링 (QnA 탭에서만 사용)
    val categories = listOf("전체", "질문", "자유")
    var selectedCategory by remember { mutableStateOf("전체") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("커뮤니티", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        floatingActionButton = {
            // QnA 탭에서만 글쓰기 버튼 표시
            if (selectedTab == 0) {
                FloatingActionButton(
                    onClick = { navigateToScreen(Screen.WritePost.route) },
                    containerColor = Color(0xFFD0BCFF),
                    contentColor = Color.White,
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "글쓰기"
                    )
                }
            }
        },
        bottomBar = {
            BottomNavigation(
                currentRoute = Screen.Community.route,
                onNavigate = navigateToScreen
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // QnA / 공지사항 탭
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFFD0BCFF)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Q&A") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("공지사항") }
                )
            }

            when (selectedTab) {
                0 -> {
                    // QnA 화면
                    Column {
                        // 카테고리 탭
                        ScrollableTabRow(
                            selectedTabIndex = categories.indexOf(selectedCategory),
                            modifier = Modifier.fillMaxWidth(),
                            containerColor = Color.White,
                            contentColor = Color(0xFFD0BCFF),
                            edgePadding = 16.dp,
                            indicator = { tabPositions -> Box {} },  // 기본 인디케이터 없애기
                            divider = {}  // 기본 구분선 없애기
                        ) {
                            categories.forEach { category ->
                                Tab(
                                    selected = selectedCategory == category,
                                    onClick = { selectedCategory = category },
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(20.dp))
                                            .background(
                                                if (selectedCategory == category)
                                                    Color(0xFFD0BCFF) else Color(0xFFF5F5F5)
                                            )
                                            .padding(horizontal = 16.dp, vertical = 8.dp)
                                    ) {
                                        Text(
                                            text = category,
                                            color = if (selectedCategory == category)
                                                Color.White else Color.Gray
                                        )
                                    }
                                }
                            }
                        }

                        // 게시글 목록
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp)
                        ) {
                            items(posts.filter {
                                selectedCategory == "전체" || it.category == selectedCategory
                            }) { post ->
                                PostItem(
                                    post = post,
                                    onClick = { /* 게시글 상세 화면으로 이동 */ }
                                )
                                Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                            }
                        }
                    }
                }
                1 -> {
                    // 공지사항 화면
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp)
                    ) {
                        items(notices) { notice ->
                            NoticeItem(
                                notice = notice,
                                onClick = { /* 공지사항 상세 화면으로 이동 */ }
                            )
                            Divider(color = Color(0xFFEEEEEE), thickness = 1.dp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostItem(
    post: CommunityViewModel.Post,
    onClick: () -> Unit
) {
    // 기존 PostItem 코드 유지
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        // 카테고리 및 시간
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF0F0F0))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = post.category,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Text(
                text = post.timeAgo,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 제목
        Text(
            text = post.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 내용 미리보기
        Text(
            text = post.content,
            fontSize = 14.sp,
            color = Color.DarkGray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 작성자 정보 및 반응
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 프로필 아이콘
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFEEEEEE))
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "프로필",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // 작성자 이름
            Text(
                text = post.author,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            // 좋아요 수
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ThumbUp,
                    contentDescription = "좋아요",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = post.likes.toString(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 댓글 수
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "댓글",
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )

                Spacer(modifier = Modifier.width(4.dp))

                Text(
                    text = post.comments.toString(),
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
        }
    }
}

@Composable
fun NoticeItem(
    notice: CommunityViewModel.Notice,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp)
    ) {
        // 중요 표시 및 시간
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (notice.isImportant) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFFFEBEE))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "중요",
                        fontSize = 12.sp,
                        color = Color(0xFFE57373)
                    )
                }
            }

            Text(
                text = notice.date,
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 제목
        Text(
            text = notice.title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(6.dp))

        // 내용 미리보기
        Text(
            text = notice.content,
            fontSize = 14.sp,
            color = Color.DarkGray,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 작성자 정보
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "관리자",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}