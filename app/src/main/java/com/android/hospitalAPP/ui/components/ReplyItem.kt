// ReplyItem.kt
package com.android.hospitalAPP.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.android.hospitalAPP.data.model.Comment

@Composable
fun ReplyItem(reply: Comment) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 4.dp, bottom = 4.dp)
    ) {
        Text(
            text = "${reply.username} • ${reply.createdAt}",
            fontSize = 11.sp,
            color = Color.Gray
        )
        Text(
            text = reply.comment,
            fontSize = 13.sp
        )
    }
}
