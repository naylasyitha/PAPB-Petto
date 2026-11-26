package com.papb.petto.ui.community

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.papb.petto.data.model.Article
import com.papb.petto.data.model.ForumPost
import com.papb.petto.data.repository.CommunityRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CommunityViewModel(
    private val repository: CommunityRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState: StateFlow<CommunityUiState> = _uiState.asStateFlow()

    private var allPosts: List<ForumPost> = emptyList()
    private var allArticles: List<Article> = emptyList()

    fun load() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                allPosts = repository.getForumPosts().getOrThrow()
                allArticles = repository.getArticles().getOrThrow()
                _uiState.value = _uiState.value.copy(isLoading = false)
                applyFilters()
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memuat data komunitas"
                )
            }
        }
    }

    fun onForumFilterChange(filter: String) {
        _uiState.value = _uiState.value.copy(forumFilter = filter)
        applyFilters()
    }

    fun onArticleQueryChange(value: String) {
        _uiState.value = _uiState.value.copy(articleQuery = value)
        applyFilters()
    }

    fun onArticleCategoryChange(category: String) {
        _uiState.value = _uiState.value.copy(articleCategory = category)
        applyFilters()
    }

    private fun applyFilters() {
        val state = _uiState.value

        val posts = if (state.forumFilter == "Newest") {
            allPosts
        } else {
            allPosts.filter { it.tag == state.forumFilter }
        }

        val articles = allArticles
            .filter { state.articleCategory == "All" || it.category == state.articleCategory }
            .filter {
                state.articleQuery.isBlank() ||
                        it.title.contains(state.articleQuery, ignoreCase = true)
            }

        _uiState.value = _uiState.value.copy(posts = posts, articles = articles)
    }

    companion object {
        fun factory(repository: CommunityRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CommunityViewModel(repository) as T
                }
            }
    }
}