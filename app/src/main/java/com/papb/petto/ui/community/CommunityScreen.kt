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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Notifications
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
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextField
import com.papb.petto.data.model.Article
import com.papb.petto.ui.components.BottomTab
import com.papb.petto.ui.components.FloatingBottomNavigation

private val forumFilters = listOf("Newest", "Health", "Adopt", "Other")

@Composable
fun CommunityScreen(
    uiState: CommunityUiState,
    onTabSelected: (CommunityTab) -> Unit,
    onForumFilterChange: (String) -> Unit,
    onArticleQueryChange: (String) -> Unit,
    onArticleCategoryChange: (String) -> Unit,
    onArticleClick: (Int) -> Unit,
    onMakePostClick: () -> Unit,
    onBottomTabSelected: (BottomTab) -> Unit,
) {
    val colors = MaterialTheme.colorScheme

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colors.background)
            .statusBarsPadding()
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Community",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.onBackground
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Notifications,
                        contentDescription = "Notifikasi",
                        tint = colors.onBackground,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(Modifier.size(12.dp))
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Profil",
                            tint = colors.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            TabRowCommunity(
                selected = uiState.selectedTab,
                onTabSelected = onTabSelected
            )

            Spacer(Modifier.height(14.dp))

            when (uiState.selectedTab) {
                CommunityTab.FORUM -> ForumContent(
                    uiState = uiState,
                    onForumFilterChange = onForumFilterChange
                )

                CommunityTab.MISSING -> Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    Alignment.Center
                ) {
                    Text("Fitur lapor hewan hilang", color = colors.onSurfaceVariant)
                }

                CommunityTab.EDUCATION -> EducationContent(
                    uiState = uiState,
                    onArticleQueryChange = onArticleQueryChange,
                    onArticleCategoryChange = onArticleCategoryChange,
                    onArticleClick = onArticleClick
                )
            }
        }

        if (uiState.selectedTab == CommunityTab.FORUM) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 20.dp, bottom = 110.dp)
                    .clip(CircleShape)
                    .background(colors.primary)
                    .clickable { onMakePostClick() }
                    .padding(horizontal = 18.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = colors.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(Modifier.size(6.dp))
                Text("Make a post", color = colors.onPrimary, fontSize = 14.sp)
            }
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
            ) {
                FloatingBottomNavigation(
                    currentTab = BottomTab.COMMUNITY,
                    onTabSelected = onBottomTabSelected
                )
            }
        }
    }
}

@Composable
private fun TabRowCommunity(
    selected: CommunityTab,
    onTabSelected: (CommunityTab) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val tabs = listOf(
        CommunityTab.FORUM to "Forum",
        CommunityTab.MISSING to "Missing",
        CommunityTab.EDUCATION to "Education"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(CircleShape)
            .background(colors.primaryContainer)
            .padding(4.dp)
    ) {
        tabs.forEach { (tab, label) ->
            val active = tab == selected
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(CircleShape)
                    .background(if (active) colors.primary else colors.primaryContainer)
                    .clickable { onTabSelected(tab) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                    color = if (active) colors.onPrimary else colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ForumContent(
    uiState: CommunityUiState,
    onForumFilterChange: (String) -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column {
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

private val articleCategories = listOf("All", "Health", "Nutrition", "First Aid", "Behavior")

@Composable
private fun EducationContent(
    uiState: CommunityUiState,
    onArticleQueryChange: (String) -> Unit,
    onArticleCategoryChange: (String) -> Unit,
    onArticleClick: (Int) -> Unit
) {
    val colors = MaterialTheme.colorScheme
    val featured = uiState.articles.firstOrNull()
    val rest = uiState.articles.drop(1)

    Column {
        OutlinedTextField(
            value = uiState.articleQuery,
            onValueChange = onArticleQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search by name, breed, or color...", fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            singleLine = true,
            shape = CircleShape
        )

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            articleCategories.forEach { category ->
                val active = uiState.articleCategory == category
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(if (active) colors.primary else colors.surfaceVariant)
                        .clickable { onArticleCategoryChange(category) }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        category,
                        fontSize = 12.sp,
                        color = if (active) colors.onPrimary else colors.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            if (featured != null) {
                item {
                    FeaturedArticleCard(
                        article = featured,
                        onClick = { onArticleClick(featured.id) }
                    )
                }
            }

            if (rest.isNotEmpty()) {
                item {
                    Text(
                        "Artikel Terbaru",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.onBackground,
                        modifier = Modifier.padding(top = 6.dp)
                    )
                }
            }

            items(rest) { article ->
                ArticleRow(
                    article = article,
                    onClick = { onArticleClick(article.id) }
                )
            }

            if (uiState.articles.isEmpty()) {
                item {
                    Text("Tidak ada artikel yang cocok", color = colors.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
private fun FeaturedArticleCard(
    article: Article,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .border(1.dp, colors.outline.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Pets,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(40.dp)
            )
        }

        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CategoryBadge(article.category)
                Spacer(Modifier.size(8.dp))
                Text(article.publishedAt, fontSize = 11.sp, color = colors.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                article.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(article.excerpt, fontSize = 12.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun ArticleRow(
    article: Article,
    onClick: () -> Unit
) {
    val colors = MaterialTheme.colorScheme

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(colors.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.Pets,
                contentDescription = null,
                tint = colors.primary,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(Modifier.size(12.dp))

        Column(Modifier.weight(1f)) {
            CategoryBadge(article.category)
            Spacer(Modifier.height(6.dp))
            Text(
                article.title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = colors.onBackground
            )
            Spacer(Modifier.height(4.dp))
            Text(article.publishedAt, fontSize = 11.sp, color = colors.onSurfaceVariant)
        }
    }
}

@Composable
private fun CategoryBadge(category: String) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(colors.primary)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(category, fontSize = 10.sp, color = colors.onPrimary)
    }
}