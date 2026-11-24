package com.papb.petto.data.model

data class Article(
    val id: Int,
    val title: String,
    val category: String,
    val excerpt: String,
    val publishedAt: String,
    val imageUrl: String? = null,
    val authorName: String? = null,
    val authorTitle: String? = null,
    val content: String = ""
)