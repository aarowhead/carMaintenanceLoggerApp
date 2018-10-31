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

	fun hasRecentOilLog() = getTimeSinceLastOilCheck() ?: 0 <= 14

	fun hasRecentCoolantLog() = getTimeSinceLastCoolantCheck() ?: 0 <= 14

	fun getTimeSinceLastOilCheck() = oilLogs.value?.first()?.entryDate?.let { daysBetween(it, Calendar.getInstance().time) }

	fun getTimeSinceLastCoolantCheck() = coolantLogs.value?.first()?.entryDate?.let { daysBetween(it, Calendar.getInstance().time) }

	fun getEstimatedOilLevel() = oilLogs.value?.let { logs -> if (!logs.isEmpty()) getEstimatedLevelByDate(logs) else null }

	fun getEstimatedCoolantLevel() = coolantLogs.value?.let { logs ->	if (!logs.isEmpty()) getEstimatedLevelByDate(logs) else null }

	private fun getEstimatedLevelByDate(list: List<FluidLogEntry>) = list.first().entryDate?.let {
		(list.first().level) - daysBetween(Calendar.getInstance().time, it) * getAverageLossPerDay(list)
	} ?: 0

	private fun getAverageLossPerDay(list: List<FluidLogEntry>) = list.first().entryDate?.let { first ->
		list.last().entryDate?.let { last ->
			getTotalLoss(list) / daysBetween(first, last).let { if (it == 0L) 1L else it }
		}
	} ?: 0

	private fun getTotalLoss(list: List<FluidLogEntry>): Int {
		var previousLevel: Int? = null
		var totalLoss = 0
		list.forEach { entry ->
			previousLevel?.let {
				val difference = it - (entry.level)
				if (difference > 0) {
					totalLoss += difference
				}
			}
			previousLevel = entry.level
		}
		return totalLoss
	}

	private fun daysBetween(d1: Date, d2: Date) = ((d2.time - d1.time) / (1000 * 60 * 60 * 24))
}