package com.logger.car.androidcarmaintenanceapp.domain

interface BaseLogEntry {
    val entryDate: Date
    val mileage: Int
}