package com.logger.car.androidcarmaintenanceapp.databaseAccess

import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import java.util.*

class MockLogDao: LogDao {

    private val oilLevelLogs = mutableListOf(FluidLogEntry(Calendar.getInstance().time, 127000, 80))
    private val coolantLevelLogs = mutableListOf(FluidLogEntry(Calendar.getInstance().time, 127000, 80))
    private val gasAddedLogs = mutableListOf(GasLogEntry(Calendar.getInstance().time, 127000, 12))

    override fun getOilLogs() = oilLevelLogs

    override fun addOilLog(newLog: FluidLogEntry) { oilLevelLogs.add(newLog) }

    override fun getCoolantLogs() = coolantLevelLogs

    override fun addCoolantLog(newLog: FluidLogEntry) { coolantLevelLogs.add(newLog) }

    override fun getGasLogs() = gasAddedLogs

    override fun addGasLog(newLog: GasLogEntry) { gasAddedLogs.add(newLog) }
}