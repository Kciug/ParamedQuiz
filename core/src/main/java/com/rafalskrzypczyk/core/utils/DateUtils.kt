package com.rafalskrzypczyk.core.utils

import java.util.Calendar
import java.util.Date

fun Date.toDateOnly() : Date {
    return Calendar.getInstance().apply {
        time = this@toDateOnly
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.time
}