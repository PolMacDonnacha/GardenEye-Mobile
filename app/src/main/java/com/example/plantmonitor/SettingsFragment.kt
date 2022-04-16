package com.example.plantmonitor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.fanToggle
import kotlinx.android.synthetic.main.fragment_home.imageViewLive
import kotlinx.android.synthetic.main.fragment_home.pumpToggle
import kotlinx.android.synthetic.main.fragment_home.switchAutoFan
import kotlinx.android.synthetic.main.fragment_home.switchAutoPump
import kotlinx.android.synthetic.main.fragment_home.switchAutoTimelapse
import kotlinx.android.synthetic.main.fragment_home.textViewLastWatered
import kotlinx.android.synthetic.main.fragment_home.timelapseToggle
import kotlinx.android.synthetic.main.fragment_home.tvHumidity
import kotlinx.android.synthetic.main.fragment_home.tvLight
import kotlinx.android.synthetic.main.fragment_home.tvMoisture
import kotlinx.android.synthetic.main.fragment_home.tvTemp
import kotlinx.android.synthetic.main.fragment_settings.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Switch
import android.widget.TextView
import android.widget.ToggleButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class SettingsFragment : Fragment() {
    var off = 0
    var on = 1
    var pumpTimeInput = 0
    var fanTimeInput:Int = 0
    var intervalTimeInput: Float? = null
    var lengthTimeInput: Float? = null
    var controls: Controls? = null
    var plot: Plot? = null
    private var database = FirebaseDatabase.getInstance()
    var ref = database.getReference("/")
    var controlRef = database.getReference("/PiDevices/0/control")
    var plotRef = database.getReference("/PiDevices/0/plot")

    var pumpTimeCat: String? = null
    var fanTimeCat:kotlin.String? = null
    var timelapseLengthTimeCat:kotlin.String? = null
    var timelapseIntervalTimeCat:kotlin.String? = null

    override fun onResume() {
        Log.i("RESUMING","Resuming Settings Fragment")
        // Read from the database controls when they change
        controlRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                controls = dataSnapshot.getValue(Controls::class.java)
                Log.d("Firebase", "autoCool is: " + controls!!.autoCool)
                Log.d("Firebase", "autoPump is: " + controls!!.autoPump)
                Log.d("Firebase", "autoTimelapse is: " + controls!!.autoTimelapse)
                Log.d("Firebase", "fanTime is: " + controls!!.fanTime)
                Log.d("Firebase", "fanSwitch is: " + controls!!.fanSwitch)
                Log.d("Firebase", "pumpSwitch is: " + controls!!.pumpSwitch)
                if(etTimelapseLength != null) {
                    autoControls(controls!!.autoPump, controls!!.autoCool, controls!!.autoTimelapse)
                    etTimelapseLength!!.setText(controls!!.timelapseLength.toString())
                    etCoolTime!!.setText(controls!!.fanTime.toString())
                    etFPS!!.setText(controls!!.timelapseFps.toString())
                    etTimelapseInterval!!.setText(controls!!.timelapseInterval.toString())
                    etMaxTemp!!.setText(controls!!.maxTemp.toString())
                    etPumpTime!!.setText(controls!!.pumpTime.toString())
                    etIdealMoisture!!.setText(controls!!.idealSoilMoisture.toString())

                    //CHANGE DROPDOWNS TO SECONDS ON DATA CHANGE
                    SpinnerPump?.setSelection(0)
                    SpinnerFan?.setSelection(0)
                    SpinnerInterval?.setSelection(0)
                    SpinnerLength?.setSelection(0)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("Firebase", "Failed to read value.", error.toException())
            }
        })
        super.onResume()
    }

    override fun onPause() {
        Log.i("PAUSING","Pausing Settings Fragment")
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ArrayAdapter.createFromResource(activity as Context,R.array.time_categories, android.R.layout.simple_spinner_item)
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        // Apply the adapter to the spinner
        SpinnerInterval?.adapter = adapter
        val adapter2 = ArrayAdapter.createFromResource(
            activity as Context,
            R.array.mim_minutes, android.R.layout.simple_spinner_item
        )
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        SpinnerLength?.adapter = adapter2
        SpinnerFan?.adapter = adapter
        SpinnerPump?.adapter = adapter

        switchAutoPump?.setOnClickListener {
            if (switchAutoPump!!.isChecked) {
                controls!!.autoPump = 1
                controls!!.pumpSwitch = 0
            } else {
                controls!!.autoPump = 0
            }
            updateDatabase()
        }
        switchAutoFan?.setOnClickListener {
            Log.i("AUTO_FAN","Auto fan switch clicked")
            if (switchAutoFan!!.isChecked) {
                controls!!.autoCool = 1
                controls!!.fanSwitch = 0
            } else {
                controls!!.autoCool = 0
            }
            updateDatabase()
        }
        switchAutoTimelapse?.setOnClickListener {
            if (switchAutoTimelapse!!.isChecked) {
                controls!!.autoTimelapse = 1
                controls!!.timelapseSwitch = 0
                //timelapseToggle!!.isClickable = false
            } else {
                controls!!.autoTimelapse = 0
                //timelapseToggle!!.isClickable = true
            }
            updateDatabase()
        }

        buttonUpdate?.setOnClickListener{ updateDatabase() }



    }
    fun autoControls(pump: Int, fan: Int, timelapse: Int) {
        switchAutoPump?.isChecked = pump == 1
        switchAutoFan?.isChecked = fan == 1
        switchAutoTimelapse?.isChecked = timelapse == 1
    }



    private fun updateDatabase() {
        pumpTimeInput = etPumpTime!!.text.toString().toInt()
        fanTimeInput = etCoolTime!!.text.toString().toInt()
        intervalTimeInput = etTimelapseInterval!!.text.toString().toFloat()
        lengthTimeInput = etTimelapseLength!!.text.toString().toFloat()
        pumpTimeCat = SpinnerPump?.selectedItem.toString()
        fanTimeCat = SpinnerFan?.selectedItem.toString()
        timelapseIntervalTimeCat = SpinnerInterval?.selectedItem.toString()
        timelapseLengthTimeCat = SpinnerLength?.selectedItem.toString()
        when (fanTimeCat) {
            "seconds" -> {}
            "minutes" -> fanTimeInput *= 60
            "hours" -> fanTimeInput *= 60 * 60
            "days" -> fanTimeInput *= 60 * 60 * 60
        }
        when (timelapseIntervalTimeCat) {
            "seconds" -> {}
            "minutes" -> intervalTimeInput!!.times(60f)
            "hours" -> intervalTimeInput!!.times(60 * 60f)
            "days" -> intervalTimeInput!!.times(60 * 60 * 60f)
        }
        when (timelapseLengthTimeCat) {
            "seconds" -> lengthTimeInput!!.div(60f)
            "minutes" -> {}
            "hours" -> lengthTimeInput!!.times(60 * 60)
            "days" -> lengthTimeInput!!.times(60 * 60 * 60)
        }
        controls!!.fanTime = fanTimeInput
        controls!!.timelapseInterval = intervalTimeInput!!
        controls!!.timelapseLength = lengthTimeInput!!
        controls!!.timelapseFps = etFPS!!.text.toString().toInt()
        controls!!.maxTemp = etMaxTemp!!.text.toString().toInt()
        controlRef.updateChildren(controls!!.toMap()).addOnSuccessListener{
            Log.v("Data Update","Data Sucessfully Updated")
        }.addOnFailureListener { Log.e("Data Update","Failed to update data") }
    }




    class SpinnerActivity : Activity(), AdapterView.OnItemSelectedListener {
        override fun onItemSelected(
            parent: AdapterView<*>, view: View,
            pos: Int, id: Long
        ) {
            // An item was selected. You can retrieve the selected item using
            parent.getItemAtPosition(pos)
            Log.i("Selected Item", "Item Selected: $pos")
            Log.i("Selected Item", "Item Selected: $parent")
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Nothing happens
        }
    }




        override fun onCreateView(
        inflator: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
            Log.v("Fragment Create","Returning the layout")
        return inflator.inflate(R.layout.fragment_settings, container, false)
    }
}