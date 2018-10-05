package com.logger.car.androidcarmaintenanceapp

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.Vehicle
import java.util.*

class MainViewModel : ViewModel() {

    private val vehicles =
            listOf(Vehicle(
                    "Honda",
                    "Accord",
                    mutableListOf(FluidLogEntry(Calendar.getInstance().time, 127000, 80)),
                    mutableListOf(FluidLogEntry(Calendar.getInstance().time, 127000, 100)),
                    mutableListOf(GasLogEntry(Calendar.getInstance().time, 127000, 13.0))
            ), Vehicle(
                    "Toyota",
                    "Camry",
                    mutableListOf(FluidLogEntry(Calendar.getInstance().time, 50000, 30)),
                    mutableListOf(FluidLogEntry(Calendar.getInstance().time, 70000, 65)),
                    mutableListOf(GasLogEntry(Calendar.getInstance().time, 90000, 2.0))
            )
            )

    fun getVehicles() = vehicles
}