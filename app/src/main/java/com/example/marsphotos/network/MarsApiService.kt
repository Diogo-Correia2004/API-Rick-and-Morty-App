package com.example.marsphotos.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Query

private const val BASE_URL = "https://rickandmortyapi.com/api/"

private val json = Json {
    ignoreUnknownKeys = true
    isLenient = true
    coerceInputValues = true
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface RickAndMortyApiService {
    @GET("character")
    suspend fun getCharacters(
        @Query("name") name: String? = null,
        @Query("page") page: Int? = null
    ): ApiResponse<CharacterDto>

    @GET("episode")
    suspend fun getEpisodes(
        @Query("name") name: String? = null,
        @Query("page") page: Int? = null
    ): ApiResponse<EpisodeDto>

    @GET("location")
    suspend fun getLocations(
        @Query("name") name: String? = null,
        @Query("page") page: Int? = null
    ): ApiResponse<LocationDto>
}

object MarsApi {
    val retrofitService: RickAndMortyApiService by lazy {
        retrofit.create(RickAndMortyApiService::class.java)
    }
}