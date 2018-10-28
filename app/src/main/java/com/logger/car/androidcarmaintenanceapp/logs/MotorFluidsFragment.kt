package com.logger.car.androidcarmaintenanceapp.logs

import android.app.DatePickerDialog
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.getDayOfMonth
import com.logger.car.androidcarmaintenanceapp.getMonthName
import kotlinx.android.synthetic.main.gas_log_entry.*
import kotlinx.android.synthetic.main.level_indicator_layout.view.*
import kotlinx.android.synthetic.main.log_entry.view.*
import kotlinx.android.synthetic.main.logs_fragment.view.*
import kotlinx.android.synthetic.main.set_level_dialog_fragment.view.*
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

	override fun getDialogCustomView(): View {
		val dialogView = layoutInflater.inflate(R.layout.set_level_dialog_fragment, null)
		dialogView.level_indicator_layout.level_indicator.progress = frameView.level_indicator.progress
		dialogView.save_date_text_view.text = Calendar.getInstance().time.toString()
		Calendar.getInstance().run {
			dialogView.edit_image_view.setOnClickListener {
				DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
					Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time.let {
						dialogView.save_date_text_view.text = sdf.format(it)
					}
				}, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)).show()
			}
		}
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
					holder.itemView.month.text = date.getMonthName()
					holder.itemView.day.text = date.getDayOfMonth().toString()
				}
				holder.itemView.mileage.text = NumberFormat.getNumberInstance(Locale.US).format(it.mileage) + " miles"
				holder.itemView.percentage_indicator.text = it.level.toString() + "%"
			}
		}
	}
}

class CoolantFragment : MotorFluidsFragment() {
	override fun getFluidList() = model?.currentVehicle?.coolantLogs
}

class OilFragment : MotorFluidsFragment() {
	override fun getFluidList() = model?.currentVehicle?.oilLogs
}