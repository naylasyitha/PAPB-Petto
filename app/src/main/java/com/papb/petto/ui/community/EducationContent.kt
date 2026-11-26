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
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.papb.petto.data.model.Article

private val articleCategories = listOf("All", "Health", "Nutrition", "First Aid", "Behavior")

@Composable
fun EducationContent(
    uiState: CommunityUiState,
    onArticleQueryChange: (String) -> Unit,
    onArticleCategoryChange: (String) -> Unit,
    onArticleClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = MaterialTheme.colorScheme
    val featured = uiState.articles.firstOrNull()
    val rest = uiState.articles.drop(1)

    Column(modifier = modifier.fillMaxWidth().padding(horizontal = 20.dp)) {

        Spacer(Modifier.height(12.dp))

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
                ArticleCategoryBadge(article.category)
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
            ArticleCategoryBadge(article.category)
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
private fun ArticleCategoryBadge(category: String) {
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