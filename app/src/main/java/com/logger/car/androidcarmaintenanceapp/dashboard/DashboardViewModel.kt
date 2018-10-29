package com.logger.car.androidcarmaintenanceapp.dashboard

import android.arch.lifecycle.ViewModel
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import com.logger.car.androidcarmaintenanceapp.repository.VehicleRepository

class DashboardViewModel: ViewModel() {

	private val vehicleRepository = VehicleRepository.instance
	var pendingGasEntry: GasLogEntry? = null
	var pendingOilEntry: FluidLogEntry? = null
	var pendingCoolantEntry: FluidLogEntry? = null

	fun getObservableVehicles() = vehicleRepository.vehicles

	//Return True if successfully updated, false otherwise
	fun addOilLogEntry(vehicleId: Int, entry: FluidLogEntry) =
			vehicleRepository.getVehiclebyId(vehicleId)?.oilLogs?.value?.add(0, entry) ?: false

	fun addCoolantLogEntry(vehicleId: Int, entry: FluidLogEntry) =
			vehicleRepository.getVehiclebyId(vehicleId)?.coolantLogs?.value?.add(0, entry) ?: false

	fun addGasEntry(vehicleId: Int, entry: GasLogEntry) =
			vehicleRepository.getVehiclebyId(vehicleId)?.gasLogs?.value?.add(0, entry) ?: false
}