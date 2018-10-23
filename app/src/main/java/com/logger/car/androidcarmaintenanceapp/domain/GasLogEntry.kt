package com.logger.car.androidcarmaintenanceapp.domain

import java.util.*

data class GasLogEntry(
        override var entryDate: Date? = null,
		override var mileage: Int? = null,
        var gallonsAdded: Double? = null
): BaseLogEntry {
	override fun isValidLogEntry() = entryDate != null && mileage != null && gallonsAdded != null
}