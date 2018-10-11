package com.logger.car.androidcarmaintenanceapp.checkup

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.logger.car.androidcarmaintenanceapp.R

class CheckupMainFragment() : DialogFragment() {
	private lateinit var customView: View

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		return customView
	}

	override fun onCreateDialog(savedInstanceState: Bundle?) = AlertDialog.Builder(requireContext()).apply {
		setTitle("Checkup")
		customView = requireActivity().layoutInflater.inflate(R.layout.checkup_frame_layout, null)
		setView(customView)
		childFragmentManager.beginTransaction().run {
			add(R.id.frame, CheckupGasFragment())
			commit()
		}
		setPositiveButton("Next", null)
		setNegativeButton("Cancel") { _, _ ->
			dismiss()
		}

	}.create().apply {
		setOnShowListener { _ ->
			getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
				childFragmentManager.beginTransaction().run {
					replace(R.id.frame, CheckupOilFragment())
					setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
					commit()
					getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener { _ ->
						childFragmentManager.beginTransaction().run {
							replace(R.id.frame, CheckupCoolantFragment())
							setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
							commit()
							getButton(AlertDialog.BUTTON_POSITIVE).run {
								text = "Finish"
								setOnClickListener {
									dismiss()
									Toast.makeText(context, "Should save but not implemented yet...Sorry", Toast.LENGTH_LONG).show()
								}
							}
						}
					}
				}
			}
		}
	}
}