package com.logger.car.androidcarmaintenanceapp.logs

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import com.logger.car.androidcarmaintenanceapp.MainViewModel
import com.logger.car.androidcarmaintenanceapp.R
import kotlinx.android.synthetic.main.logs_activity.*


//TODO: Move strings to strings resources
class LogsActivity : AppCompatActivity() {

    var toolbar: ActionBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.logs_activity)
        val adapter = LogsPagerAdapter()
        val viewSwitcher = logs_view_switcher
        viewSwitcher.adapter = adapter
        toolbar = supportActionBar
        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)

        //TODO: Figure out if there is a cleaner way to do this
        viewSwitcher.addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
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
    }

    class OilFragment: LogsFragment() {
        override fun getList() = model?.vehicles?.value?.get(0)?.oilLogs
    }

    class CoolantFragment: LogsFragment() {
        override fun getList() = model?.vehicles?.value?.get(0)?.coolantLogs
    }

    inner class LogsPagerAdapter: FragmentPagerAdapter(supportFragmentManager) {

        private val fragmentList = listOf(CoolantFragment(), OilFragment(), CoolantFragment())

        override fun getItem(fragmentIndex: Int) = fragmentList[fragmentIndex]

        override fun getCount() = fragmentList.size
    }
}