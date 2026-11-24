package com.papb.petto.ui.community

import com.papb.petto.data.model.Article
import com.papb.petto.data.model.ForumPost

enum class CommunityTab { FORUM, MISSING, EDUCATION }

data class CommunityUiState(
    val isLoading: Boolean = false,
    val selectedTab: CommunityTab = CommunityTab.FORUM,
    val forumFilter: String = "Newest",
    val posts: List<ForumPost> = emptyList(),
    val articleQuery: String = "",
    val articleCategory: String = "All",
    val articles: List<Article> = emptyList(),
    val errorMessage: String? = null
)