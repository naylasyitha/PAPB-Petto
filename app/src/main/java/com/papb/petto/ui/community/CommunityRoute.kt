package com.papb.petto.ui.community

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.papb.petto.data.repository.DummyCommunityRepository
import com.papb.petto.ui.components.BottomTab

@Composable
fun CommunityRoute(
    onBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToServiceMap: () -> Unit
) {
    val repository = remember { DummyCommunityRepository() }

    val viewModel: CommunityViewModel = viewModel(
        factory = CommunityViewModel.factory(repository)
    )
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedArticleId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    val article = uiState.articles.firstOrNull { it.id == selectedArticleId }

    if (article != null) {
        ArticleDetailScreen(
            article = article,
            relatedArticles = uiState.articles.filter { it.id != article.id }.take(3),
            onRelatedClick = { id -> selectedArticleId = id },
            onBack = { selectedArticleId = null }
        )
    } else {
        CommunityScreen(
            uiState = uiState,
            onTabSelected = viewModel::onTabSelected,
            onForumFilterChange = viewModel::onForumFilterChange,
            onArticleQueryChange = viewModel::onArticleQueryChange,
            onArticleCategoryChange = viewModel::onArticleCategoryChange,
            onArticleClick = { id -> selectedArticleId = id },
            onMakePostClick = { },
            onBottomTabSelected = { tab ->
                when (tab) {
                    BottomTab.HOME -> onNavigateToHome()
                    BottomTab.LOCATION -> onNavigateToServiceMap()
                    else -> { }
                }
            }
        )
    }
}