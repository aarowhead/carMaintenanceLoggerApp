package com.logger.car.androidcarmaintenanceapp.repository

import android.arch.lifecycle.MutableLiveData
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.Vehicle
import java.text.SimpleDateFormat
import java.util.*

class VehicleRepository private constructor() {
	private val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)
	private object Holder { val INSTANCE = VehicleRepository() }

	companion object {
		val instance: VehicleRepository by lazy { Holder.INSTANCE }
	}

	val vehicles = MutableLiveData<MutableList<Vehicle>>()

	init {
		vehicles.value = mutableListOf(
				Vehicle(
						1,
						"Honda",
						"Accord"
				).apply {
					oilLogs.value = mutableListOf(
							FluidLogEntry(dateFormat.parse("06/10/2018"), 127000, 80),
							FluidLogEntry(dateFormat.parse("01/10/2018"), 126500, 95),
							FluidLogEntry(dateFormat.parse("20/09/2018"), 126000, 100),
							FluidLogEntry(dateFormat.parse("06/09/2018"), 125500, 25),
							FluidLogEntry(dateFormat.parse("22/08/2018"), 124000, 5),
							FluidLogEntry(dateFormat.parse("08/08/2018"), 123500, 100),
							FluidLogEntry(dateFormat.parse("18/07/2018"), 120000, 80),
							FluidLogEntry(dateFormat.parse("04/07/2018"), 118500, 50),
							FluidLogEntry(dateFormat.parse("20/06/2018"), 117000, 40),
							FluidLogEntry(dateFormat.parse("02/06/2018"), 116500, 10),
							FluidLogEntry(dateFormat.parse("15/05/2018"), 115000, 100),
							FluidLogEntry(dateFormat.parse("01/05/2018"), 114000, 85)
					)
					coolantLogs.value = mutableListOf(
							FluidLogEntry(dateFormat.parse("06/10/2018"), 127000, 100),
							FluidLogEntry(dateFormat.parse("01/10/2018"), 126500, 80),
							FluidLogEntry(dateFormat.parse("20/09/2018"), 126000, 55),
							FluidLogEntry(dateFormat.parse("06/09/2018"), 125200, 30)
					)
					gasLogs.value = mutableListOf(GasLogEntry(Calendar.getInstance().time, 127000, 13.0))
				},
				Vehicle(
						2,
						"Toyota",
						"Camry"
				)
		)
	}

	fun getVehiclebyId(id: Int) = vehicles.value?.find { it.id == id }
}