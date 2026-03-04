package com.rafalskrzypczyk.cem_mode.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.cem_mode.domain.CemRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetCemCategoryUseCase @Inject constructor(
    private val repository: CemRepository
) {
    operator fun invoke(categoryId: Long): Flow<Response<CemCategory>> =
        repository.getCemCategories().map { response ->
            when (response) {
                is Response.Success -> {
                    val category = response.data.find { it.id == categoryId }
                    if (category != null) {
                        Response.Success(category)
                    } else {
                        Response.Error("Category not found")
                    }
                }
                is Response.Error -> Response.Error(response.error)
                Response.Loading -> Response.Loading
            }
        }
}
