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
	private var gasLogEntry: GasLogEntry? = null
	private var oilLogEntry: FluidLogEntry? = null
	private var coolantLogEntry: FluidLogEntry? = null
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
			add(R.id.frame, CheckupGasFragment().apply { callback = object: CheckupFragment.CheckupCallback<GasLogEntry> {
				override fun onProceedCheckup(entry: GasLogEntry) {
					gasLogEntry = entry
					showOilFragment()
				}

				override fun onCancelCheckup() {
					dismiss()
				}

				override fun onBackSelected() {
				}
			} }).addToBackStack(null)
			commit()
		}
	}.create()

	fun showOilFragment() {
		dialog.setTitle(getString(R.string.oil_log_dialog_title))
		gasLogEntry?.let { gasEntry ->
			gasEntry.entryDate?.let { date ->
				cfm.beginTransaction().run {
					replace(R.id.frame, CheckupOilFragment.newInstance(date, gasEntry.mileage ?: 0).apply {
						callback = object : CheckupFragment.CheckupCallback<FluidLogEntry> {
							override fun onProceedCheckup(entry: FluidLogEntry) {
								oilLogEntry = entry
								showCoolantFragment()
							}

							override fun onCancelCheckup() {
								dismiss()
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
		}
	}

	fun showCoolantFragment() {
		dialog.setTitle(getString(R.string.coolant_log_dialog_title))
		gasLogEntry?.let { gasEntry ->
			gasEntry.entryDate?.let { date ->
				cfm.beginTransaction().run {
					replace(R.id.frame, CheckupCoolantFragment.newInstance(date, gasEntry.mileage ?: 0).apply {
						callback = object : CheckupFragment.CheckupCallback<FluidLogEntry> {
							override fun onProceedCheckup(entry: FluidLogEntry) {
								coolantLogEntry = entry
								model.addGasEntry(vehicleId ?: 0, gasEntry)
								oilLogEntry?.let { model.addOilLogEntry(vehicleId ?: 0, it) }
								coolantLogEntry?.let { model.addCoolantLogEntry(vehicleId ?: 0, it) }
								Toast.makeText(requireActivity(), "Checkup Complete!", Toast.LENGTH_SHORT).show()
								dismiss()
								onFinishedCallback?.onFinished(vehicleId ?: 0)
							}

							override fun onCancelCheckup() {
								dismiss()
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
		}
	}

	interface OnCheckupFinishedListener {
		fun onFinished(vehicleId: Int)
	}
}