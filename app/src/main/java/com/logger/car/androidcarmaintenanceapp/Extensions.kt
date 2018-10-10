package com.logger.car.androidcarmaintenanceapp

import java.text.DateFormatSymbols
import java.util.*

fun Date.getMonthName(): String? {
    val monthInt = Calendar.getInstance().apply {
        time = this@getMonthName
    }.get(Calendar.MONTH)
    return DateFormatSymbols().shortMonths.getOrNull(monthInt)
}

fun Date.getDayOfMonth() = Calendar.getInstance().apply { time = this@getDayOfMonth }.get(Calendar.DAY_OF_MONTH)