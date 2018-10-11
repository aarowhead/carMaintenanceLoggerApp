package com.logger.car.androidcarmaintenanceapp.domain

import java.util.*

data class FluidLogEntry(
        override val entryDate: Date,
        override val mileage: Int,
        val level: Int,
		val fluidType: FluidType
): BaseLogEntry {

	enum class FluidType {OIL, COOLANT}
}