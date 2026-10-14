package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.network.Category
import com.example.marsphotos.network.DisplayItem
import com.example.marsphotos.network.MarsApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface RickAndMortyUiState {
    data class Success(
        val selectedItem: DisplayItem?,
        val totalCountInApi: Int,
        val retrievedCount: Int,
        val category: Category
    ) : RickAndMortyUiState

    object Empty : RickAndMortyUiState
    object Error : RickAndMortyUiState
    object Loading : RickAndMortyUiState
}

class MarsViewModel : ViewModel() {

    var selectedCategory by mutableStateOf(Category.CHARACTER)
        private set

    var searchQuery by mutableStateOf("")
        private set

    var uiState: RickAndMortyUiState by mutableStateOf(RickAndMortyUiState.Loading)
        private set

    private var currentItems: List<DisplayItem> = emptyList()
    private var totalApiCount: Int = 0
    private var totalApiPages: Int = 1
    private var searchJob: Job? = null
    private var fetchJob: Job? = null

    init {
        loadData()
    }

    fun onCategoryChange(category: Category) {
        if (selectedCategory != category) {
            selectedCategory = category
            searchQuery = ""
            totalApiPages = 1
            loadData()
        }
    }

    fun onSearchQueryChange(query: String) {
        searchQuery = query
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            totalApiPages = 1
            loadData()
        }
    }

    fun rollRandomItem() {
        // Faz o pedido à API para obter imagens e informações e escolhe uma delas random
        loadData(fetchRandomPage = true)
    }

    fun loadData(fetchRandomPage: Boolean = false) {
        fetchJob?.cancel()
        fetchJob = viewModelScope.launch {
            uiState = RickAndMortyUiState.Loading
            try {
                val queryParam = searchQuery.trim().ifEmpty { null }
                val pageParam = if (fetchRandomPage && totalApiPages > 1) {
                    (1..totalApiPages).random()
                } else {
                    null
                }
                when (selectedCategory) {
                    Category.CHARACTER -> {
                        val response = MarsApi.retrofitService.getCharacters(queryParam, pageParam)
                        totalApiCount = response.info?.count ?: response.results.size
                        totalApiPages = response.info?.pages ?: 1
                        currentItems = response.results.map {
                            DisplayItem(
                                id = it.id,
                                name = it.name,
                                imageUrl = it.image.ifBlank { "https://rickandmortyapi.com/api/character/avatar/1.jpeg" },
                                label1 = "Status",
                                value1 = it.status.ifBlank { "Unknown" },
                                label2 = "Species",
                                value2 = it.species.ifBlank { "Unknown" }
                            )
                        }
                    }
                    Category.EPISODE -> {
                        val response = MarsApi.retrofitService.getEpisodes(queryParam, pageParam)
                        totalApiCount = response.info?.count ?: response.results.size
                        totalApiPages = response.info?.pages ?: 1
                        currentItems = response.results.map {
                            DisplayItem(
                                id = it.id,
                                name = it.name,
                                imageUrl = "https://rickandmortyapi.com/api/character/avatar/19.jpeg",
                                label1 = "Episode",
                                value1 = it.episode.ifBlank { "N/A" },
                                label2 = "Air Date",
                                value2 = it.airDate.ifBlank { "N/A" }
                            )
                        }
                    }
                    Category.LOCATION -> {
                        val response = MarsApi.retrofitService.getLocations(queryParam, pageParam)
                        totalApiCount = response.info?.count ?: response.results.size
                        totalApiPages = response.info?.pages ?: 1
                        currentItems = response.results.map {
                            DisplayItem(
                                id = it.id,
                                name = it.name,
                                imageUrl = "https://rickandmortyapi.com/api/character/avatar/19.jpeg",
                                label1 = "Type",
                                value1 = it.type.ifBlank { "Unknown" },
                                label2 = "Dimension",
                                value2 = it.dimension.ifBlank { "Unknown" }
                            )
                        }
                    }
                }

                if (currentItems.isNotEmpty()) {
                    val randomChoice = currentItems.random()
                    uiState = RickAndMortyUiState.Success(
                        selectedItem = randomChoice,
                        totalCountInApi = totalApiCount,
                        retrievedCount = currentItems.size,
                        category = selectedCategory
                    )
                } else {
                    uiState = RickAndMortyUiState.Empty
                }
            } catch (e: HttpException) {
                if (e.code() == 404) {
                    // Rick e Morty API retorna 404 quando nao ha resultados
                    currentItems = emptyList()
                    totalApiCount = 0
                    totalApiPages = 1
                    uiState = RickAndMortyUiState.Empty
                } else {
                    uiState = RickAndMortyUiState.Error
                }
            } catch (e: IOException) {
                uiState = RickAndMortyUiState.Error
            } catch (e: Exception) {
                uiState = RickAndMortyUiState.Error
            }
        }
    }
}
