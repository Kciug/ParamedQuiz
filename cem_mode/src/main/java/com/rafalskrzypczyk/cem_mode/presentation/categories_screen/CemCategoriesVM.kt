package com.rafalskrzypczyk.cem_mode.presentation.categories_screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.cem_mode.domain.use_cases.CemUseCases
import com.rafalskrzypczyk.cem_mode.navigation.CemCategoriesRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CemCategoriesVM @Inject constructor(
    private val useCases: CemUseCases,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val route = savedStateHandle.toRoute<CemCategoriesRoute>()
    private val parentId = route.parentId
    private val categoryTitle = route.categoryTitle

    private val _state = MutableStateFlow(CemCategoriesState(title = categoryTitle))
    val state = _state.asStateFlow()

    init {
        loadCategories()
        observeCategoriesUpdate()
    }

    fun onEvent(event: CemCategoriesUIEvents) {
        when (event) {
            CemCategoriesUIEvents.OnRetry -> loadCategories()
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            useCases.getCemCategories(parentId).collectLatest { response ->
                val result = when (response) {
                    is Response.Success -> Response.Success(response.data.map { it.toUIM() })
                    is Response.Error -> Response.Error(response.error)
                    Response.Loading -> Response.Loading
                }
                _state.update { it.copy(categories = result) }
            }
        }
    }

    private fun observeCategoriesUpdate() {
        viewModelScope.launch {
            useCases.getUpdatedCemCategories(parentId).collectLatest { list ->
                _state.update { it.copy(categories = Response.Success(list.map { it.toUIM() })) }
            }
        }
    }
}
