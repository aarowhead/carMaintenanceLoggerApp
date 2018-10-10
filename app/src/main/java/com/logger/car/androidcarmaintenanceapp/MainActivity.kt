package com.logger.car.androidcarmaintenanceapp

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.logger.car.androidcarmaintenanceapp.domain.Vehicle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dashboard_cardview.view.*

class MainActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val model = ViewModelProviders.of(this).get(MainViewModel::class.java)
        val adapter = VehicleAdapter(model.vehicles.value)
        //TODO: look into using an apply
        cars_recycler.adapter = adapter
        cars_recycler.layoutManager = LinearLayoutManager(this)
    }

    class VehicleAdapter(var vehicles: List<Vehicle>?) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

        class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
                VehicleViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.dashboard_cardview, parent, false))

        override fun getItemCount() = vehicles?.size ?: 0

        override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
            //TODO do this better
            holder.itemView.car_name.text = vehicles?.let {"${it[position].make} ${it[position].model}" }
            holder.itemView.status.text = "Testing"
        }
    }
}


