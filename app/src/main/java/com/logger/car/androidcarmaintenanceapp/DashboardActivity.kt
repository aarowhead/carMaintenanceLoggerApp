package com.logger.car.androidcarmaintenanceapp

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logger.car.androidcarmaintenanceapp.checkup.CheckupMainFragment
import com.logger.car.androidcarmaintenanceapp.domain.BaseLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.Vehicle
import kotlinx.android.synthetic.main.dashboard_activity.*
import kotlinx.android.synthetic.main.dashboard_cardview.view.*
import java.text.SimpleDateFormat

class DashboardActivity : AppCompatActivity() {
	companion object {
		private const val CRITICAL_THRESHOLD = 15
		private const val WARNING_THRESHOLD = 35
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.dashboard_activity)

		cars_recycler.run {
			adapter = VehicleAdapter(ViewModelProviders.of(this@DashboardActivity).get(MainViewModel::class.java).getVehicles())
			layoutManager = LinearLayoutManager(this@DashboardActivity)
		}
	}

	inner class VehicleAdapter(var vehicles: List<Vehicle>?) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
				VehicleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dashboard_cardview, parent, false))

		override fun getItemCount() = vehicles?.size ?: 0

		override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
			vehicles?.get(position)?.let { vehicle ->
				holder.itemView.run {
					fun displayDetailLayout(button: AppCompatButton, logList: List<BaseLogEntry>) {
						gas_button.isSelected = false
						oil_button.isSelected = false
						coolant_button.isSelected = false
						button.isSelected = true
						detail_layout.visibility = View.VISIBLE
						info_text.text = logList.lastOrNull()?.let { "${SimpleDateFormat("MMMM d").format(it.entryDate)} at ${it.mileage} miles." } ?: "No logs for selected item."

						view_logs_button.setOnClickListener { TODO("Start activity once it's been added") }
						add_entry_button.setOnClickListener { TODO("Start activity once it's been added") }
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
					checkup_button.setOnClickListener { CheckupMainFragment().show(supportFragmentManager, "TAG") }
					gas_button.run {
						text = "18.1 MPG"
						setOnClickListener { if (!isSelected) {
							displayDetailLayout(this, vehicle.gasLogs)
						} else hideDetailLayout(this) }
//						TODO("Figure out how to calculate mileage")
					}
					oil_button.run {
						vehicle.oilLogs.lastOrNull()?.level?.let { oilLevel ->
							text = "$oilLevel%"
							background = getButtonStyleFromLevel(oilLevel)
						} ?: run {
							text = "???"
							background = getButtonStyleFromLevel()
						}
						setOnClickListener { if (!isSelected) displayDetailLayout(this, vehicle.oilLogs) else hideDetailLayout(this) }
					}
					coolant_button.run {
						vehicle.coolantLogs.lastOrNull()?.level?.let { coolantLevel ->
							text = "$coolantLevel%"
							background = getButtonStyleFromLevel(coolantLevel)
						} ?: run {
							text = "???"
							background = getButtonStyleFromLevel()
						}
						setOnClickListener { if (!isSelected) displayDetailLayout(this, vehicle.coolantLogs) else hideDetailLayout(this) }
					}
				}
			}
		}

		inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	}
}


