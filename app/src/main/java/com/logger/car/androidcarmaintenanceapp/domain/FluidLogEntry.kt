package com.logger.car.androidcarmaintenanceapp.domain

import java.util.*

data class FluidLogEntry(
        override var entryDate: Date? = null,
        override var mileage: Int? = null,
        var level: Int? = null
): BaseLogEntry {
	override fun isValidLogEntry() = entryDate != null && mileage != null && level != null
}