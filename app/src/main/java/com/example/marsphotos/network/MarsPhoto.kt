package com.example.marsphotos.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val info: ApiInfo? = null,
    val results: List<T> = emptyList()
)

@Serializable
data class ApiInfo(
    val count: Int = 0,
    val pages: Int = 0,
    val next: String? = null,
    val prev: String? = null
)

@Serializable
data class CharacterDto(
    val id: Int,
    val name: String = "",
    val status: String = "",
    val species: String = "",
    val type: String = "",
    val gender: String = "",
    val image: String = ""
)

@Serializable
data class EpisodeDto(
    val id: Int,
    val name: String = "",
    @SerialName("air_date")
    val airDate: String = "",
    val episode: String = ""
)

@Serializable
data class LocationDto(
    val id: Int,
    val name: String = "",
    val type: String = "",
    val dimension: String = ""
)

data class DisplayItem(
    val id: Int,
    val name: String,
    val imageUrl: String,
    val label1: String,
    val value1: String,
    val label2: String,
    val value2: String
)

enum class Category(val displayName: String) {
    CHARACTER("Character"),
    EPISODE("Episode"),
    LOCATION("Location")
}