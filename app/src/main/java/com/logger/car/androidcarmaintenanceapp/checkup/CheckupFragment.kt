package com.logger.car.androidcarmaintenanceapp.checkup

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.AppCompatButton
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.dashboard.DashboardViewModel
import com.logger.car.androidcarmaintenanceapp.domain.BaseLogEntry

abstract class CheckupFragment<T : BaseLogEntry> : Fragment() {
	protected lateinit var model: DashboardViewModel
	var callback: CheckupCallback<T>? = null

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		model = ViewModelProviders.of(this@CheckupFragment).get(DashboardViewModel::class.java)
		val view = inflater.inflate(getLayout(), container, false)
		view.findViewById<AppCompatButton>(R.id.back_button).setOnClickListener { callback?.onBackSelected() }
		view.findViewById<AppCompatButton>(R.id.cancel_button).setOnClickListener { callback?.onCancelCheckup() }
		view.findViewById<AppCompatButton>(R.id.proceed_button).setOnClickListener { logEntry.let { entry -> callback?.onProceedCheckup(entry) } }
		return view
	}

	abstract fun getLayout(): Int

	interface CheckupCallback<T : BaseLogEntry> {
		fun onProceedCheckup(entry: T)
		fun onCancelCheckup()
		fun onBackSelected()
	}
}