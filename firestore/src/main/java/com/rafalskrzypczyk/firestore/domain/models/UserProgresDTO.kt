package com.rafalskrzypczyk.firestore.domain.models

data class UserProgresDTO(
    val userId: Long = -1,
    val quizProgress: List<CategoryProgressDTO> = emptyList(),
    val swipeQuizProgress: SwipeQuizProgressDTO? = null
)

data class CategoryProgressDTO(
    val categoryId: Long = -1,
    val succeedQuestionsIds: List<Long> = emptyList(),
    val failedQuestionsIds: List<Long> = emptyList()
)

data class SwipeQuizProgressDTO(
    val succeedQuestionsIds: List<Long> = emptyList(),
    val failedQuestionsIds: List<Long> = emptyList()
)
