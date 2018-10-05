package com.logger.car.androidcarmaintenanceapp.domain

import java.util.*

interface BaseLogEntry {
    val entryDate: Date
    val mileage: Int
}