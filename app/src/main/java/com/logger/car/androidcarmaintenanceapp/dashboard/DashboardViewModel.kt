package com.logger.car.androidcarmaintenanceapp.dashboard

import android.arch.lifecycle.ViewModel
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import com.logger.car.androidcarmaintenanceapp.repository.VehicleRepository
import java.util.*

class DashboardViewModel : ViewModel() {

	private val vehicleRepository = VehicleRepository.instance

	fun getObservableVehicles() = vehicleRepository.vehicles

	//Return True if successfully updated, false otherwise
	fun addOilLogEntry(vehicleId: Int, entry: FluidLogEntry) =
			vehicleRepository.getVehiclebyId(vehicleId)?.oilLogs?.value?.add(0, entry) ?: false

	fun addCoolantLogEntry(vehicleId: Int, entry: FluidLogEntry) =
			vehicleRepository.getVehiclebyId(vehicleId)?.coolantLogs?.value?.add(0, entry) ?: false

	fun addGasEntry(vehicleId: Int, entry: GasLogEntry) =
			vehicleRepository.getVehiclebyId(vehicleId)?.gasLogs?.value?.add(0, entry) ?: false

	fun getPendingGasEntry() = vehicleRepository.pendingGasEntry

	fun getPendingOilEntry() = vehicleRepository.pendingOilEntry

	fun getPendingCoolantEntry() = vehicleRepository.pendingCoolantEntry

	fun setPendingGasEntry(entry: GasLogEntry) {
		vehicleRepository.pendingGasEntry = entry
	}

	fun setPendingOilEntry(entry: FluidLogEntry) {
		vehicleRepository.pendingOilEntry = entry
	}

	fun setPendingCoolantEntry(entry: FluidLogEntry) {
		vehicleRepository.pendingCoolantEntry = entry
	}

	fun addPendingEntries(vehicleId: Int) {
		vehicleRepository.pendingGasEntry?.let { addGasEntry(vehicleId, it) }
		vehicleRepository.pendingOilEntry?.let { addOilLogEntry(vehicleId, it) }
		vehicleRepository.pendingCoolantEntry?.let { addCoolantLogEntry(vehicleId, it) }
		clearPendingEntries()
	}

	fun clearPendingEntries() {
		vehicleRepository.pendingGasEntry = null
		vehicleRepository.pendingOilEntry = null
		vehicleRepository.pendingCoolantEntry = null
	}

	fun getEstimatedOilLevel(vehicleId: Int) = (vehicleRepository.getVehiclebyId(vehicleId)?.oilLogs?.value)?.let { logs ->
		if (!logs.isEmpty()) getEstimatedLevelByDate(logs) else null
	}

	fun getEstimatedCoolantLevel(vehicleId: Int) = (vehicleRepository.getVehiclebyId(vehicleId)?.coolantLogs?.value)?.let { logs ->
		if (!logs.isEmpty()) getEstimatedLevelByDate(logs) else null
	}

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