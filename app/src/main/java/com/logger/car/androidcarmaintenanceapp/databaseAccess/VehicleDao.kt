package com.logger.car.androidcarmaintenanceapp.databaseAccess

import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.Vehicle

interface VehicleDao {
    fun addVehicle(vehicle: Vehicle)
    fun getAllVehicles(): List<Vehicle>
}