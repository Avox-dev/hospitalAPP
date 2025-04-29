// WritePostScreen.kt
package com.example.compose.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.compose.viewmodel.CommunityViewModel
import com.example.compose.data.UserRepository // ← 로그인 상태 확인을 위해 추가

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritePostScreen(
    onNavigateBack: () -> Unit,
    onPostSuccess: () -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    val userRepository = UserRepository.getInstance()
    val currentUser by userRepository.currentUser.collectAsState()

    if (currentUser == null) {
        // ✅ 로그인되지 않은 경우
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("글쓰기", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "뒤로가기"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.White
                    )
                )
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("로그인이 필요한 서비스입니다.")
            }
        }
        return
    }

    // ✅ 로그인된 경우
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    // 카테고리 항상 "일반"으로 고정
    val defaultCategory = "일반"

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("글쓰기", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.addPost(title, content, defaultCategory)
                            onPostSuccess()
                        },
                        enabled = title.isNotBlank() && content.isNotBlank()
                    ) {
                        Text(
                            text = "등록",
                            color = if (title.isNotBlank() && content.isNotBlank())
                                Color(0xFFD0BCFF) else Color.Gray
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 카테고리 선택 부분 제거됨

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("제목") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD0BCFF),
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = content,
                onValueChange = { content = it },
                label = { Text("내용을 입력하세요") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp), // 높이 증가
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFD0BCFF),
                    unfocusedBorderColor = Color.LightGray
                )
            )
        }
    }
}