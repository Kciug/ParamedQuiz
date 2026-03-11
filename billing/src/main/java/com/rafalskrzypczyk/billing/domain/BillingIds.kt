package com.rafalskrzypczyk.billing.domain

object BillingIds {
    const val ID_PREFIX = "mediquiz_"

    const val ID_FULL_PACKAGE = "mediquiz_full_package"
    const val ID_TRANSLATION_MODE = "mediquiz_translations_mode"
    const val ID_SWIPE_MODE = "mediquiz_swipe_mode"
    const val ID_AD_FREE = "mediquiz_adfree"
}

fun getCategoryBillingId(categoryId: Long): String {
    return BillingIds.ID_PREFIX + categoryId
}
