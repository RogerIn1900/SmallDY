package com.example.smalldy.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.smalldy.ui.components.FeedCard
import com.example.smalldy.ui.components.NavItem
import com.example.smalldy.ui.components.TopNav

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val feedItems by viewModel.feedItems.collectAsStateWithLifecycle()
    val activeNavItem by remember { mutableIntStateOf(2) }

    val navItems = remember(activeNavItem) {
        listOf(
            NavItem("司城", active = activeNavItem == 0),
            NavItem("团购", active = activeNavItem == 1),
            NavItem("直播", active = activeNavItem == 2, badge = true),
            NavItem("商城", active = activeNavItem == 3),
            NavItem("推荐", active = activeNavItem == 4, badge = true)
        )
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopNav(
                navItems = navItems,
                onMenuClick = {},
                onSearchClick = {}
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF5F5F5))
        ) {
            feedItems.forEach { feedData ->
                FeedCard(
                    data = feedData,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    onClick = {}
                )
            }

            Spacer(
                modifier = Modifier.padding(bottom = 80.dp)
            )
        }
    }
}
