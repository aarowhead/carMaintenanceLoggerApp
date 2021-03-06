package com.logger.car.androidcarmaintenanceapp.checkup

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.dashboard.DashboardViewModel
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry

class CheckupMainFragment : DialogFragment() {
	companion object {
		private const val VEHICLE_ID_ARG = "vehicleId"

		fun newInstance(vehicleId: Int) = CheckupMainFragment().apply { arguments = Bundle().apply { putInt(VEHICLE_ID_ARG, vehicleId) } }
	}

	private lateinit var model: DashboardViewModel
	private lateinit var customView: View
	private lateinit var cfm: FragmentManager
	private var vehicleId: Int? = null
	var onFinishedCallback: OnCheckupFinishedListener? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) = customView

	override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext()).apply {
		setTitle(getString(R.string.gas_log_dialog_title))
		cfm = childFragmentManager
		customView = requireActivity().layoutInflater.inflate(R.layout.checkup_frame_layout, null)
		setView(customView)
		vehicleId = arguments?.getInt(VEHICLE_ID_ARG)
		model = ViewModelProviders.of(this@CheckupMainFragment).get(DashboardViewModel::class.java)
		cfm.beginTransaction().run {
			add(R.id.frame, CheckupGasFragment().apply {
				callback = object : CheckupFragment.CheckupCallback<GasLogEntry> {
					override fun onProceedCheckup() {
						showOilFragment()
					}

					override fun onCancelCheckup() {
						cancelCheckup()
					}

					override fun onBackSelected() {
					}
				}
			}).addToBackStack(null)
			commit()
		}
	}.create()

	fun showOilFragment() {
		dialog.setTitle(getString(R.string.oil_log_dialog_title))
		cfm.beginTransaction().run {
			replace(R.id.frame, CheckupOilFragment().apply {
				callback = object : CheckupFragment.CheckupCallback<FluidLogEntry> {
					override fun onProceedCheckup() {
						showCoolantFragment()
					}

					override fun onCancelCheckup() {
						cancelCheckup()
					}

					override fun onBackSelected() {
						dialog.setTitle(getString(R.string.gas_log_dialog_title))
						cfm.popBackStack()
					}

				}
			}).addToBackStack(null)
			setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
			commit()
		}
	}

	fun showCoolantFragment() {
		dialog.setTitle(getString(R.string.coolant_log_dialog_title))
		cfm.beginTransaction().run {
			replace(R.id.frame, CheckupCoolantFragment().apply {
				callback = object : CheckupFragment.CheckupCallback<FluidLogEntry> {
					override fun onProceedCheckup() {
						model.addPendingEntries(vehicleId ?: 0)
						Toast.makeText(requireActivity(), "Checkup Complete!", Toast.LENGTH_SHORT).show()
						dismiss()
						onFinishedCallback?.onFinished(vehicleId ?: 0)
					}

					override fun onCancelCheckup() {
						cancelCheckup()
					}

					override fun onBackSelected() {
						dialog.setTitle(getString(R.string.oil_log_dialog_title))
						cfm.popBackStack()
					}

				}
			}).addToBackStack(null)
			setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
			commit()
		}
	}

	fun cancelCheckup() {
		dismiss()
		model.clearPendingEntries()
	}

	interface OnCheckupFinishedListener {
		fun onFinished(vehicleId: Int)
	}
}