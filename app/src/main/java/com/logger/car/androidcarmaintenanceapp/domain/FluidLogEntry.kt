package com.logger.car.androidcarmaintenanceapp.domain

import java.util.*

data class FluidLogEntry(
        override var entryDate: Date? = null,
        override var mileage: Int? = null,
        var level: Int = 0
): BaseLogEntry {
	override fun isValidLogEntry() = entryDate != null && mileage != null
}