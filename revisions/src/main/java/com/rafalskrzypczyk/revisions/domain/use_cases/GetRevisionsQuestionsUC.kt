package com.rafalskrzypczyk.revisions.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.utils.QuizMode
import com.rafalskrzypczyk.revisions.domain.RevisionsRepository
import com.rafalskrzypczyk.revisions.domain.models.RevisionCriterion
import com.rafalskrzypczyk.revisions.domain.models.RevisionQuestion
import com.rafalskrzypczyk.score.domain.ScoreManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetRevisionsQuestionsUC @Inject constructor(
    private val repository: RevisionsRepository,
    private val scoreManager: ScoreManager
) {
    operator fun invoke(
        mode: QuizMode,
        categoryId: Long?,
        criterion: RevisionCriterion,
        limit: Int?
    ): Flow<Response<List<RevisionQuestion>>> {
        val seenQuestions = scoreManager.getScore().seenQuestions

        return repository.getQuestions(mode, categoryId).map { response ->
            when (response) {
                is Response.Success -> {
                    val answeredQuestions = response.data.filter { question ->
                        val annotation = seenQuestions.find { it.questionId == question.id }
                        annotation != null && annotation.timesSeen > 0
                    }

                    val questionRatios = answeredQuestions.map { question ->
                        val annotation = seenQuestions.first { it.questionId == question.id }
                        val ratio = annotation.timesCorrect.toFloat() / annotation.timesSeen.toFloat()
                        question to ratio
                    }

                    val filteredAndSorted = when (criterion) {
                        RevisionCriterion.WORST -> {
                            questionRatios.sortedBy { it.second }.map { it.first }
                        }
                        RevisionCriterion.BEST -> {
                            questionRatios.sortedByDescending { it.second }.map { it.first }
                        }
                        RevisionCriterion.UNDER_50 -> {
                            questionRatios.filter { it.second < 0.5f }.sortedBy { it.second }.map { it.first }
                        }
                    }

                    val limited = if (limit != null && limit > 0) {
                        filteredAndSorted.take(limit)
                    } else {
                        filteredAndSorted
                    }

                    Response.Success(limited)
                }
                is Response.Error -> Response.Error(response.error)
                Response.Loading -> Response.Loading
            }
        }
    }
}
