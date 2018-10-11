package com.logger.car.androidcarmaintenanceapp

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.logger.car.androidcarmaintenanceapp.domain.BaseLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.Vehicle
import com.logger.car.androidcarmaintenanceapp.logs.LogsActivity
import kotlinx.android.synthetic.main.dashboard_activity.*
import kotlinx.android.synthetic.main.dashboard_cardview.view.*
import kotlinx.android.synthetic.main.level_indicator_layout.view.*
import java.text.SimpleDateFormat

class DashboardActivity : AppCompatActivity() {
	companion object {
		private const val CRITICAL_THRESHOLD = 15
		private const val WARNING_THRESHOLD = 35
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dashboard_activity)
		val model = ViewModelProviders.of(this@DashboardActivity).get(MainViewModel::class.java)
		val adapter = VehicleAdapter(mutableListOf())

		cars_recycler.run {
			this.adapter = adapter
			layoutManager = LinearLayoutManager(this@DashboardActivity)
		}
		model.vehicles.observe(this, Observer {
			adapter.vehicles = it
			adapter.notifyDataSetChanged()
		})
	}

	inner class VehicleAdapter(var vehicles: List<Vehicle>?) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
				VehicleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dashboard_cardview, parent, false))

		override fun getItemCount() = vehicles?.size ?: 0

		override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
			vehicles?.get(position)?.let { vehicle ->
				holder.itemView.run {
					fun displayDetailLayout(button: AppCompatButton, logList: List<BaseLogEntry>?) {
						gas_button.isSelected = false
						oil_button.isSelected = false
						coolant_button.isSelected = false
						button.isSelected = true
						detail_layout.visibility = View.VISIBLE
						info_text.text = logList?.lastOrNull()?.let { "${SimpleDateFormat("MMMM d").format(it.entryDate)} at ${it.mileage} miles." } ?: "No logs for selected item."

						view_logs_button.setOnClickListener { startActivity(Intent(this@DashboardActivity, LogsActivity::class.java)) }
						add_entry_button.setOnClickListener { this@DashboardActivity.showAddEntryDialog(layoutInflater.inflate(R.layout.set_level_dialog_fragment, null).apply {
							//TODO: figure out a smart way for it to set the progress
							level_indicator.progress = 50
						}) {
							//TODO: Make this actually save the logs. The main problem is that the logs activity has a different view model than the Dashboard activity so updating one won't update the other
							Toast.makeText(context, "Should save but not implemented yet...Sorry", Toast.LENGTH_LONG).show()
						} }
					}

					fun hideDetailLayout(button: AppCompatButton) {
						button.isSelected = false
						detail_layout.visibility = View.GONE
					}

					fun getButtonStyleFromLevel(level: Int? = null) =  getDrawable(when {
						level == null -> R.drawable.good_button
						level <= CRITICAL_THRESHOLD -> R.drawable.critical_button
						level <= WARNING_THRESHOLD -> R.drawable.warning_button
						else -> R.drawable.good_button
					})

					detail_layout.visibility = View.GONE
					car_name.text = "${vehicle.make} ${vehicle.model}"
					status.text = "Testing"
					checkup_button.setOnClickListener { TODO("start checkup once it has been added to the app") }
					gas_button.run {
						text = "18.1 MPG"
						setOnClickListener { if (!isSelected) {
							displayDetailLayout(this, vehicle.gasLogs.value as List<BaseLogEntry>?)
						} else hideDetailLayout(this) }
//						TODO("Figure out how to calculate mileage")
					}
					oil_button.run {
						vehicle.oilLogs.value?.lastOrNull()?.level?.let { oilLevel ->
							text = "$oilLevel%"
							background = getButtonStyleFromLevel(oilLevel)
						} ?: run {
							text = "???"
							background = getButtonStyleFromLevel()
						}
						setOnClickListener { if (!isSelected) displayDetailLayout(this, vehicle.oilLogs.value as List<BaseLogEntry>?) else hideDetailLayout(this) }
					}
					coolant_button.run {
						vehicle.coolantLogs.value?.lastOrNull()?.level?.let { coolantLevel ->
							text = "$coolantLevel%"
							background = getButtonStyleFromLevel(coolantLevel)
						} ?: run {
							text = "???"
							background = getButtonStyleFromLevel()
						}
						setOnClickListener { if (!isSelected) displayDetailLayout(this, vehicle.coolantLogs.value as List<BaseLogEntry>?) else hideDetailLayout(this) }
					}
				}
			}
		}

		inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	}
}


