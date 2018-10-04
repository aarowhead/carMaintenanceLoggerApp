package com.logger.car.androidcarmaintenanceapp.domain

data class Vehicle(
    val make: String,
    val model: String,
    val oilLogs: List<FluidLogEntry>,
    val coolantLogs: List<FluidLogEntry>,
    val gasLogs: List<GasLogEntry>
)