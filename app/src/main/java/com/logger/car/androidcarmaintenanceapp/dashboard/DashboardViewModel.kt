package com.logger.car.androidcarmaintenanceapp.dashboard

import android.arch.lifecycle.ViewModel
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import com.logger.car.androidcarmaintenanceapp.repository.VehicleRepository

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
}