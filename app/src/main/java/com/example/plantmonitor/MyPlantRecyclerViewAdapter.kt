package com.example.plantmonitor

import android.content.Context
import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import kotlinx.android.synthetic.main.fragment_home.*


class MyPlantRecyclerViewAdapter(
    private val values: MutableList<Plant>,
    private val listener: Listener
) : RecyclerView.Adapter<MyPlantRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_plant, parent, false
            )
        )
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.tvPlantName)
        val season: TextView = view.findViewById(R.id.tvSeason)
        override fun toString(): String {
            return super.toString() + " '" + nameView.text + "'"
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.nameView.text = item.name
        holder.season.text = "${item.earliestPlantMonth.toString()} - ${item.latestPlantMonth.toString()}"
        holder.itemView.setOnClickListener {
            //Check if the selected item has a position
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(values[position])
            }
        }
    }

    interface Listener {
        fun onItemClick(plant: Plant)

    }
}