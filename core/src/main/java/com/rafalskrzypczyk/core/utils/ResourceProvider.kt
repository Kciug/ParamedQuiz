package com.rafalskrzypczyk.core.utils

import android.content.Context
import android.graphics.drawable.Drawable
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ResourceProvider @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun getString(resId: Int): String = context.getString(resId)

    fun getColor(resId: Int): Int = context.getColor(resId)

    fun getDrawable(resId: Int): Drawable? = context.getDrawable(resId)
}