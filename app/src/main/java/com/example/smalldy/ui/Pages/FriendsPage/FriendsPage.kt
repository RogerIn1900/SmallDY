package com.example.smalldy.ui.Pages.FriendsPage

import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun FriendsPage(
    modifier: Modifier = Modifier
) {
    val bgColor = Color(0xFF05060A)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(Modifier.height(56.dp)) // 顶部占位（可换成真正的TopBar）

        // 发现通讯录朋友 卡片
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, bottom = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // 中间插画占位
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(Color(0xFF12D1C2)),
                    contentAlignment = Alignment.Center
                ) {

                }

                Spacer(Modifier.height(24.dp))

                Text(
                    text = "发现通讯录朋友",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "你身边的朋友在用抖音，快去看看吧",
                    color = Color(0xFFB0B0B5),
                    fontSize = 14.sp
                )

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { /* TODO 通讯录权限 */ },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF2850),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .height(48.dp),
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text("查看", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    text = "更多添加朋友的方式",
                    color = Color(0xFF4F8DFF),
                    fontSize = 13.sp
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // 为你推荐 标题
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "为你推荐",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.width(4.dp))

            Spacer(Modifier.weight(1f))
            Text(
                text = "关闭",
                color = Color(0xFF777985),
                fontSize = 14.sp
            )
        }

        Spacer(Modifier.height(8.dp))

        val mockFriends = listOf(
            Triple("火种Lewis", "可能认识的人", "关注"),
            Triple("飞蟹蚊子", "可能认识的人", "关注"),
            Triple("北极圈剩饭", "可能认识的人", "关注")
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(mockFriends) { (name, desc, btnLabel) ->
                FriendRecommendItem(
                    name = name,
                    desc = desc,
                    buttonText = btnLabel
                )
            }
        }
    }
}

@Composable
private fun FriendRecommendItem(
    name: String,
    desc: String,
    buttonText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 头像占位
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFF262833)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name.take(1),
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = desc,
                color = Color(0xFF777985),
                fontSize = 13.sp
            )
        }

        Button(
            onClick = { /* TODO 关注 */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF2850),
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(buttonText, fontSize = 14.sp)
        }

        Spacer(Modifier.width(8.dp))
    }
}