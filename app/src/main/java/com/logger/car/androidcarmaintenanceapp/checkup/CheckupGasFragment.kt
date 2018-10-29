package com.logger.car.androidcarmaintenanceapp.checkup

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import kotlinx.android.synthetic.main.checkup_gas_fragment.*
import kotlinx.android.synthetic.main.checkup_gas_fragment.view.*
import java.text.SimpleDateFormat
import java.util.*

class CheckupGasFragment : CheckupFragment<GasLogEntry>() {
	private val sdf = SimpleDateFormat("MM/dd/yy", Locale.US)

	override fun getLayout() = R.layout.checkup_gas_fragment

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = super.onCreateView(inflater, container, savedInstanceState)?.apply {
		model.pendingGasEntry?.let {
			gallons_added_edit_text.setText(it.gallonsAdded.toString())
			mileage_edit_text.setText(it.mileage.toString())
			date_edit_text.setText(sdf.format(it.entryDate))
		} ?: run { model.pendingGasEntry = GasLogEntry() }
		gallons_added_edit_text.addListener {
			model.pendingGasEntry?.gallonsAdded = it?.toString()?.toDoubleOrNull()
			enableProceedButtonIfReady()
		}
		mileage_edit_text.addListener {
			model.pendingGasEntry?.mileage = it?.toString()?.toIntOrNull()
			enableProceedButtonIfReady()
		}
		Calendar.getInstance().run {
			date_edit_text.setOnClickListener {
				DatePickerDialog(requireContext(), DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
					Calendar.getInstance().apply { set(year, month, dayOfMonth) }.time.let { date ->
						model.pendingGasEntry?.entryDate = date
						date_edit_text.setText(sdf.format(date))
					}
					enableProceedButtonIfReady()
				}, get(Calendar.YEAR), get(Calendar.MONTH), get(Calendar.DAY_OF_MONTH)).show()
			}
		}
	}

	private fun enableProceedButtonIfReady() {
		proceed_button.isEnabled = model.pendingGasEntry?.isValidLogEntry() == true
	}

	private fun EditText.addListener(action: (text: Editable?) -> Unit) {
		this.addTextChangedListener(object : TextWatcher {
			override fun afterTextChanged(text: Editable?) {
				action.invoke(text)
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
			}

			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
			}
		})
	}
}
