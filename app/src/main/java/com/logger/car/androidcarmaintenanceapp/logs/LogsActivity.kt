package com.logger.car.androidcarmaintenanceapp.logs

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.opengl.Visibility
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logger.car.androidcarmaintenanceapp.R
import com.logger.car.androidcarmaintenanceapp.dashboard.DashboardActivity.Companion.MAINTENANCE_TYPE
import com.logger.car.androidcarmaintenanceapp.dashboard.DashboardActivity.Companion.VEHICLE_ID
import com.logger.car.androidcarmaintenanceapp.domain.FluidLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.GasLogEntry
import com.logger.car.androidcarmaintenanceapp.domain.MaintenanceType
import com.logger.car.androidcarmaintenanceapp.getDayOfMonth
import com.logger.car.androidcarmaintenanceapp.getMonthName
import kotlinx.android.synthetic.main.gas_entry_dialog_fragment.view.*
import kotlinx.android.synthetic.main.gas_log_entry.view.*
import kotlinx.android.synthetic.main.gas_mileage_indicator.view.*
import kotlinx.android.synthetic.main.level_indicator_layout.view.*
import kotlinx.android.synthetic.main.log_entry.view.*
import kotlinx.android.synthetic.main.logs_activity.*
import kotlinx.android.synthetic.main.logs_fragment.view.*
import kotlinx.android.synthetic.main.set_level_dialog_fragment.view.*
import java.text.NumberFormat
import java.util.*


//TODO: Move strings to strings resources
class LogsActivity : AppCompatActivity() {

