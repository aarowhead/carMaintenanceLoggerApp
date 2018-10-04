package com.logger.car.androidcarmaintenanceapp.databaseAccess

import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry

interface LogDao {
    fun getOilLogs(): List<FluidLogEntry>
    fun addOilLog(newLog: FluidLogEntry)
    fun getCoolantLogs(): List<FluidLogEntry>
    fun addCoolantLog(newLog: FluidLogEntry)
    fun getGasLogs(): List<GasLogEntry>
    fun addGasLog(newLog: GasLogEntry)
}