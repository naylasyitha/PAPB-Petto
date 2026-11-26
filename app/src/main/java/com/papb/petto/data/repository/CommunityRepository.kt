package com.papb.petto.data.repository

import com.papb.petto.data.model.Article
import com.papb.petto.data.model.ForumPost
import com.papb.petto.data.remote.SupabaseClient
import com.papb.petto.data.remote.dto.ArticleDto
import com.papb.petto.data.remote.dto.ForumPostDto
import com.papb.petto.data.remote.dto.toArticle
import com.papb.petto.data.remote.dto.toForumPost
import io.github.jan.supabase.postgrest.from

interface CommunityRepository {
    suspend fun getForumPosts(): Result<List<ForumPost>>
    suspend fun getArticles(): Result<List<Article>>
}

class CommunityRepositoryImpl : CommunityRepository {
    private val client = SupabaseClient.client

    override suspend fun getForumPosts(): Result<List<ForumPost>> {
        return try {
            val posts = client.from("forum_posts")
                .select()
                .decodeList<ForumPostDto>()
                .map { it.toForumPost() }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getArticles(): Result<List<Article>> {
        return try {
            val articles = client.from("articles")
                .select()
                .decodeList<ArticleDto>()
                .map { it.toArticle() }
            Result.success(articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
class DummyCommunityRepository : CommunityRepository {

    override suspend fun getForumPosts(): Result<List<ForumPost>> = Result.success(listOf(
        ForumPost(
            id = 1,
            authorName = "Anasya Laili",
            authorMeta = "2 jam lalu · Kesehatan",
            title = "My cat has been sneezing all day, is that normal?",
            body = "My 2 years old cat has been sneezing from morning until this noon. " +
                    "It wasn't non stop but almost per 5 minute. I'm planning to go to th...",
            commentCount = 12,
            likeCount = 24,
            tag = "Health"
        ),
        ForumPost(
            id = 2,
            authorName = "Sabrina Qalby",
            authorMeta = "5 jam lalu · Adopsi",
            title = "Tips for helping an adopted dog adjust during the first week",
            body = "Sharing my experience after adopting from a shelter last mont. " +
                    "Hopefully, this helps those who are just starting out!",
            commentCount = 8,
            likeCount = 21,
            tag = "Adopt"
        ),
        ForumPost(
            id = 3,
            authorName = "Nayla Masyitha",
            authorMeta = "8 jam lalu · Nutrisi",
            title = "My puppy has been refusing to eat",
            body = "He's just 2 month golden retriever pup. He's eating fine yesterday " +
                    "but suddenly refused to eat this morning. I've been tryi...",
            commentCount = 5,
            likeCount = 9,
            tag = "Health"
        ),
        ForumPost(
            id = 4,
            authorName = "Aqeela Sahla",
            authorMeta = "1 hari lalu · Perilaku",
            title = "My dog became afraid to go outside after we move",
            body = "It's been a week, but they still refuse to go out for a walk. " +
                    "Is there a way to train them gradually?",
            commentCount = 6,
            likeCount = 18,
            tag = "Other"
        )
    ))

    override suspend fun getArticles(): Result<List<Article>> = Result.success(listOf(
        Article(
            id = 1,
            title = "How Often Should You Vaccinate Your Cat?",
            category = "Health",
            excerpt = "As pet parents, ensuring our furry companions are protected from " +
                    "preventable infectious diseases is one of our fundam...",
            publishedAt = "Oct 12, 2026",
            authorName = "Dr. Satwa, DVM",
            authorTitle = "Clinical Veterinary Advisor",
            content = "As pet parents, ensuring our furry companions are protected from " +
                    "preventable infectious diseases is one of our fundamental acts of love. " +
                    "Vaccinations play a crucial role for both indoor cats and adventurous " +
                    "outdoor explorers alike."
        ),
        Article(
            id = 2,
            title = "Safe and Toxic Foods for Dogs",
            category = "Nutrition",
            excerpt = "Beberapa makanan rumahan ternyata berbahaya untuk anjing.",
            publishedAt = "Oct 11, 2026"
        ),
        Article(
            id = 3,
            title = "Why Does Your Cat Suddenly Refuses to Use the Litter Box?",
            category = "Behavior",
            excerpt = "Perubahan kebiasaan buang air bisa jadi tanda masalah kesehatan.",
            publishedAt = "Oct 9, 2026"
        ),
        Article(
            id = 4,
            title = "Signs of Dehydration in Pets",
            category = "First Aid",
            excerpt = "Kenali tanda awal dehidrasi sebelum kondisinya memburuk.",
            publishedAt = "Oct 10, 2026"
        )
    ))
}