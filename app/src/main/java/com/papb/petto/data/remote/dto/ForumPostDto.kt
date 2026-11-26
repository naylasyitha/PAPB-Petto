package com.papb.petto.data.remote.dto

import com.papb.petto.data.model.ForumPost
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ForumPostDto(
    val id: Int,
    @SerialName("author_name") val authorName: String,
    @SerialName("author_meta") val authorMeta: String? = null,
    val title: String,
    val body: String? = null,
    @SerialName("comment_count") val commentCount: Int = 0,
    @SerialName("like_count") val likeCount: Int = 0,
    val tag: String? = null
)

fun ForumPostDto.toForumPost(): ForumPost = ForumPost(
    id = id,
    authorName = authorName,
    authorMeta = authorMeta.orEmpty(),
    title = title,
    body = body.orEmpty(),
    commentCount = commentCount,
    likeCount = likeCount,
    tag = tag.orEmpty()
)