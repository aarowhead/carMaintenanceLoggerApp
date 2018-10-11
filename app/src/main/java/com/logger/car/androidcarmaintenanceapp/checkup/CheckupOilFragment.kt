package com.logger.car.androidcarmaintenanceapp.checkup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logger.car.androidcarmaintenanceapp.R

class CheckupOilFragment: Fragment() {
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val view = inflater.inflate(R.layout.checkup_oil_fragment, container, false)
		return view
	}
}