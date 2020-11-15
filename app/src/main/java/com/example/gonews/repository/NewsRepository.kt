package com.example.gonews.repository

import com.example.gonews.api.RetrofitInstance
import com.example.gonews.db.ArticleDatabase
import com.example.gonews.models.Article

class NewsRepository (val db: ArticleDatabase){

    suspend fun getBreakingNews(countryCode: String, pageNumber:Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery: String, pageNumber:Int) =
        RetrofitInstance.api.seachNews(searchQuery,pageNumber)

    suspend fun upsert(article : Article) = db.getArticleDao().upsert(article)

    fun getSavedNews() = db.getArticleDao().getAllArticles()

    suspend fun deleteArticle(article: Article) = db.getArticleDao().deleteArticle(article)
}