package com.example.smalldy.ui.Pages.MinePage


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
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun MinePage(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF6F7FB))
    ) {
        // 顶部背景 + 头像区域
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFF4CC3FF), Color(0xFF6E8BFF))
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                // 顶部操作行：添加好友、新访客等，这里简单化
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "添加好友",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "新访客 1",
                        color = Color.White,
                        fontSize = 14.sp
                    )
                }

                Spacer(Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 头像 + 添加按钮
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF24DA67)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "点击填写名字",
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "抖音号：25954785652",
                            color = Color(0xFFE4ECFF),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                // 统计信息：获赞 / 互关 / 关注 / 粉丝
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    ProfileStat("0", "获赞")
                    ProfileStat("1", "互关")
                    ProfileStat("22", "关注")
                    ProfileStat("1", "粉丝")
                }
            }
        }

        // 中间功能区：商城 / 钱包 / 全部功能
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MineFeatureItem("抖音商城", Icons.Default.ShoppingCart)
            MineFeatureItem("我的钱包", Icons.Default.AccountBox)
            MineFeatureItem("全部功能", Icons.Default.Close)
        }

        // Tab 行
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Text(
                text = "作品",
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(text = "收藏", color = Color.Gray)
            Text(text = "喜欢", color = Color.Gray)
        }

        Spacer(Modifier.height(8.dp))

        // 下方操作卡片列表
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            MineActionRow(
                title = "添加名字",
                subtitle = "方便朋友认识你",
                buttonText = "去添加"
            )
            Divider()
            MineActionRow(
                title = "添加头像",
                subtitle = "展示代表你的照片",
                buttonText = "去添加"
            )
            Divider()
            MineActionRow(
                title = "发作品，留下记忆",
                subtitle = "开始在抖音记录生活",
                buttonText = "去发布"
            )
        }
    }
}

@Composable
private fun ProfileStat(
    value: String,
    label: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            color = Color(0xFFE4ECFF),
            fontSize = 12.sp
        )
    }
}

@Composable
private fun MineFeatureItem(
    label: String,
    icon: ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = Color.Black,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(text = label, color = Color.Black, fontSize = 13.sp)
    }
}

@Composable
private fun MineActionRow(
    title: String,
    subtitle: String,
    buttonText: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = title, color = Color.Black, fontSize = 15.sp)
            Spacer(Modifier.height(2.dp))
            Text(text = subtitle, color = Color.Gray, fontSize = 12.sp)
        }

        Button(
            onClick = { /* TODO */ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF4180),
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
            shape = RoundedCornerShape(999.dp),
            modifier = Modifier.height(32.dp)
        ) {
            Text(buttonText, fontSize = 13.sp)
        }
    }
}