package com.logger.car.androidcarmaintenanceapp.dashboard

import android.app.DatePickerDialog
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.AppCompatButton
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
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
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

class DashboardActivity : AppCompatActivity() {
	companion object {
		//TODO: See if this should be stored somewhere else
		const val MAINTENANCE_TYPE = "MAINTENANCE_TYPE"
		const val VEHICLE_ID = "VEHICLE_ID"

		fun setLevelText(context: Context, percentage: Int, textView: TextView) {
			textView.text = "$percentage%"
			context.resources.run {
				textView.setTextColor(ContextCompat.getColor(context, when {
					percentage <= getInteger(R.integer.critical_threshold) -> R.color.critical
					percentage <= getInteger(R.integer.warning_threshold) -> R.color.warning
					else -> R.color.good
				}))
			}
		}
	}

	private val vehicleAdapter = VehicleAdapter(mutableListOf())
	private val sdf = SimpleDateFormat("MMMM dd, yyyy", Locale.US)
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
			vehicleAdapter.selectedButtons = arrayOfNulls(it?.size ?: 0)
		})
	}

	inner class VehicleAdapter(var vehicles: List<Vehicle>?) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {
		var selectedButtons: Array<MaintenanceType?>? = null

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
						selectedButtons?.set(position, type)
						detail_layout.visibility = View.VISIBLE
						info_text.text = logList?.firstOrNull()?.let { "${SimpleDateFormat("MMMM d").format(it.entryDate)} at ${NumberFormat.getNumberInstance(Locale.US).format(it.mileage)} miles." } ?: "No logs for selected item."

						view_logs_button.setOnClickListener {
							startActivity(
									Intent(this@DashboardActivity, LogsActivity::class.java).apply {
										putExtra(VEHICLE_ID, vehicle.id)
										putExtra(MAINTENANCE_TYPE, type)
									}
							)
						}
						add_entry_button.setOnClickListener {
							// TODO Fix crashes when data isn't properly selected in the dialog
							val fluidType = when {
								gas_button.isSelected -> "Gas"
								oil_button.isSelected -> "Oil"
								else -> "Coolant"
							}
							this@DashboardActivity.showAddEntryDialog(layoutInflater.inflate(if (type == MaintenanceType.GAS) R.layout.gas_entry_dialog_fragment else R.layout.set_level_dialog_fragment, null).apply {
								fun setUpMotorFluidLayout(startingLevel: Int) {
									date_edit_text_motor_fluid.setText(sdf.format(Calendar.getInstance().time))
									Calendar.getInstance().apply {
										date_edit_text_motor_fluid.setOnClickListener {
											DatePickerDialog(context, R.style.AppTheme_AlertDialog, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
												Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time.let {
													date_edit_text_motor_fluid.setText(sdf.format(it))
												}
											}, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)).show()
										}
									}
									level_indicator.progress = startingLevel
									level_indicator.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
										override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
											setLevelText(this@DashboardActivity, progress, level_text)
										}

										override fun onStartTrackingTouch(p0: SeekBar?) {}
										override fun onStopTrackingTouch(p0: SeekBar?) {}
									})
									setLevelText(this@DashboardActivity, level_indicator.progress, level_text)
								}
								when (type) {
									MaintenanceType.OIL -> setUpMotorFluidLayout(vehicle.getEstimatedOilLevel()?.toInt() ?: 0)
									MaintenanceType.COOLANT -> setUpMotorFluidLayout(vehicle.getEstimatedCoolantLevel()?.toInt() ?: 0)
									MaintenanceType.GAS -> {
										date_edit_text_gas.setText(sdf.format(Calendar.getInstance().time))
										Calendar.getInstance().apply {
											date_edit_text_gas.setOnClickListener {
												DatePickerDialog(context, R.style.AppTheme_AlertDialog, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
													Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time.let {
														date_edit_text_gas.setText(sdf.format(it))
													}
												}, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)).show()
											}
										}
									}
								}
							}, fluidType) {
								when (type) {
									MaintenanceType.GAS -> {
										GasLogEntry(
												sdf.parse(it.date_edit_text_gas.text.toString()),
												it.mileage_edit_text_gas.text.toString().toIntOrNull(),
												it.gallons_added_edit_text.text.toString().toDoubleOrNull()
										).let { log ->
											if (log.isValidLogEntry()) {
												model.addGasEntry(vehicle.id, log)
											} else {
												Toast.makeText(context, "Unable to save entry due to missing data.", Toast.LENGTH_LONG).show()
											}
										}
									}
									//TODO: get rid of this duplicate code
									MaintenanceType.OIL -> {
										FluidLogEntry(
												sdf.parse(it.date_edit_text_motor_fluid.text.toString()),
												it.mileage_edit_text.text.toString().toIntOrNull(),
												it.level_indicator.progress
										).let { log ->
											if (log.isValidLogEntry()) {
												model.addOilLogEntry(vehicle.id, log)
											} else {
												Toast.makeText(context, "Unable to save entry due to missing data.", Toast.LENGTH_LONG).show()
											}
										}
									}
									MaintenanceType.COOLANT -> {
										FluidLogEntry(
												sdf.parse(it.date_edit_text_motor_fluid.text.toString()),
												it.mileage_edit_text.text.toString().toIntOrNull(),
												it.level_indicator.progress
										).let { log ->
											if (log.isValidLogEntry()) {
												model.addCoolantLogEntry(vehicle.id, log)
											} else {
												Toast.makeText(context, "Unable to save entry due to missing data.", Toast.LENGTH_LONG).show()
											}
										}
									}
								}
								//TODO: Change this to check boolean return to see if it was successfully added
								Toast.makeText(context, "Log successfully added", Toast.LENGTH_LONG).show()
								vehicleAdapter.vehicles?.indexOf(vehicle)?.let {
									vehicleAdapter.notifyItemChanged(it)
									button.isSelected = true
									detail_layout.visibility = View.VISIBLE
									info_text.text = logList?.firstOrNull()?.let { "${SimpleDateFormat("MMMM d").format(it.entryDate)} at ${NumberFormat.getNumberInstance(Locale.US).format(it.mileage)} miles." } ?: "No logs for selected item."
								}
							}
						}
					}

					fun hideDetailLayout(button: AppCompatButton) {
						button.isSelected = false
						detail_layout.visibility = View.GONE
					}

					fun getButtonStyleFromLevel(level: Int? = null) = getDrawable(when {
						level == null -> R.drawable.good_button
						level <= resources.getInteger(R.integer.critical_threshold) -> R.drawable.critical_button
						level <= resources.getInteger(R.integer.warning_threshold) -> R.drawable.warning_button
						else -> R.drawable.good_button
					})

					car_name.text = "${vehicle.make} ${vehicle.model}"
					status.text = when {
						vehicle.getEstimatedOilLevel() ?: 100 <= resources.getInteger(R.integer.critical_threshold) -> "Your estimated oil level is critically low!  Be sure to fill it up soon!"
						vehicle.getEstimatedCoolantLevel() ?: 100 <= resources.getInteger(R.integer.critical_threshold) -> "Your estimated coolant level is critically low!  Be sure to fill it up soon!"
						!vehicle.hasRecentOilLog() -> "It's been ${vehicle.getTimeSinceLastOilCheck()} days since you last checked your oil level.  It's probably time for another checkup!"
						!vehicle.hasRecentCoolantLog() -> "It's been ${vehicle.getTimeSinceLastCoolantCheck()} days since you last checked your coolant level.  It's probably time for another checkup!"
						else -> "Everything looks great!"
					}
					if (position < selectedButtons?.size ?: 0) {
						selectedButtons?.get(position)?.let {
							when (it) {
								MaintenanceType.GAS -> displayDetailLayout(gas_button, vehicle.gasLogs.value, it)
								MaintenanceType.OIL -> displayDetailLayout(oil_button, vehicle.oilLogs.value, it)
								else -> displayDetailLayout(coolant_button, vehicle.coolantLogs.value, it)
							}
						}
					}
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
						vehicle.gasLogs.observe(this@DashboardActivity, android.arch.lifecycle.Observer {
							text = vehicle.getAverageGasMileage().toString() + " MPG"
						})
						setOnClickListener {
							if (!isSelected) {
								displayDetailLayout(this, vehicle.gasLogs.value, MaintenanceType.GAS)
							} else {
								hideDetailLayout(this)
								selectedButtons?.set(position, null)
							}
						}
					}

					oil_button.run {
						vehicle.getEstimatedOilLevel()?.let { oilLevel ->
							text = "$oilLevel%"
							background = getButtonStyleFromLevel(oilLevel.toInt())
						} ?: run {
							text = "???"
							background = getButtonStyleFromLevel()
						}
						setOnClickListener {
							if (!isSelected) displayDetailLayout(this, vehicle.oilLogs.value as List<BaseLogEntry>?, MaintenanceType.OIL) else {
								hideDetailLayout(this)
								selectedButtons?.set(position, null)
							}
						}
					}
					coolant_button.run {
						vehicle.getEstimatedCoolantLevel()?.let { coolantLevel ->
							text = "$coolantLevel%"
							background = getButtonStyleFromLevel(coolantLevel.toInt())
						} ?: run {
							text = "???"
							background = getButtonStyleFromLevel()
						}
						setOnClickListener {
							if (!isSelected) displayDetailLayout(this, vehicle.coolantLogs.value as List<BaseLogEntry>?, MaintenanceType.COOLANT) else {
								hideDetailLayout(this)
								selectedButtons?.set(position, null)
							}
						}
					}
				}
			}
		}

		inner class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
	}

}


