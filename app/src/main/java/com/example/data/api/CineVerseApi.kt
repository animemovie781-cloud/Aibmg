package com.example.data.api

import com.example.data.model.*
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CineVerseApi {

    @GET("trending")
    suspend fun getTrending(): Response<TrendingResponse>

    @GET("search")
    suspend fun search(@Query("q") query: String): Response<SearchResponse>

    @GET("detail/{id}")
    suspend fun getDetail(@Path("id") id: String): Response<DetailResponse>

    @GET("stream/{id}")
    suspend fun getStream(@Path("id") id: String): Response<StreamResponse>

    @GET("health")
    suspend fun healthCheck(): Response<Map<String, Any>>
}
