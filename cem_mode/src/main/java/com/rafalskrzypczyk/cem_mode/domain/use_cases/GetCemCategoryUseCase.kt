package com.rafalskrzypczyk.cem_mode.domain.use_cases

import com.rafalskrzypczyk.core.api_response.Response
import com.rafalskrzypczyk.core.error.AppError
import com.rafalskrzypczyk.core.error.ErrorLogger
import com.rafalskrzypczyk.core.error.report
import com.rafalskrzypczyk.cem_mode.domain.CemRepository
import com.rafalskrzypczyk.cem_mode.domain.models.CemCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val ORIGIN_GET_CEM_CATEGORY = "GetCemCategoryUseCase.invoke"

class GetCemCategoryUseCase @Inject constructor(
    private val repository: CemRepository,
    private val errorLogger: ErrorLogger
) {
    operator fun invoke(categoryId: Long): Flow<Response<CemCategory>> =
        repository.getCemCategories().map { response ->
            when (response) {
                is Response.Success -> {
                    val category = response.data.find { it.id == categoryId }
                    if (category != null) {
                        Response.Success(category)
                    } else {
                        Response.Error(errorLogger.report(ORIGIN_GET_CEM_CATEGORY, AppError.Data.NotFound))
                    }
                }
                is Response.Error -> response
                Response.Loading -> Response.Loading
            }
        }
}
