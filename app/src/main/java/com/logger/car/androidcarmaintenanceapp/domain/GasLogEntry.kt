package com.logger.car.androidcarmaintenanceapp.domain

import java.util.*

data class GasLogEntry(
        override val entryDate: Date,
        override val mileage: Int,
        val gallonsAdded: Int
): BaseLogEntry