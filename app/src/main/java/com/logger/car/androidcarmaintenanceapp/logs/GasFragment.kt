package com.logger.car.androidcarmaintenanceapp.logs

import android.app.DatePickerDialog
import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import com.logger.car.androidcarmaintenanceapp.getDayOfMonth
import com.logger.car.androidcarmaintenanceapp.getMonthName
import kotlinx.android.synthetic.main.gas_entry_dialog_fragment.view.*
import kotlinx.android.synthetic.main.gas_mileage_indicator.view.*
import kotlinx.android.synthetic.main.log_date_layout.view.*
import kotlinx.android.synthetic.main.log_entry.view.*
import kotlinx.android.synthetic.main.logs_fragment.view.*
import java.text.NumberFormat
import java.util.*

class GasFragment: LogsFragment() {

	private val adapter = GasAdapter()
	private var gasLogs: MutableLiveData<MutableList<GasLogEntry>>? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = super.onCreateView(inflater, container, savedInstanceState)
		view.estimated_level_label.visibility = View.INVISIBLE
		gasLogs = model?.currentVehicle?.gasLogs
		gasLogs?.observe(this, android.arch.lifecycle.Observer {
			view.mpg_indicator_text_view.text = "24"
			adapter.entries = it as List<GasLogEntry>
			adapter.notifyDataSetChanged()
		})
		return view
	}

	override fun getIndicatorLayout(): View = layoutInflater.inflate(R.layout.gas_mileage_indicator, null)

	override fun getAdapter() = adapter

	override fun getDialogCustomView(): View {
		val view = layoutInflater.inflate(R.layout.gas_entry_dialog_fragment, null)
		view.date_edit_text_gas.setText(sdf.format(Calendar.getInstance().time))
		Calendar.getInstance().run {
			view.date_edit_text_gas.setOnClickListener {
				DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
					Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time.let {
						view.date_edit_text_gas.setText(sdf.format(it))
					}
				}, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)).show()
			}
		}
		return view
	}

	//TODO: figure out safer way to conver toInt and toDouble
	override fun onSaveClicked(customView: View) {
		val newEntry = GasLogEntry(
				sdf.parse(customView.date_edit_text_gas.text.toString()),
				customView.mileage_edit_text_gas.text.toString().toInt(),
				customView.gallons_added_edit_text.text.toString().toDouble())

		if (newEntry.isValidLogEntry()) {
			//TODO: figure out how to do safer indexing for when there is no 0 index?
			gasLogs?.let { it.value = it.value.also { it?.add(0, newEntry) } }
			Toast.makeText(context, "Entry Saved", Toast.LENGTH_LONG).show()
		} else {
			//TODO: Think about showing this in a dialog
			Toast.makeText(context, "Unable to save entry due to missing data.", Toast.LENGTH_LONG).show()
		}
	}

	inner class GasAdapter : LogAdapter<GasLogEntry>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
				LogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_entry, parent, false))

		override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
			entries[position].let { gasLogEntry ->
				model?.currentVehicle?.tankSize?.let { tankSize ->
					holder.itemView.historic_level_indicator.max = tankSize
					holder.itemView.historic_level_indicator.progress = gasLogEntry.gallonsAdded?.toInt() ?: 0
					gasLogEntry.entryDate?.let { date ->
						holder.itemView.log_date_layout.month.text = date.getMonthName()
						holder.itemView.log_date_layout.day.text = date.getDayOfMonth().toString()
					}
					holder.itemView.mileage.text = NumberFormat.getNumberInstance(Locale.US).format(gasLogEntry.mileage) + " miles"
					holder.itemView.fluid_amount_text_indicator.text = gasLogEntry.gallonsAdded.toString() + " gallons"
				}
			}
		}
	}

}