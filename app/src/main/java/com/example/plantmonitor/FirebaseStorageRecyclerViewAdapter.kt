package com.example.plantmonitor

import Timelapse
import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import com.example.plantmonitor.placeholder.PlaceholderContent.PlaceholderItem


class FirebaseStorageRecyclerViewAdapter(
    private val values: MutableList<Timelapse>,
    private val listener: VideoListener
) : RecyclerView.Adapter<FirebaseStorageRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.fragment_timelapse, parent, false
            )
        )
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val timelapseName: TextView = view.findViewById(R.id.timelapseName)

    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.timelapseName.text = item.name
        holder.itemView.setOnClickListener {
            //Check if the selected item has a position
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(values[position])
            }
        }
    }
    interface VideoListener{
        fun onItemClick(timelapse: Timelapse)
    }
}