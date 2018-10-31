package com.logger.car.androidcarmaintenanceapp.logs

import android.app.DatePickerDialog
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.dashboard.DashboardActivity
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.getDayOfMonth
import com.logger.car.androidcarmaintenanceapp.getMonthName
import kotlinx.android.synthetic.main.oil_level_indicator_layout.*
import kotlinx.android.synthetic.main.oil_level_indicator_layout.view.*
import kotlinx.android.synthetic.main.log_date_layout.view.*
import kotlinx.android.synthetic.main.log_entry.view.*
import kotlinx.android.synthetic.main.logs_fragment.view.*
import kotlinx.android.synthetic.main.set_oil_level_dialog_fragment.view.*
import java.text.NumberFormat
import java.util.*

abstract class MotorFluidsFragment : LogsFragment() {

	private var fluidList: MutableLiveData<MutableList<FluidLogEntry>>? = null
	private val adapter = MotorFluidsAdapter()

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = super.onCreateView(inflater, container, savedInstanceState)
		fluidList = getFluidList()
		fluidList?.observe(this, android.arch.lifecycle.Observer {
			val estimatedLevel = getEstimatedLevelByDate(it as List<FluidLogEntry>)
			view.estimated_level_text.text = estimatedLevel.toString() + "%"
			view.level_indicator.progress = estimatedLevel?.toInt() ?: 0
			adapter.entries = it
			adapter.notifyDataSetChanged()
		})
		return view
	}

	abstract fun getFluidList(): MutableLiveData<MutableList<FluidLogEntry>>?

	override fun onSaveClicked(customView: View) {
		val newEntry = FluidLogEntry(
				Calendar.getInstance().time,
				customView.mileage_edit_text.text.toString().toIntOrNull(),
				customView.level_indicator_layout.level_indicator.progress)
		if (newEntry.isValidLogEntry()) {
			//TODO: figure out how to do safer indexing for when there is no 0 index?
			fluidList?.let { it.value = it.value.also { it?.add(0, newEntry) } }
			Toast.makeText(context, "Entry Saved", Toast.LENGTH_LONG).show()
		} else {
			//TODO: Think about showing this in a dialog
			Toast.makeText(context, "Unable to save entry due to missing data.", Toast.LENGTH_LONG).show()
		}
	}

	abstract fun getDialogIndicatorLayout(): Int

	override fun getDialogCustomView(): View {
		val dialogView = layoutInflater.inflate(getDialogIndicatorLayout(), null)
		dialogView.level_indicator_layout.level_indicator.progress = frameView.level_indicator.progress
		dialogView.level_indicator_layout.level_indicator.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
				DashboardActivity.setLevelText(requireContext(), progress, dialogView.level_text)
			}

			override fun onStartTrackingTouch(p0: SeekBar?) {
			}

			override fun onStopTrackingTouch(p0: SeekBar?) {
			}
		})
		dialogView.date_edit_text_motor_fluid.setText(sdf.format(Calendar.getInstance().time))
		Calendar.getInstance().run {
			dialogView.date_edit_text_motor_fluid.setOnClickListener {
				DatePickerDialog(requireContext(), R.style.AppTheme_AlertDialog, DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
					Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time.let {
						dialogView.date_edit_text_motor_fluid.setText(sdf.format(it))
					}
				}, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)).show()
			}
		}
		DashboardActivity.setLevelText(requireContext(), level_indicator.progress, dialogView.level_text)
		return dialogView
	}

	override fun getAdapter() = adapter

	inner class MotorFluidsAdapter : LogAdapter<FluidLogEntry>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
				LogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_entry, parent, false))

		override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
			entries[position].let {
				holder.itemView.historic_level_indicator.progress = it.level ?: 0
				it.entryDate?.let { date ->
					holder.itemView.log_date_layout.month.text = date.getMonthName()
					holder.itemView.log_date_layout.day.text = date.getDayOfMonth().toString()
				}
				holder.itemView.mileage.text = NumberFormat.getNumberInstance(Locale.US).format(it.mileage) + " miles"
				holder.itemView.fluid_amount_text_indicator.text = it.level.toString() + "%"
			}
		}
	}
}

class CoolantFragment : MotorFluidsFragment() {
	override fun getFluidList() = model?.currentVehicle?.coolantLogs

	override fun getDialogIndicatorLayout() = R.layout.set_coolant_level_dialog_fragment

	override fun getIndicatorLayout(): View {
		val view = layoutInflater.inflate(R.layout.coolant_level_indicator_layout, null)
		view.level_indicator.isEnabled = false
		view.level_indicator.thumb = null
		return view
	}
}

class OilFragment : MotorFluidsFragment() {
	override fun getFluidList() = model?.currentVehicle?.oilLogs

	override fun getDialogIndicatorLayout() = R.layout.set_oil_level_dialog_fragment

	override fun getIndicatorLayout(): View {
		val view = layoutInflater.inflate(R.layout.oil_level_indicator_layout, null)
		view.level_indicator.isEnabled = false
		view.level_indicator.thumb = null
		return view
	}
}