package com.example.gonews.api

import com.example.gonews.models.NewsResponse
import com.example.gonews.util.Constants
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {
    @GET("/v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("category")
        countryCode : String = "technology",
        @Query("page")
        pageNumber:Int = 1,
        @Query("apiKey")
        apiKey : String = Constants.API_KEY
    ): Response<NewsResponse>

    @GET("/v2/everything")
    suspend fun seachNews(
        @Query("q")
        searchQuery : String = "technology",
        @Query("page")
        pageNumber:Int = 1,
        @Query("apiKey")
        apiKey : String = Constants.API_KEY
    ): Response<NewsResponse>
}