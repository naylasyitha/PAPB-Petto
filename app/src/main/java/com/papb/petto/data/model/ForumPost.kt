package com.papb.petto.data.model

data class ForumPost(
    val id: Int,
    val authorName: String,
    val authorMeta: String,
    val title: String,
    val body: String,
    val commentCount: Int,
    val likeCount: Int,
    val tag: String
)