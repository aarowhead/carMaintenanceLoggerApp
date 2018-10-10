package com.logger.car.androidcarmaintenanceapp.logs

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logger.car.androidcarmaintenanceapp.MainViewModel
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.getDayOfMonth
import com.logger.car.androidcarmaintenanceapp.getMonthName
import kotlinx.android.synthetic.main.log_entry.*
import kotlinx.android.synthetic.main.log_entry.view.*
import kotlinx.android.synthetic.main.log_recycler_view.view.*
import kotlinx.android.synthetic.main.logs_fragment.view.*
import kotlinx.android.synthetic.main.set_level_dialog_fragment.view.*
import java.text.NumberFormat
import java.util.*

//TODO: Major cleanup needed on this
abstract class LogsFragment : Fragment() {

    var model: MainViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.logs_fragment, container, false)
        model = activity?.let { ViewModelProviders.of(it).get(MainViewModel::class.java) }
        view.log_recycler.layoutManager = LinearLayoutManager(activity)
        val adapter = LogAdapter()
        view.log_recycler.adapter = adapter
        view.level_indicator.isEnabled = false
        val logEntries = getList()
        logEntries?.observe(this, android.arch.lifecycle.Observer {
            adapter.entries = it
            adapter.notifyDataSetChanged()
            val estimatedLevel = getEstimatedLevelByDate(it as List<FluidLogEntry>)
            view.estimated_level_text.text = estimatedLevel.toString() + "%"
            view.level_indicator.progress = estimatedLevel.toInt()
        })

        view.add_log.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.set_level_dialog_fragment, null)
            dialogView.level_indicator_layout.level_indicator.progress = level_indicator.progress
            activity?.let {
                AlertDialog.Builder(it)
                        .setTitle("Add Entry")
                        .setView(dialogView)
                        .setPositiveButton("Save") { _, _ ->
                            dialogView.mileage_edit_text.text
                            //TODO: make it get date from edit text
                            val newEntry = FluidLogEntry(
                                    Calendar.getInstance().time,
                                    dialogView.mileage_edit_text.text.toString().toInt(),
                                    dialogView.level_indicator_layout.level_indicator.progress)
                            //TODO: figure out how to do safer indexing for when there is no 0 index?
                            logEntries?.setValue(logEntries.value.also { it?.add(0, newEntry) })
                        }
                        .setNegativeButton("Cancel") { dialog, _ -> dialog?.dismiss() }
                        .create()
                        .show()
            }
        }
        return view
    }

    abstract fun getList(): MutableLiveData<MutableList<FluidLogEntry>>?

    private fun getEstimatedLevelByDate(list: List<FluidLogEntry>) = list.first().level - daysBetween(Calendar.getInstance().time, list.first().entryDate) * getAverageLossPerDay(list)

    private fun getAverageLossPerDay(list: List<FluidLogEntry>) =
            getTotalLoss(list) / daysBetween(list.first().entryDate, list.last().entryDate).let { if (it == 0L) 1L else it }

    private fun getTotalLoss(list: List<FluidLogEntry>): Int {
        var previousLevel: Int? = null
        var totalLoss = 0
        list.forEach { entry ->
            previousLevel?.let {
                val difference = it - entry.level
                if (difference > 0) {
                    totalLoss += difference
                }
            }
            previousLevel = entry.level
        }
        return totalLoss
    }

    private fun daysBetween(d1: Date, d2: Date) = ((d2.time - d1.time) / (1000 * 60 * 60 * 24))

    class LogAdapter : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

        var entries: List<FluidLogEntry>? = emptyList()

        class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                LogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_entry, parent, false))

        override fun getItemCount() = entries?.size ?: 0

        override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
            entries?.get(position)?.let {
                holder.itemView.level_indicator.progress = it.level
                holder.itemView.month.text = it.entryDate.getMonthName()
                holder.itemView.day.text = it.entryDate.getDayOfMonth().toString()
                holder.itemView.mileage.text = NumberFormat.getNumberInstance(Locale.US).format(it.mileage) + " miles"
                holder.itemView.percentage_indicator.text = it.level.toString() + "%"
            }
        }
    }
}