	var toolbar: ActionBar? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.logs_activity)
		val model = ViewModelProviders.of(this).get(LogsViewModel::class.java)
		//TODO: Figure out if there is a way to default the value to null
		val vehicleId = intent.getIntExtra(VEHICLE_ID, -1)
		if (vehicleId != -1) model.setCurrentVehicle(vehicleId)
		val adapter = LogsPagerAdapter()
		val viewSwitcher = logs_view_switcher
		viewSwitcher.adapter = adapter
		toolbar = supportActionBar
		val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

		//TODO: Figure out if there is a cleaner way to do this
		viewSwitcher.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
			override fun onPageScrollStateChanged(position: Int) {}

			override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

			override fun onPageSelected(position: Int) {
				bottomNavigation.menu.apply {
					findItem(bottomNavigation.selectedItemId).isChecked = false
					getItem(position).isChecked = true
				}
			}
		})

		//TODO: remove redundant code
		bottomNavigation.setOnNavigationItemSelectedListener {
			when (it.itemId) {
				R.id.navigation_gas -> {
					viewSwitcher.currentItem = 0
					true
				}
				R.id.navigation_oil -> {
					viewSwitcher.currentItem = 1
					true
				}
				R.id.navigation_coolant -> {
					viewSwitcher.currentItem = 2
					true
				}
				else -> false
			}
		}
		viewSwitcher.currentItem =
				when (intent.getSerializableExtra(MAINTENANCE_TYPE)) {
					MaintenanceType.OIL -> 1
					MaintenanceType.COOLANT -> 2
					else -> 0
				}
	}

	//TODO: Combine duplicate code in Oil and Coolant fragments!!!!!!!!!!!!!!!!!!
	class OilFragment : LogsFragment() {
		private val adapter = OilAdapter()
		private var oilLogs: MutableLiveData<MutableList<FluidLogEntry>>? = null

		override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
			val view = super.onCreateView(inflater, container, savedInstanceState)
			oilLogs = model?.currentVehicle?.oilLogs
			oilLogs?.observe(this, android.arch.lifecycle.Observer {
				val estimatedLevel = getEstimatedLevelByDate(it as List<FluidLogEntry>)
				view.estimated_level_text.text = estimatedLevel.toString() + "%"
				view.level_indicator.progress = estimatedLevel.toInt()
				adapter.entries = it
				adapter.notifyDataSetChanged()
			})
			return view
		}

		override fun getDialogCustomView(): View {
			val dialogView = layoutInflater.inflate(R.layout.set_level_dialog_fragment, null)
			dialogView.level_indicator_layout.level_indicator.progress = frameView.level_indicator.progress
			return dialogView
		}

		override fun onSaveClicked(customView: View) {
			val newEntry = FluidLogEntry(
					Calendar.getInstance().time,
					customView.mileage_edit_text.text.toString().toInt(),
					customView.level_indicator_layout.level_indicator.progress)
			//TODO: figure out how to do safer indexing for when there is no 0 index?
			oilLogs?.let { it.value = it.value.also { it?.add(0, newEntry) } }
		}

		override fun getAdapter() = adapter

		inner class OilAdapter : LogAdapter<FluidLogEntry>() {
			override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
					LogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_entry, parent, false))

			override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
				entries[position].let {
					holder.itemView.historic_level_indicator.progress = it.level
					holder.itemView.month.text = it.entryDate.getMonthName()
					holder.itemView.day.text = it.entryDate.getDayOfMonth().toString()
					holder.itemView.mileage.text = NumberFormat.getNumberInstance(Locale.US).format(it.mileage) + " miles"
					holder.itemView.percentage_indicator.text = it.level.toString() + "%"
				}
			}
		}
	}

	class CoolantFragment : LogsFragment() {

		private val adapter = CoolantAdapter()
		private var coolantLogs: MutableLiveData<MutableList<FluidLogEntry>>? = null

		override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
			val view = super.onCreateView(inflater, container, savedInstanceState)
			coolantLogs = model?.currentVehicle?.coolantLogs
			coolantLogs?.observe(this, android.arch.lifecycle.Observer {
				val estimatedLevel = getEstimatedLevelByDate(it as List<FluidLogEntry>)
				view.estimated_level_text.text = estimatedLevel.toString() + "%"
				view.level_indicator.progress = estimatedLevel.toInt()
				adapter.entries = it
				adapter.notifyDataSetChanged()
			})
			return view
		}

		override fun getDialogCustomView(): View {
			val dialogView = layoutInflater.inflate(R.layout.set_level_dialog_fragment, null)
			dialogView.level_indicator_layout.level_indicator.progress = frameView.level_indicator.progress
			return dialogView
		}

		override fun onSaveClicked(customView: View) {
			val newEntry = FluidLogEntry(
					Calendar.getInstance().time,
					customView.mileage_edit_text.text.toString().toInt(),
					customView.level_indicator_layout.level_indicator.progress)
			//TODO: figure out how to do safer indexing for when there is no 0 index?
			coolantLogs?.let { it.value = it.value.also { it?.add(0, newEntry) } }
		}

		override fun getAdapter() = adapter

		inner class CoolantAdapter : LogAdapter<FluidLogEntry>() {
			override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
					LogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.log_entry, parent, false))

			override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
				entries[position].let {
					holder.itemView.historic_level_indicator.progress = it.level
					holder.itemView.month.text = it.entryDate.getMonthName()
					holder.itemView.day.text = it.entryDate.getDayOfMonth().toString()
					holder.itemView.mileage.text = NumberFormat.getNumberInstance(Locale.US).format(it.mileage) + " miles"
					holder.itemView.percentage_indicator.text = it.level.toString() + "%"
				}
			}
		}
	}

	class GasFragment : LogsFragment() {

		private val adapter = GasAdapter()
		private var gasLogs: MutableLiveData<MutableList<GasLogEntry>>? = null

		override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
			val view = super.onCreateView(inflater, container, savedInstanceState)
			view.estimated_level_label.visibility = View.INVISIBLE
			gasLogs = model?.currentVehicle?.gasLogs
			gasLogs?.observe(this, android.arch.lifecycle.Observer {
				view.mpg_indicator.text = "24 MPG"
				adapter.entries = it as List<GasLogEntry>
				adapter.notifyDataSetChanged()
			})
			return view
		}

		override fun getIndicatorLayout(): View = layoutInflater.inflate(R.layout.gas_mileage_indicator, null)

		override fun getAdapter() = adapter

		override fun getDialogCustomView(): View = layoutInflater.inflate(R.layout.gas_entry_dialog_fragment, null)

		//TODO: figure out safer way to conver toInt and toDouble
		override fun onSaveClicked(customView: View) {
			val newEntry = GasLogEntry(
					Calendar.getInstance().time,
					customView.mileage_edit_text_gas.text.toString().toInt(),
					customView.gallons_added_edit_text.text.toString().toDouble())
			//TODO: figure out how to do safer indexing for when there is no 0 index?
			gasLogs?.let { it.value = it.value.also { it?.add(0, newEntry) } }
		}

		inner class GasAdapter : LogAdapter<GasLogEntry>() {
			override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
					LogViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.gas_log_entry, parent, false))

			override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
				entries[position].let {
					holder.itemView.date_text_view.text = it.entryDate.toString()
					holder.itemView.gallons_added.text = it.gallonsAdded.toString()
				}
			}
		}
	}

	inner class LogsPagerAdapter : FragmentPagerAdapter(supportFragmentManager) {

		private val fragmentList = listOf(GasFragment(), OilFragment(), CoolantFragment())

		override fun getItem(fragmentIndex: Int) = fragmentList[fragmentIndex]

		override fun getCount() = fragmentList.size
	}
}