// ProfileManagementScreen.kt
package com.example.compose.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.data.UserRepository
import com.example.compose.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileManagementScreen(
    onNavigateBack: () -> Unit
) {
    // UserRepository에서 현재 로그인한 사용자 정보 가져오기
    val userRepository = UserRepository.getInstance()
    val currentUser by userRepository.currentUser.collectAsState()

    // 폼 상태 관리
    var userId by remember { mutableStateOf(currentUser?.userName ?: "") }
    var userName by remember { mutableStateOf(currentUser?.userName ?: "사용자") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // 수정 완료 후 상태 관리
    var isEditSuccess by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(isEditSuccess) {
        if (isEditSuccess) {
            snackbarHostState.showSnackbar("회원정보가 수정되었습니다.")
            isEditSuccess = false
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("내 정보 관리") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        if (currentUser == null) {
            // 로그인되지 않은 경우
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "로그인이 필요한 서비스입니다.",
                    fontSize = 18.sp
                )
            }
        } else {
            // 로그인된 경우, 프로필 정보 표시 및 수정 폼
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 프로필 헤더
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 프로필 아이콘 (원형)
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.userName?.first().toString().uppercase(),
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF6650a4)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = currentUser?.userName ?: "",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Divider()
                Spacer(modifier = Modifier.height(24.dp))

                // 아이디 필드 (읽기 전용)
                OutlinedTextField(
                    value = userId,
                    onValueChange = { },
                    label = { Text("아이디") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    readOnly = true,
                    enabled = false
                )

                // 이름 필드
                OutlinedTextField(
                    value = userName,
                    onValueChange = { userName = it },
                    label = { Text("이름") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp)
                )

                // 이메일 필드
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("이메일") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                // 전화번호 필드
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("전화번호") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    shape = RoundedCornerShape(8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 정보 수정 버튼
                Button(
                    onClick = {
                        // 사용자 정보 업데이트
                        userRepository.setCurrentUser(
                            User(userId = userId, userName = userName)
                        )
                        isEditSuccess = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD0BCFF)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "정보 수정",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 비밀번호 변경 버튼
                OutlinedButton(
                    onClick = { /* 비밀번호 변경 화면으로 이동 */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFF6650a4)
                    )
                ) {
                    Text(
                        text = "비밀번호 변경",
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}