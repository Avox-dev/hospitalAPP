package com.example.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.compose.ui.theme.TextSecondary


@Composable
fun LoginPage (){
    Text(text = "Login Page", color = TextSecondary)
}

@Preview
@Composable
fun LoginPagePreview() {
    LoginPage()
}