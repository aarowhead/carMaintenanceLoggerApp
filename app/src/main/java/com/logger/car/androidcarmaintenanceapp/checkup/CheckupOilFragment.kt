package com.logger.car.androidcarmaintenanceapp.checkup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.SeekBar
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.dashboard.DashboardActivity
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import kotlinx.android.synthetic.main.checkup_oil_fragment.view.*
import kotlinx.android.synthetic.main.oil_level_indicator_layout.view.*

class CheckupOilFragment : CheckupFragment<FluidLogEntry>() {
	override fun getLayout() = R.layout.checkup_oil_fragment

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = super.onCreateView(inflater, container, savedInstanceState)?.apply {
		model.getPendingOilEntry()?.run {
			level_indicator_layout.level_indicator.progress = level ?: 0
		} ?: run { model.getPendingGasEntry()?.let { model.setPendingOilEntry(FluidLogEntry(it.entryDate, it.mileage)) } }
		level_indicator_layout.level_indicator.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
			override fun onProgressChanged(bar: SeekBar, progress: Int, fromUser: Boolean) {
				model.getPendingOilEntry()?.level = progress
				DashboardActivity.setLevelText(requireContext(), progress, level_text)
			}

			override fun onStartTrackingTouch(p0: SeekBar?) {
			}

			override fun onStopTrackingTouch(p0: SeekBar?) {
			}
		})
		DashboardActivity.setLevelText(requireContext(), level_indicator_layout.level_indicator.progress, level_text)
		(activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(this.windowToken, 0)
	}
}
