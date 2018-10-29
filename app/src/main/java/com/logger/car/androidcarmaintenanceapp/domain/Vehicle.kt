package com.logger.car.androidcarmaintenanceapp.domain

import android.arch.lifecycle.MutableLiveData
import java.util.*

data class Vehicle(
		val id: Int,
        val make: String,
        val model: String,
		val tankSize: Int,
		val oilLogs: MutableLiveData<MutableList<FluidLogEntry>> = MutableLiveData(),
        val coolantLogs: MutableLiveData<MutableList<FluidLogEntry>> = MutableLiveData(),
        val gasLogs: MutableLiveData<MutableList<GasLogEntry>> = MutableLiveData()
)