package com.example.marsphotos.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.marsphotos.ui.screens.HeaderBar
import com.example.marsphotos.ui.screens.HomeScreen
import com.example.marsphotos.ui.screens.MarsViewModel

@Composable
fun MarsPhotosApp() {
    val marsViewModel: MarsViewModel = viewModel()

    Scaffold(
        topBar = {
            HeaderBar(
                selectedCategory = marsViewModel.selectedCategory,
                onCategorySelected = marsViewModel::onCategoryChange,
                onGetNewDataClicked = marsViewModel::loadData
            )
        }
    ) { paddingValues ->
        Surface(
            modifier = Modifier.fillMaxSize()
        ) {
            HomeScreen(
                uiState = marsViewModel.uiState,
                selectedCategory = marsViewModel.selectedCategory,
                searchQuery = marsViewModel.searchQuery,
                onCategorySelected = marsViewModel::onCategoryChange,
                onSearchQueryChanged = marsViewModel::onSearchQueryChange,
                onRollClicked = marsViewModel::rollRandomItem,
                onRetryClicked = marsViewModel::loadData,
                contentPadding = paddingValues
            )
        }
    }
}
