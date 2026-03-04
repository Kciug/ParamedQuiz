package com.rafalskrzypczyk.cem_mode.domain.use_cases

import com.rafalskrzypczyk.cem_mode.domain.CemRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetUpdatedCemCategoriesUseCase @Inject constructor(
    private val repository: CemRepository
) {
    operator fun invoke(parentId: Long = 0): Flow<List<CemCategory>> =
        repository.getUpdatedCemCategories().map { list ->
            list.filter { it.parentCategoryID == parentId }
        }
}
