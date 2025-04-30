package com.android.hospitalAPP.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.sp
import com.android.hospitalAPP.data.UserRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthInfoInputScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val userRepository = UserRepository.getInstance()
    val currentUser by userRepository.currentUser.collectAsState()
    val dimens = 16.dp
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("환자 기본 정보") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (currentUser == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("로그인이 필요한 서비스입니다.", fontSize = 18.sp)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(dimens)
                    .verticalScroll(rememberScrollState())
            ) {
                // 첫 번째 줄: 혈액형 + 알레르기
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        BloodTypeDropdown()
                    }
                    Box(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        OutlinedTextField(
                            value = "",
                            onValueChange = {},
                            label = { Text("알레르기 정보") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(dimens))

                // 두 번째 줄: 키 + 몸무게
                Row(modifier = Modifier.fillMaxWidth()) {
                    Box(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text("키") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("cm")
                        }
                    }

                    Box(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = "",
                                onValueChange = {},
                                label = { Text("몸무게") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("kg")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(dimens))

                // 과거 질병 이력 (멀티라인, 높이 증가)
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("과거 질병 이력") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // ✅ 기존보다 두 배 정도 높게
                    maxLines = 5,
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(dimens))

                // 만성 질환 (멀티라인, 높이 증가)
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    label = { Text("만성 질환") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp), // ✅ 동일한 높이 유지
                    maxLines = 5,
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}


/*
    혈액형 드롭다운 박스
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BloodTypeDropdown() {
    val bloodTypes = listOf("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-")
    var expanded by remember { mutableStateOf(false) }
    var selectedBloodType by remember { mutableStateOf("") }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField( // ← TextField → OutlinedTextField 로 변경!
            readOnly = true,
            value = selectedBloodType.ifEmpty { "선택해주세요" },
            onValueChange = {},
            label = { Text("혈액형") },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.outline
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true // ✅ 한 줄로 제한 (알레르기 정보와 맞추기)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            bloodTypes.forEach { type ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = type,
                            color = if (selectedBloodType == type)
                                MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = {
                        selectedBloodType = type
                        expanded = false
                    }
                )
            }
        }
    }
}
