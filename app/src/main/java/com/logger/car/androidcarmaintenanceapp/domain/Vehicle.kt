package com.logger.car.androidcarmaintenanceapp.domain

import android.arch.lifecycle.MutableLiveData
import java.util.*

data class Vehicle(
        val make: String,
        val model: String,
        val oilLogs: MutableLiveData<MutableList<FluidLogEntry>> = MutableLiveData(),
        val coolantLogs: MutableLiveData<MutableList<FluidLogEntry>> = MutableLiveData(),
        val gasLogs: MutableLiveData<MutableList<GasLogEntry>> = MutableLiveData()
)