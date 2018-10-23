package com.logger.car.androidcarmaintenanceapp.checkup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.SeekBar
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import kotlinx.android.synthetic.main.checkup_oil_fragment.view.*
import kotlinx.android.synthetic.main.level_indicator_layout.view.*
import java.util.*

class CheckupCoolantFragment : CheckupFragment<FluidLogEntry>() {
	override var logEntry = FluidLogEntry()

	companion object {
		private const val DATE_ARG = "date"
		private const val MILEAGE_ARG = "mileage"

		fun newInstance(date: Date, mileage: Int) = CheckupCoolantFragment().apply {
			arguments = Bundle().apply {
				putLong(DATE_ARG, date.time)
				putInt(MILEAGE_ARG, mileage)
			}
		}
	}

	override fun getLayout() = R.layout.checkup_coolant_fragment

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = super.onCreateView(inflater, container, savedInstanceState)?.apply {
		arguments?.run {
			logEntry.entryDate = Date().apply { time = getLong(DATE_ARG) }
			logEntry.mileage = getInt(MILEAGE_ARG)
		}
		level_indicator_layout.level_indicator.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
				logEntry.level = progress
			}

			override fun onStartTrackingTouch(p0: SeekBar?) {
			}

			override fun onStopTrackingTouch(p0: SeekBar?) {
			}
		})
	}
}