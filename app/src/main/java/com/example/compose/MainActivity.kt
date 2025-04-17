// MainActivity.kt - Entry point with navigation setup
package com.example.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.compose.rememberNavController
import com.example.compose.navigation.AppNavigation
import com.example.compose.ui.theme.HospitalAppTheme
import com.kakao.sdk.common.util.Utility
import com.kakao.sdk.common.KakaoSdk
import com.kakao.vectormap.KakaoMapSdk

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var keyHash = Utility.getKeyHash(this)
        Log.d("KeyHash", "KeyHash: $keyHash")
        KakaoMapSdk.init(this,"bf105d2a0b3861e39aff0e8f49f7f0ce")
        setContent {
            HospitalAppTheme {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    val navController = rememberNavController()
                    AppNavigation(navController = navController)
                }
            }
        }

    }
}