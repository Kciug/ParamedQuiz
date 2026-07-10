package com.rafalskrzypczyk.revisions.presentation.config

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.api_response.ResponseState
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.domain.RevisionsRepository
import com.rafalskrzypczyk.revisions.domain.use_cases.GetRevisionsQuestionsUC
import com.rafalskrzypczyk.score.domain.ScoreManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RevisionsConfigVM @Inject constructor(
    private val repository: RevisionsRepository,
    private val getRevisionsQuestions: GetRevisionsQuestionsUC,
    private val scoreManager: ScoreManager
) : ViewModel() {

    private val _state = MutableStateFlow(RevisionsConfigState())
    val state = _state.asStateFlow()

    init {
        initialLoad()
    }

    fun onEvent(event: RevisionsConfigUIEvents) {
        when (event) {
            is RevisionsConfigUIEvents.SelectMode -> {
                val currentState = _state.value
                if (currentState.loadingMode != null) return
                val isAlreadyLoaded = event.mode == currentState.selectedMode &&
                        !currentState.isCategoriesLoading && !currentState.isQuestionsLoading
                if (isAlreadyLoaded) {
                    _state.update { it.copy(isConfigDialogVisible = true) }
                } else {
                    _state.update { it.copy(loadingMode = event.mode) }
                    loadModeData(event.mode)
                }
            }
            is RevisionsConfigUIEvents.DismissConfigDialog -> {
                _state.update { it.copy(isConfigDialogVisible = false) }
            }
            is RevisionsConfigUIEvents.ShowCategoryDialog -> {
                _state.update { it.copy(isCategoryDialogVisible = true) }
            }
            is RevisionsConfigUIEvents.DismissCategoryDialog -> {
                _state.update { it.copy(isCategoryDialogVisible = false) }
            }
            is RevisionsConfigUIEvents.SelectCategory -> {
                _state.update {
                    it.copy(
                        selectedCategory = event.category,
                        isCategoryDialogVisible = false
                    )
                }
                updateQuestionsCountAndLimits()
            }
            is RevisionsConfigUIEvents.SelectCriterion -> {
                _state.update { it.copy(selectedCriterion = event.criterion) }
                updateQuestionsCountAndLimits()
            }
            is RevisionsConfigUIEvents.SelectLimit -> {
                _state.update { it.copy(selectedLimit = event.limit) }
            }
        }
    }

    private fun initialLoad() {
        _state.update { it.copy(responseState = ResponseState.Loading) }
        viewModelScope.launch {
            repository.getCategories(QuizMode.MainMode).collectLatest { response ->
                when (response) {
                    is Response.Success -> {
                        val categories = response.data
                        val defaultCategory = categories.firstOrNull { it.isEligible } ?: categories.firstOrNull()
                        _state.update {
                            it.copy(
                                categoriesList = categories,
                                selectedCategory = defaultCategory,
                                responseState = ResponseState.Success,
                                isModeEligible = categories.any { c -> c.isEligible }
                            )
                        }
                        updateQuestionsCountAndLimits()
                    }
                    is Response.Error -> {
                        _state.update { it.copy(responseState = ResponseState.Error(response.error)) }
                    }
                    Response.Loading -> {
                        _state.update { it.copy(responseState = ResponseState.Loading) }
                    }
                }
            }
        }
    }

    private fun loadModeData(mode: QuizMode) {
        _state.update {
            it.copy(
                selectedMode = mode,
                categoriesList = emptyList(),
                selectedCategory = null,
                isCategoriesLoading = mode != QuizMode.TranslationMode,
                isQuestionsLoading = true,
                isEmptyState = false
            )
        }

        viewModelScope.launch {
            if (mode == QuizMode.TranslationMode) {
                checkTranslationEligibility()
            } else {
                repository.getCategories(mode).collectLatest { response ->
                    when (response) {
                        is Response.Success -> {
                            val categories = response.data
                            val defaultCategory = categories.firstOrNull { it.isEligible } ?: categories.firstOrNull()
                            _state.update {
                                it.copy(
                                    categoriesList = categories,
                                    selectedCategory = defaultCategory,
                                    isCategoriesLoading = false,
                                    isModeEligible = categories.any { c -> c.isEligible }
                                )
                            }
                            updateQuestionsCountAndLimits()
                        }
                        is Response.Error -> {
                            _state.update {
                                it.copy(
                                    isCategoriesLoading = false,
                                    isQuestionsLoading = false,
                                    loadingMode = null,
                                    responseState = ResponseState.Error(response.error)
                                )
                            }
                        }
                        Response.Loading -> {
                            _state.update { it.copy(isCategoriesLoading = true) }
                        }
                    }
                }
            }
        }
    }

    private suspend fun checkTranslationEligibility() {
        val seenQuestions = scoreManager.getScore().seenQuestions
        val questionsResp = repository.getQuestions(QuizMode.TranslationMode, null).first { it !is Response.Loading }

        if (questionsResp is Response.Success) {
            val allQuestions = questionsResp.data
            val answeredCount = allQuestions.sumOf { q ->
                seenQuestions.find { it.questionId == q.id }?.timesSeen ?: 0L
            }.toInt()

            val isEligible = answeredCount >= 10
            _state.update {
                it.copy(
                    isModeEligible = isEligible,
                    selectedCategory = null,
                    categoriesList = emptyList()
                )
            }
            updateQuestionsCountAndLimits()
        } else if (questionsResp is Response.Error) {
            _state.update {
                it.copy(
                    isQuestionsLoading = false,
                    loadingMode = null,
                    responseState = ResponseState.Error(questionsResp.error)
                )
            }
        }
    }

    private fun updateQuestionsCountAndLimits() {
        val currentState = _state.value
        val mode = currentState.selectedMode
        val categoryId = currentState.selectedCategory?.id
        val criterion = currentState.selectedCriterion

        _state.update { it.copy(isQuestionsLoading = true) }

        viewModelScope.launch {
            getRevisionsQuestions(mode, categoryId, criterion, limit = null).collectLatest { response ->
                if (response is Response.Success) {
                    val count = response.data.size
                    val limits = calculateAvailableLimits(count)
                    val newLimit = if (currentState.selectedLimit == null || limits.contains(currentState.selectedLimit)) {
                        currentState.selectedLimit
                    } else {
                        limits.firstOrNull()
                    }

                    _state.update {
                        it.copy(
                            availableQuestionsCount = count,
                            availableLimits = limits,
                            selectedLimit = newLimit,
                            isEmptyState = count == 0,
                            isQuestionsLoading = false,
                            isConfigDialogVisible = if (it.loadingMode != null) true else it.isConfigDialogVisible,
                            loadingMode = null
                        )
                    }
                }
            }
        }
    }

    private fun calculateAvailableLimits(questionsCount: Int): List<Int?> {
        val baseLimits = listOf(10, 20, 50, 100)
        val filtered = baseLimits.filter { it <= questionsCount }
        return filtered + listOf(null)
    }
}
