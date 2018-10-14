package com.logger.car.androidcarmaintenanceapp.logs

import android.arch.lifecycle.ViewModel
import com.logger.car.androidcarmaintenanceapp.domain.Vehicle
import com.logger.car.androidcarmaintenanceapp.repository.VehicleRepository

class LogsViewModel: ViewModel() {

	private val vehicleRepository = VehicleRepository.instance
	var currentVehicle: Vehicle? = null

	fun setCurrentVehicle(id: Int) {
		currentVehicle = vehicleRepository.getVehiclebyId(id)
	}
}