package com.logger.car.androidcarmaintenanceapp.domain

import java.util.*

interface BaseLogEntry {
	var entryDate: Date?
	var mileage: Int?

	fun isValidLogEntry(): Boolean
}