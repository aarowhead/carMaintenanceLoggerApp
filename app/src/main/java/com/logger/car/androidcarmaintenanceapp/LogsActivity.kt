package com.logger.car.androidcarmaintenanceapp

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.Vehicle
import kotlinx.android.synthetic.main.dashboard_cardview.view.*
import kotlinx.android.synthetic.main.log_entry.view.*
import kotlinx.android.synthetic.main.log_recycler_view.*
import java.time.LocalDate
import java.util.*

class LogsActivity: AppCompatActivity() {

    var toolbar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logs_activity)

        val model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        log_recycler.layoutManager = LinearLayoutManager(this)
        val adapter = LogAdapter()
        log_recycler.adapter = adapter

        toolbar = supportActionBar
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_gas -> {
                    adapter.entries = model.getVehicles()[0].coolantLogs
                    adapter.notifyDataSetChanged()
                    true
                }
                R.id.navigation_oil -> {
                    adapter.entries = model.getVehicles()[0].oilLogs
                    adapter.notifyDataSetChanged()
                    true
                }
                R.id.navigation_coolant -> {
                    adapter.entries = model.getVehicles()[0].coolantLogs
                    adapter.notifyDataSetChanged()
                    true
                }
                else -> false
            }
        }
    }

    class LogAdapter : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

        var entries: List<FluidLogEntry>? = emptyList()

        class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                LogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_entry, parent, false))

        override fun getItemCount() = entries?.size ?: 0

        override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
            //TODO do this better
            entries?.get(position)?.let {
                holder.itemView.level_indicator.progress = it.level
                holder.itemView.month.text = "May"
                holder.itemView.day.text = "15"
                holder.itemView.mileage.text = it.mileage.toString()
                holder.itemView.percentage_indicator.text = it.level.toString() + "%"
            }
        }
    }
}