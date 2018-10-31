package com.logger.car.androidcarmaintenanceapp

import android.app.Activity
import android.support.v7.app.AlertDialog
import android.view.View
import com.logger.car.androidcarmaintenanceapp.domain.MaintenanceType
import java.text.DateFormatSymbols
import java.util.*

fun Date.getMonthName(): String? {
    val monthInt = Calendar.getInstance().apply {
        time = this@getMonthName
    }.get(Calendar.MONTH)
    return DateFormatSymbols().shortMonths.getOrNull(monthInt)
}

fun Date.getDayOfMonth() = Calendar.getInstance().apply { time = this@getDayOfMonth }.get(Calendar.DAY_OF_MONTH)

fun Activity.showAddEntryDialog(customView: View, fluidType: MaintenanceType, onSaveClicked: (view: View) -> Unit) {
    this.let {
        val fluidString = when(fluidType) {
            MaintenanceType.OIL -> "Oil"
            MaintenanceType.GAS -> "Gas"
            MaintenanceType.COOLANT -> "Coolant"
        }
        AlertDialog.Builder(it)
                .setTitle("Add $fluidString Entry")
                .setView(customView)
                .setPositiveButton("Save") { _, _ ->
                    onSaveClicked(customView)
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog?.dismiss() }
                .create()
                .show()
    }
}