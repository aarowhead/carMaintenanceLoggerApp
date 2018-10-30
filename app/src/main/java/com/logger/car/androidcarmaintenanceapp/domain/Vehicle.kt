package com.logger.car.androidcarmaintenanceapp.domain

import android.arch.lifecycle.MutableLiveData
import java.util.*
import kotlin.math.roundToInt

data class Vehicle(
		val id: Int,
        val make: String,
        val model: String,
		val tankSize: Int,
		val oilLogs: MutableLiveData<MutableList<FluidLogEntry>> = MutableLiveData(),
        val coolantLogs: MutableLiveData<MutableList<FluidLogEntry>> = MutableLiveData(),
        val gasLogs: MutableLiveData<MutableList<GasLogEntry>> = MutableLiveData()
) {
	fun getAverageGasMileage(): Int {
		var totalMiles = 0
		var totalGallons = 0.0
		var prevMiles: Int? = null
		gasLogs.value?.forEach { logEntry ->
			logEntry.mileage?.let {mileage ->
				prevMiles?.let {
					totalMiles += it - mileage
				}
			}
			prevMiles = logEntry.mileage
			logEntry.gallonsAdded?.let{ totalGallons += it }
		}
		return (totalMiles / totalGallons).roundToInt()
	}
}