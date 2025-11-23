package com.example.smalldy.ui.common

import android.R
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import com.example.smalldy.R as AppR

data class FeedCardData(
    val image: String,
    val title: String,
    val description: String? = null,
    val author: String,
    val avatar: String,
    val likes: Int
)

/**
 * 生成缺省的 FeedCardData，使用 raw 文件夹中的图片
 */
@Composable
fun generateDefaultFeedCardData(): FeedCardData {
    val context = LocalContext.current
    val rawImages = listOf("cat2.gif", "cat3.gif", "cat4.gif", "no_stress.png")
    val randomImage = rawImages.random()
    val imageUri = Uri.parse("android.resource://${context.packageName}/raw/${randomImage.replace(".gif", "").replace(".png", "")}")
    
    val titles = listOf(
        "探索城市美食之旅",
        "周末户外运动指南",
        "摄影技巧分享",
        "旅行日记：云南之行",
        "科技产品评测",
        "生活小妙招",
        "音乐分享时刻",
        "读书心得分享",
        "健身打卡日记",
        "美食制作教程"
    )
    
    val descriptions = listOf(
        "发现隐藏在城市角落的美味佳肴，每一口都是惊喜",
        "享受阳光，拥抱自然，让身体和心灵都得到放松",
        "用镜头记录生活中的美好瞬间，捕捉每一个精彩时刻",
        "彩云之南，风景如画，感受不一样的民族风情",
        "最新科技产品深度体验，为你提供最真实的购买建议",
        "简单实用的生活技巧，让每一天都更美好",
        "分享好听的音乐，让心情随着旋律飞扬",
        "好书推荐，一起在文字中寻找智慧与温暖",
        "坚持运动，遇见更好的自己",
        "手把手教你制作美味佳肴，享受烹饪的乐趣"
    )
    
    val authors = listOf(
        "美食探索家",
        "运动达人",
        "摄影师小王",
        "旅行者",
        "科技评测",
        "生活小助手",
        "音乐爱好者",
        "书虫",
        "健身教练",
        "美食博主"
    )
    
    val avatars = listOf(
        "https://i.pravatar.cc/150?img=1",
        "https://i.pravatar.cc/150?img=2",
        "https://i.pravatar.cc/150?img=3",
        "https://i.pravatar.cc/150?img=4",
        "https://i.pravatar.cc/150?img=5",
        "https://i.pravatar.cc/150?img=6",
        "https://i.pravatar.cc/150?img=7",
        "https://i.pravatar.cc/150?img=8",
        "https://i.pravatar.cc/150?img=9",
        "https://i.pravatar.cc/150?img=10"
    )
    
    return FeedCardData(
        image = imageUri.toString(),
        title = titles.random(),
        description = descriptions.random(),
        author = authors.random(),
        avatar = avatars.random(),
        likes = (100..50000).random()
    )
}

@Composable
fun FeedCard(
    data: FeedCardData? = null,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val defaultData = remember { generateDefaultFeedCardData() }
    val cardData = data ?: defaultData
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
                    .height(48.dp)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = data.title,
                    fontSize = 14.sp,
                    color = Color(0xFF1A1A1A),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (!data.description.isNullOrEmpty()) {
                    Text(
                        text = data.description,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
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
                        text = formatLikes(data.likes),
                        fontSize = 12.sp,
                        color = Color(0xFF666666)
                    )
                }
            }
        }
    }
}

private fun formatLikes(num: Int): String {
    return if (num >= 10000) {
        "${(num / 10000.0).let { String.format("%.1f", it) }}万"
    } else {
        num.toString()
    }
}

@Preview(showBackground = true)
@Composable
fun FeedCardPreview() {
    val context = LocalContext.current
    val sampleData = remember {
        val rawImages = listOf("cat2.gif", "cat3.gif", "cat4.gif", "no_stress.png")
        val randomImage = rawImages.random()
        val imageUri = Uri.parse("android.resource://${context.packageName}/raw/${randomImage.replace(".gif", "").replace(".png", "")}")
        
        FeedCardData(
            image = imageUri.toString(),
            title = "探索城市美食之旅",
            description = "发现隐藏在城市角落的美味佳肴，每一口都是惊喜",
            author = "美食探索家",
            avatar = "https://i.pravatar.cc/150?img=1",
            likes = 12345
        )
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp)
    ) {
        FeedCard(
            data = sampleData,
            onClick = {}
        )
    }
}

