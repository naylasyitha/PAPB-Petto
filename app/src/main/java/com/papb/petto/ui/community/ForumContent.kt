package com.papb.petto.ui.community

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.data.model.ForumPost

private val forumFilters = listOf("Newest", "Health", "Adopt", "Other")

@Composable
fun ForumContent(
    uiState: CommunityUiState,
    onForumFilterChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            forumFilters.forEach { filter ->
                val active = uiState.forumFilter == filter
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (active) colors.primary else colors.surfaceVariant)
                        .clickable { onForumFilterChange(filter) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        filter,
                        fontSize = 12.sp,
                        color = if (active) colors.onPrimary else colors.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            items(uiState.posts) { post ->
                ForumPostCard(post)
            }
        }
    }
}

@Composable
private fun ForumPostCard(post: ForumPost) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(colors.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    tint = colors.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(Modifier.size(10.dp))
            Column {
                Text(
                    post.authorName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Text(post.authorMeta, fontSize = 11.sp, color = colors.onSurfaceVariant)
            }
        }

        Spacer(Modifier.height(10.dp))

        Text(
            post.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = colors.onBackground
        )

        Spacer(Modifier.height(4.dp))

        Text(post.body, fontSize = 12.sp, color = colors.onSurfaceVariant)

        Spacer(Modifier.height(10.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Outlined.ChatBubbleOutline,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.size(4.dp))
            Text("${post.commentCount}", fontSize = 12.sp, color = colors.onSurfaceVariant)

            Spacer(Modifier.size(16.dp))

            Icon(
                Icons.Default.FavoriteBorder,
                contentDescription = null,
                tint = colors.onSurfaceVariant,
                modifier = Modifier.size(14.dp)
            )
            Spacer(Modifier.size(4.dp))
            Text("${post.likeCount}", fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}