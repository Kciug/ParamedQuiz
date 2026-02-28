package com.rafalskrzypczyk.core.utils

import android.app.Activity
import android.content.Context
import com.google.android.play.core.review.ReviewManagerFactory
import javax.inject.Inject

class InAppReviewManager @Inject constructor(
    context: Context
) {
    private val manager = ReviewManagerFactory.create(context)

    fun launchReviewFlow(activity: Activity) {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(activity, reviewInfo)
            }
        }
    }
}
