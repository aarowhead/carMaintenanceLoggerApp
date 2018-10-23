package com.logger.car.androidcarmaintenanceapp.checkup

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry

class CheckupMainFragment() : DialogFragment() {
	private lateinit var customView: View
	private var gasLogEntry: GasLogEntry? = null
	private var oilLogEntry: FluidLogEntry? = null
	private var coolantLogEntry: FluidLogEntry? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return customView
	}

	override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext()).apply {
		setTitle("Checkup")
		customView = requireActivity().layoutInflater.inflate(R.layout.checkup_frame_layout, null)
		setView(customView)
		childFragmentManager.beginTransaction().run {
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
			} })
			commit()
		}
	}.create()

	fun showOilFragment() {
		gasLogEntry?.let { gasEntry ->
			gasEntry.entryDate?.let { date ->
				childFragmentManager.beginTransaction().run {
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
								TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
							}

						}
					})
					setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
					commit()
				}
			}
		}
	}

	fun showCoolantFragment() {
		gasLogEntry?.let { gasEntry ->
			gasEntry.entryDate?.let { date ->
				childFragmentManager.beginTransaction().run {
					replace(R.id.frame, CheckupCoolantFragment.newInstance(date, gasEntry.mileage ?: 0).apply {
						callback = object : CheckupFragment.CheckupCallback<FluidLogEntry> {
							override fun onProceedCheckup(entry: FluidLogEntry) {
								coolantLogEntry = entry
								Toast.makeText(requireActivity(), "Checkup Complete!", Toast.LENGTH_SHORT).show()
								dismiss()
							}

							override fun onCancelCheckup() {
								dismiss()
							}

							override fun onBackSelected() {
								TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
							}

						}
					})
					setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
					commit()
				}
			}
		}
	}
}