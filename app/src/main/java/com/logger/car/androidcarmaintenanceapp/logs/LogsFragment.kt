package com.logger.car.androidcarmaintenanceapp.logs

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import kotlinx.android.synthetic.main.level_indicator_layout.view.*
import kotlinx.android.synthetic.main.log_recycler_view.view.*
import kotlinx.android.synthetic.main.logs_fragment.view.*
import java.util.*

//TODO: Major cleanup needed on this
abstract class LogsFragment : Fragment() {

    var model: LogsViewModel? = null
    //TODO: Should this be protected?
    lateinit var frameView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.logs_fragment, container, false)
        view.log_recycler.layoutManager = LinearLayoutManager(activity)
        model = activity?.let{ ViewModelProviders.of(it).get(LogsViewModel::class.java) }
        val adapter = getAdapter()
        view.log_recycler.adapter = adapter
        frameView = getIndicatorLayout()
        view.indicator_frame.addView(frameView)

        view.add_log.setOnClickListener {
            showAddEntryDialog(getDialogCustomView())
        }
        return view
    }

    abstract fun getAdapter(): RecyclerView.Adapter<LogAdapter.LogViewHolder>

    open fun getIndicatorLayout(): View {
        val view = layoutInflater.inflate(R.layout.level_indicator_layout, null)
        view.level_indicator.isEnabled = false
        return view
    }

    abstract fun getDialogCustomView(): View

    private fun showAddEntryDialog(customView: View) {
        activity?.let {
            AlertDialog.Builder(it)
                    .setTitle("Add Entry")
                    .setView(customView)
                    .setPositiveButton("Save") { _, _ ->
                        onSaveClicked(customView)
                    }
                    .setNegativeButton("Cancel") { dialog, _ -> dialog?.dismiss() }
                    .create()
                    .show()
        }
    }

    abstract fun onSaveClicked(customView: View)

    protected fun getEstimatedLevelByDate(list: List<FluidLogEntry>) = list.first().level - daysBetween(Calendar.getInstance().time, list.first().entryDate) * getAverageLossPerDay(list)

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

    abstract class LogAdapter<T>(var entries: List<T> = emptyList()) : RecyclerView.Adapter<LogAdapter.LogViewHolder>() {

        class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        abstract override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder

        override fun getItemCount() = entries.size

        abstract override fun onBindViewHolder(holder: LogViewHolder, position: Int)
    }
}