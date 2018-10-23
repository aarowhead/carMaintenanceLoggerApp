package com.logger.car.androidcarmaintenanceapp.dashboard

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.checkup.CheckupMainFragment
import com.logger.car.androidcarmaintenanceapp.domain.*
import com.logger.car.androidcarmaintenanceapp.logs.LogsActivity
import com.logger.car.androidcarmaintenanceapp.showAddEntryDialog
import kotlinx.android.synthetic.main.dashboard_activity.*
import kotlinx.android.synthetic.main.dashboard_cardview.view.*
import kotlinx.android.synthetic.main.gas_entry_dialog_fragment.view.*
import kotlinx.android.synthetic.main.level_indicator_layout.view.*
import kotlinx.android.synthetic.main.set_level_dialog_fragment.view.*
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {
	companion object {
		private const val CRITICAL_THRESHOLD = 15
		private const val WARNING_THRESHOLD = 35
		//TODO: See if this should be stored somewhere else
		const val MAINTENANCE_TYPE = "MAINTENANCE_TYPE"
		const val VEHICLE_ID = "VEHICLE_ID"
	}

	private val vehicleAdapter = VehicleAdapter(mutableListOf())
	private lateinit var model: DashboardViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dashboard_activity)
		model = ViewModelProviders.of(this@DashboardActivity).get(DashboardViewModel::class.java)

		cars_recycler.run {
			this.adapter = vehicleAdapter
			layoutManager = LinearLayoutManager(this@DashboardActivity)
		}
		model.getObservableVehicles().observe(this, Observer {
			vehicleAdapter.vehicles = it
			vehicleAdapter.notifyDataSetChanged()
		})
	}

	inner class VehicleAdapter(var vehicles: List<Vehicle>?) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
				VehicleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dashboard_cardview, parent, false))

		override fun getItemCount() = vehicles?.size ?: 0

		override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
			vehicles?.get(position)?.let { vehicle ->
				holder.itemView.run {
					fun displayDetailLayout(button: AppCompatButton, logList: List<BaseLogEntry>?, type: MaintenanceType) {
						gas_button.isSelected = false
						oil_button.isSelected = false
						coolant_button.isSelected = false
						button.isSelected = true
						detail_layout.visibility = View.VISIBLE
						info_text.text = logList?.firstOrNull()?.let { "${SimpleDateFormat("MMMM d").format(it.entryDate)} at ${it.mileage} miles." } ?: "No logs for selected item."

						view_logs_button.setOnClickListener {
							startActivity(
									Intent(this@DashboardActivity, LogsActivity::class.java).apply {
										putExtra(VEHICLE_ID, vehicle.id)
										putExtra(MAINTENANCE_TYPE, type)
									}
							)
						}
						add_entry_button.setOnClickListener {
							this@DashboardActivity.showAddEntryDialog(layoutInflater.inflate(if (type == MaintenanceType.GAS) R.layout.gas_entry_dialog_fragment else R.layout.set_level_dialog_fragment, null).apply {
								//TODO: figure out a smart way for it to set the progress
								level_indicator.progress = 50
							}) {
								when (type) {
									MaintenanceType.GAS -> {
										model.addGasEntry(
												vehicle.id,
												GasLogEntry(
														//TODO: figure out how to read in date
														Calendar.getInstance().time,
														it.mileage_edit_text_gas.text.toString().toInt(),
														it.gallons_added_edit_text.text.toString().toDouble()
												)
										)
									}
									//TODO: get rid of this duplicate code
									MaintenanceType.OIL -> {
										model.addOilLogEntry(
												vehicle.id,
												FluidLogEntry(
														Calendar.getInstance().time,
														it.mileage_edit_text.text.toString().toInt(),
														it.level_indicator.progress
												)
										)
									}
									MaintenanceType.COOLANT -> {
										model.addCoolantLogEntry(
												vehicle.id,
												FluidLogEntry(
														Calendar.getInstance().time,
														it.mileage_edit_text.text.toString().toInt(),
														it.level_indicator.progress
												)
										)
									}
								}
								//TODO: Change this to check boolean return to see if it was successfully added
								Toast.makeText(context, "Log successfully added", Toast.LENGTH_LONG).show()
							}
						}
					}

					fun hideDetailLayout(button: AppCompatButton) {
						button.isSelected = false
						detail_layout.visibility = View.GONE
					}

					fun getButtonStyleFromLevel(level: Int? = null) = getDrawable(when {
						level == null -> R.drawable.good_button
						level <= CRITICAL_THRESHOLD -> R.drawable.critical_button
						level <= WARNING_THRESHOLD -> R.drawable.warning_button
						else -> R.drawable.good_button
					})

					detail_layout.visibility = View.GONE
					car_name.text = "${vehicle.make} ${vehicle.model}"
					status.text = "Testing"
					checkup_button.setOnClickListener { _ ->
						CheckupMainFragment.newInstance(vehicle.id).apply {
							onFinishedCallback = object : CheckupMainFragment.OnCheckupFinishedListener {
								override fun onFinished(vehicleId: Int) {
									vehicleAdapter.vehicles?.indexOfFirst { it.id == vehicleId }?.let { vehicleAdapter.notifyItemChanged(it) }
								}
							}
						}.show(supportFragmentManager, "TAG")
					}
					gas_button.run {
						text = "18.1 MPG"
						setOnClickListener {
							if (!isSelected) {
								displayDetailLayout(this, vehicle.gasLogs.value as List<BaseLogEntry>?, MaintenanceType.GAS)
							} else hideDetailLayout(this)
						}
//						TODO("Figure out how to calculate mileage")
					}

					oil_button.run {
						vehicle.oilLogs.value?.firstOrNull()?.level?.let { oilLevel ->
							text = "$oilLevel%"
							background = getButtonStyleFromLevel(oilLevel)
						} ?: run {
							text = "???"
							background = getButtonStyleFromLevel()
						}
						setOnClickListener { if (!isSelected) displayDetailLayout(this, vehicle.oilLogs.value as List<BaseLogEntry>?, MaintenanceType.OIL) else hideDetailLayout(this) }
					}
					coolant_button.run {
						vehicle.coolantLogs.value?.firstOrNull()?.level?.let { coolantLevel ->
							text = "$coolantLevel%"
							background = getButtonStyleFromLevel(coolantLevel)
						} ?: run {
							text = "???"
							background = getButtonStyleFromLevel()
						}
						setOnClickListener {
							if (!isSelected) displayDetailLayout(this, vehicle.coolantLogs.value as List<BaseLogEntry>?, MaintenanceType.COOLANT) else hideDetailLayout(this)
						}
					}
				}
			}
		}

		inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	}
}


