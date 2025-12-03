package com.example.smalldy.ui.Pages.MsgPage


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MsgPage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 顶部 AppBar
        TopAppBar(
            title = { Text("消息") },
            navigationIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = null,
                    modifier = Modifier.padding(start = 16.dp)
                )
            },
            actions = {
                IconButton(onClick = { /* 搜索 */ }) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
                IconButton(onClick = { /* 新建 */ }) {
                    Icon(Icons.Default.Add, contentDescription = null)
                }
            }
        )

        // 顶部提示条
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF6E7))
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = Color(0xFFFF9800),
                modifier = Modifier.size(18.dp)
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text = "当前无法接收好友的消息提醒",
                color = Color(0xFF8B5A2B),
                fontSize = 13.sp
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "打开提醒",
                color = Color(0xFF007AFF),
                fontSize = 13.sp
            )
        }

        // 顶部三个入口：发日常 / 某联系人 / 状态设置
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MsgTopShortcut("发日常")
            MsgTopShortcut("某联系人")
            MsgTopShortcut("状态设置")
        }

        Divider()

        // 系统消息分组
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            MsgSystemItem(title = "新关注我的", subtitle = "没有新通知")
            MsgSystemItem(title = "互动消息", subtitle = "没有新通知")
        }

        Divider(thickness = 8.dp, color = Color(0xFFF5F5F7))

        // 最近聊天
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            MsgChatItem(
                name = "（彭绮雯） conflict 🐬🐬🐬",
                subtitle = "昨天在线",
                isOnline = false
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = "暂时没有更多了",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                color = Color.Gray,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun MsgTopShortcut(label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(Color(0xFFF4F5F7)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.Gray,
                modifier = Modifier.size(28.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        Text(text = label, color = Color.Black, fontSize = 13.sp)
    }
}

@Composable
private fun MsgSystemItem(
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(Color(0xFF4F8DFF)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, color = Color.Black, fontSize = 15.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
        }
    }
}

@Composable
private fun MsgChatItem(
    name: String,
    subtitle: String,
    isOnline: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(Color(0xFFEEF1FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1),
                color = Color(0xFF4F5FFF),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = name, color = Color.Black, fontSize = 15.sp)
            Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
        }

        if (isOnline) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color(0xFFFFC107),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}