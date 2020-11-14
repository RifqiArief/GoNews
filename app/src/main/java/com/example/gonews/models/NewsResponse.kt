package com.example.gonews.models

import com.example.gonews.models.Article

data class NewsResponse(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)