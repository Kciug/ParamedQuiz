package com.rafalskrzypczyk.cem_mode.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.cem_mode.domain.CemRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCemCategoriesUseCase @Inject constructor(
    private val repository: CemRepository
) {
    operator fun invoke(parentId: Long = 0): Flow<Response<List<CemCategory>>> =
        repository.getCemCategories().map { response ->
            when (response) {
                is Response.Success -> Response.Success(response.data.filter { it.parentCategoryID == parentId })
                is Response.Error -> Response.Error(response.error)
                Response.Loading -> Response.Loading
            }
        }
}
