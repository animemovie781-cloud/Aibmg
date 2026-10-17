package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MediaItem(
    val id: String,
    val title: String,
    @Json(name = "poster_url") val posterUrl: String?,
    @Json(name = "release_year") val releaseYear: String?,
    val rating: String?,
    @Json(name = "media_type") val mediaType: String? // "movie" or "tv"
)

@JsonClass(generateAdapter = true)
data class TrendingResponse(
    val success: Boolean,
    val results: List<MediaItem>? = null,
    val error: ApiError? = null
)

@JsonClass(generateAdapter = true)
data class SearchResponse(
    val success: Boolean,
    val results: List<MediaItem>? = null,
    val error: ApiError? = null
)

@JsonClass(generateAdapter = true)
data class ApiError(
    val code: String?,
    val message: String?,
    @Json(name = "upstream_status") val upstreamStatus: Int?
)

@JsonClass(generateAdapter = true)
data class MediaDetail(
    val id: String,
    val title: String,
    @Json(name = "poster_url") val posterUrl: String?,
    @Json(name = "backdrop_url") val backdropUrl: String?,
    val synopsis: String?,
    @Json(name = "release_year") val releaseYear: String?,
    val rating: String?,
    val genres: List<String>?,
    val cast: List<String>?,
    val runtime: String?,
    val seasons: List<Season>? = null
)

@JsonClass(generateAdapter = true)
data class Season(
    @Json(name = "season_number") val seasonNumber: Int,
    val episodes: List<Episode>
)

@JsonClass(generateAdapter = true)
data class Episode(
    val id: String,
    val title: String,
    @Json(name = "episode_number") val episodeNumber: Int
)

@JsonClass(generateAdapter = true)
data class DetailResponse(
    val success: Boolean,
    val item: MediaDetail? = null,
    val error: ApiError? = null
)

@JsonClass(generateAdapter = true)
data class StreamSource(
    val url: String,
    val quality: String,
    @Json(name = "mime_type") val mimeType: String?
)

@JsonClass(generateAdapter = true)
data class StreamResponse(
    val success: Boolean,
    val sources: List<StreamSource>? = null,
    val error: ApiError? = null
)
