package com.rafalskrzypczyk.revisions.data

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.domain.RevisionsRepository
import com.rafalskrzypczyk.revisions.domain.models.RevisionCategory
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion
import com.rafalskrzypczyk.main_mode.domain.MainModeRepository
import com.rafalskrzypczyk.cem_mode.domain.CemRepository
import com.rafalskrzypczyk.translation_mode.domain.repository.TranslationRepository
import com.rafalskrzypczyk.score.domain.ScoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RevisionsRepositoryImpl @Inject constructor(
    private val mainModeRepository: MainModeRepository,
    private val cemRepository: CemRepository,
    private val translationRepository: TranslationRepository,
    private val scoreManager: ScoreManager
) : RevisionsRepository {

    override fun getCategories(mode: QuizMode): Flow<Response<List<RevisionCategory>>> {
        val seenQuestions = scoreManager.getScore().seenQuestions
        return when (mode) {
            QuizMode.MainMode -> {
                combine(
                    mainModeRepository.getAllCategories(),
                    mainModeRepository.getAllQuestions()
                ) { categoriesResp, questionsResp ->
                    if (categoriesResp is Response.Success && questionsResp is Response.Success) {
                        val allQuestions = questionsResp.data
                        val mapped = categoriesResp.data.map { category ->
                            val categoryQuestions = allQuestions.filter { it.assignedCategoriesIds.contains(category.id) }
                            val answeredCount = categoryQuestions.sumOf { q ->
                                seenQuestions.find { it.questionId == q.id }?.timesSeen ?: 0L
                            }.toInt()
                            RevisionCategory(
                                id = category.id,
                                title = category.title,
                                mode = QuizMode.MainMode,
                                totalQuestionsCount = category.questionsCount,
                                answeredQuestionsCount = answeredCount,
                                isEligible = answeredCount >= 10
                            )
                        }
                        Response.Success(mapped)
                    } else if (categoriesResp is Response.Error) {
                        Response.Error(categoriesResp.error)
                    } else if (questionsResp is Response.Error) {
                        Response.Error(questionsResp.error)
                    } else {
                        Response.Loading
                    }
                }
            }
            QuizMode.CemMode -> {
                combine(
                    cemRepository.getCemCategories(),
                    cemRepository.getAllCemQuestions()
                ) { categoriesResp, questionsResp ->
                    if (categoriesResp is Response.Success && questionsResp is Response.Success) {
                        val allQuestions = questionsResp.data
                        val leafCategories = categoriesResp.data.filter { it.subcategoryIDs.isEmpty() }
                        val mapped = leafCategories.map { category ->
                            val categoryQuestions = allQuestions.filter { it.id in category.questionIDs }
                            val answeredCount = categoryQuestions.sumOf { q ->
                                seenQuestions.find { it.questionId == q.id }?.timesSeen ?: 0L
                            }.toInt()
                            RevisionCategory(
                                id = category.id,
                                title = category.title,
                                mode = QuizMode.CemMode,
                                totalQuestionsCount = category.questionsCount.toInt(),
                                answeredQuestionsCount = answeredCount,
                                isEligible = answeredCount >= 10
                            )
                        }
                        Response.Success(mapped)
                    } else if (categoriesResp is Response.Error) {
                        Response.Error(categoriesResp.error)
                    } else if (questionsResp is Response.Error) {
                        Response.Error(questionsResp.error)
                    } else {
                        Response.Loading
                    }
                }
            }
            else -> flowOf(Response.Success(emptyList()))
        }
    }

    override fun getQuestions(mode: QuizMode, categoryId: Long?): Flow<Response<List<RevisionQuestion>>> {
        return when (mode) {
            QuizMode.MainMode -> {
                mainModeRepository.getAllQuestions().map { resp ->
                    when (resp) {
                        is Response.Success -> {
                            val filtered = if (categoryId != null) {
                                resp.data.filter { it.assignedCategoriesIds.contains(categoryId) }
                            } else {
                                resp.data
                            }
                            Response.Success(filtered.map { RevisionQuestion.Main(it) })
                        }
                        is Response.Error -> Response.Error(resp.error)
                        Response.Loading -> Response.Loading
                    }
                }
            }
            QuizMode.CemMode -> {
                combine(
                    cemRepository.getCemCategories(),
                    cemRepository.getAllCemQuestions()
                ) { categoriesResp, questionsResp ->
                    if (categoriesResp is Response.Success && questionsResp is Response.Success) {
                        val allQuestions = questionsResp.data
                        val filtered = if (categoryId != null) {
                            val category = categoriesResp.data.find { it.id == categoryId }
                            if (category != null) {
                                allQuestions.filter { it.id in category.questionIDs }
                            } else {
                                emptyList()
                            }
                        } else {
                            allQuestions
                        }
                        Response.Success(filtered.map { RevisionQuestion.Cem(it) })
                    } else if (categoriesResp is Response.Error) {
                        Response.Error(categoriesResp.error)
                    } else if (questionsResp is Response.Error) {
                        Response.Error(questionsResp.error)
                    } else {
                        Response.Loading
                    }
                }
            }
            QuizMode.TranslationMode -> {
                translationRepository.getTranslationQuestions().map { resp ->
                    when (resp) {
                        is Response.Success -> {
                            Response.Success(resp.data.map { RevisionQuestion.Translation(it) })
                        }
                        is Response.Error -> Response.Error(resp.error)
                        Response.Loading -> Response.Loading
                    }
                }
            }
            else -> flowOf(Response.Success(emptyList()))
        }
    }
}
