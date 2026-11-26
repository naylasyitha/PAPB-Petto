package com.papb.petto.data.remote.dto

import com.papb.petto.data.model.Article
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleDto(
    val id: Int,
    val title: String,
    val category: String,
    val excerpt: String? = null,
    val content: String? = null,
    @SerialName("published_at") val publishedAt: String? = null,
    @SerialName("image_url") val imageUrl: String? = null,
    @SerialName("author_name") val authorName: String? = null,
    @SerialName("author_title") val authorTitle: String? = null
)

fun ArticleDto.toArticle(): Article = Article(
    id = id,
    title = title,
    category = category,
    excerpt = excerpt.orEmpty(),
    publishedAt = publishedAt.orEmpty(),
    imageUrl = imageUrl,
    authorName = authorName,
    authorTitle = authorTitle,
    content = content.orEmpty()
)