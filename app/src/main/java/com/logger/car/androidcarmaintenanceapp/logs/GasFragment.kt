package com.logger.car.androidcarmaintenanceapp.logs

import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import kotlinx.android.synthetic.main.gas_entry_dialog_fragment.view.*
import kotlinx.android.synthetic.main.gas_log_entry.view.*
import kotlinx.android.synthetic.main.gas_mileage_indicator.view.*
import kotlinx.android.synthetic.main.logs_fragment.view.*
import java.util.*

class GasFragment: LogsFragment() {

	private val adapter = GasAdapter()
	private var gasLogs: MutableLiveData<MutableList<GasLogEntry>>? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
		val view = super.onCreateView(inflater, container, savedInstanceState)
		view.estimated_level_label.visibility = View.INVISIBLE
		gasLogs = model?.currentVehicle?.gasLogs
		gasLogs?.observe(this, android.arch.lifecycle.Observer {
			view.mpg_indicator.text = "24 MPG"
			adapter.entries = it as List<GasLogEntry>
			adapter.notifyDataSetChanged()
		})
		return view
	}

	override fun getIndicatorLayout(): View = layoutInflater.inflate(R.layout.gas_mileage_indicator, null)

	override fun getAdapter() = adapter

	override fun getDialogCustomView(): View {
		val view = layoutInflater.inflate(R.layout.gas_entry_dialog_fragment, null)
		view.date_edit_text_gas.text = Calendar.getInstance().time.toString()
		view.edit_date_image_view.setOnClickListener {
			view.gas_dialog_view_switcher.showNext()
		}
		return view
	}

	//TODO: figure out safer way to conver toInt and toDouble
	override fun onSaveClicked(customView: View) {
		val newEntry = GasLogEntry(
				Calendar.getInstance().time,
				customView.mileage_edit_text_gas.text.toString().toInt(),
				customView.gallons_added_edit_text.text.toString().toDouble())
		//TODO: figure out how to do safer indexing for when there is no 0 index?
		gasLogs?.let { it.value = it.value.also { it?.add(0, newEntry) } }
	}

	inner class GasAdapter : LogAdapter<GasLogEntry>() {
		override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
				LogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gas_log_entry, parent, false))

		override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
			entries[position].let {
				holder.itemView.date_text_view.text = it.entryDate.toString()
				holder.itemView.gallons_added.text = it.gallonsAdded.toString()
			}
		}
	}

}