package com.example.smalldy.ui.common

import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.smalldy.data.VideoData

@Composable
fun VideoIntroCard(
    data: VideoData,
    onVideoClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column {
        AsyncImage(   // 用 Coil 显示网络封面图
            model = data.image,
            contentDescription = null,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        Text(
            text = data.title,
            modifier = Modifier.padding(8.dp),
            maxLines = 2,
            fontSize = 16.sp
        )
        Row {
            AsyncImage(
                model = data.userPic,
                contentDescription = null,
                modifier = Modifier
                    .padding(8.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .size(40.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = data.userName,
                modifier = Modifier.padding(2.dp),
                maxLines = 1,
                fontSize = 14.sp
            )
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Image(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Favorite"
            )
            Text(
                text = data.likeCount.toString(),
                modifier = Modifier.padding(2.dp)
                    .padding(2.dp),
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun VideoIntroCardPreview() {
    val context = LocalContext.current
    val rawImages = listOf("cat2", "cat3", "cat4", "no_stress")
    VideoIntroCard(
        data = VideoData(
            image = Uri.parse("android.resource://${context.packageName}/raw/${rawImages[0]}").toString(),
            title = "This is a title",
            userPic = Uri.parse("android.resource://${context.packageName}/raw/${rawImages[1]}").toString(),
            userName = "UserName",
            isLiked = true,
            likeCount = 1024
        ),
        onVideoClick = {}
    )
}