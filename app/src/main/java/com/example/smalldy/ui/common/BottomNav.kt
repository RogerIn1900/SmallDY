package com.example.smalldy.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex

data class BottomNavItem(
    val id: String,
    val label: String,
    val icon: ImageVector
)

@Composable
fun BottomNav(
    activeTab: String = " ",
    onTabChange: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val tabs = listOf(
        BottomNavItem("home", "首页", Icons.Default.Home),
        BottomNavItem("friends", "朋友", Icons.Default.Person),
        BottomNavItem("add", "", Icons.Default.Add),
        BottomNavItem("messages", "消息", Icons.Default.Email),
        BottomNavItem("profile", "我", Icons.Default.Person)
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .zIndex(50f)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFFE5E5E5))
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEach { tab ->
                val isActive = activeTab == tab.id
                val isAddButton = tab.id == "add"

                if (isAddButton) {
                    // Special styling for add button
                    Column(
                        modifier = Modifier
                            .offset(y = (-24).dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { onTabChange(tab.id) },
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1A1A1A))
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = "Add",
                                modifier = Modifier.size(24.dp),
                                tint = Color.White
                            )
                        }
                    }
                } else {
                    // Regular tab button
                    Column(
                        modifier = Modifier.width(64.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        IconButton(
                            onClick = { onTabChange(tab.id) },
                            modifier = Modifier.size(48.dp)
                        ) {
                            Icon(
                                imageVector = tab.icon,
                                contentDescription = tab.label,
                                modifier = Modifier.size(24.dp),
                                tint = if (isActive) Color(0xFF1A1A1A) else Color(0xFF9CA3AF)
                            )
                        }
                        Text(
                            text = tab.label,
                            fontSize = 12.sp,
                            color = if (isActive) Color(0xFF1A1A1A) else Color(0xFF9CA3AF)
                        )
                    }
                }
            }
        }
    }
}


