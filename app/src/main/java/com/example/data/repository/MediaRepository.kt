package com.example.data.repository

import com.example.data.api.CineVerseApi
import com.example.data.model.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

class MediaRepository {

    private val api: CineVerseApi

    init {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("https://anime-hjku.onrender.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(client)
            .build()

        api = retrofit.create(CineVerseApi::class.java)
    }

    suspend fun getTrending(): Result<TrendingResponse> = safeApiCall { api.getTrending() }

    suspend fun search(query: String): Result<SearchResponse> = safeApiCall { api.search(query) }

    suspend fun getDetail(id: String): Result<DetailResponse> = safeApiCall { api.getDetail(id) }

    suspend fun getStream(id: String): Result<StreamResponse> = safeApiCall { api.getStream(id) }

    private suspend fun <T> safeApiCall(call: suspend () -> Response<T>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    Result.success(body)
                } else {
                    Result.failure(Exception("Empty response body"))
                }
            } else {
                val errorBody = response.errorBody()?.string()
                Result.failure(Exception("API Error: ${response.code()} - $errorBody"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
