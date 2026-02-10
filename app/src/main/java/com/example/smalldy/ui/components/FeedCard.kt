package com.example.smalldy.ui.components

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smalldy.ui.model.FeedCardUiModel

@Composable
fun FeedCard(
    data: FeedCardUiModel,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(8.dp))
            .clip(RoundedCornerShape(8.dp))
    ) {
        // Image with 3:4 aspect ratio
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(3f / 4f)
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
        ) {
            AsyncImage(
                model = data.image,
                contentDescription = data.title,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }

        // Content
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            // Title and Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = data.title,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (!data.description.isNullOrEmpty()) {
                    Text(
                        text = data.description,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 0.dp)
                    )
                }
            }

            // Author and Likes
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Author info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    AsyncImage(
                        model = data.avatar,
                        contentDescription = data.author,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        text = data.author,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }

                // Likes
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = "Likes",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF9CA3AF)
                    )
                    Text(
                        text = data.formattedLikes,
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FeedCardPreview() {
    val context = LocalContext.current
    val pkg = context.packageName
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        FeedCard(
            data = FeedCardUiModel(
                image = Uri.parse("android.resource://$pkg/raw/cat2").toString(),
                title = "探索城市美食之旅",
                description = "发现隐藏在城市角落的美味佳肴",
                author = "美食探索家",
                avatar = Uri.parse("android.resource://$pkg/raw/cat3").toString(),
                formattedLikes = "1.2万"
            ),
            onClick = {}
        )
    }
}
