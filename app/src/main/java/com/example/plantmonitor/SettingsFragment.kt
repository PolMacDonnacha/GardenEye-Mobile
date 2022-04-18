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
import kotlin.math.roundToInt


class SettingsFragment : Fragment() {
    var off = 0
    var on = 1
    var pumpTimeInput = 0
    var fanTimeInput: Int = 0
    var intervalTimeInput: Int = 0
    var lengthTimeInput: Int = 0
    var controls: Controls? = null
    var plot: Plot? = null
    private var database = FirebaseDatabase.getInstance()
    var ref = database.getReference("/")
    var controlRef = database.getReference("/PiDevices/0/control")
    var plotRef = database.getReference("/PiDevices/0/plot")

    var pumpTimeCat: String? = null
    var fanTimeCat: String? = null
    var timelapseLengthTimeCat: String? = null
    var timelapseIntervalTimeCat: String? = null

    override fun onResume() {
        Log.i("RESUMING", "Resuming Settings Fragment")
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
                if (etTimelapseLength != null) {
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
        Log.i("PAUSING", "Pausing Settings Fragment")
        super.onPause()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val time_categories = ArrayAdapter.createFromResource(
            activity as Context,
            R.array.time_categories,
            android.R.layout.simple_spinner_item
        )

        // Apply the adapter to the spinner
        SpinnerInterval?.adapter = time_categories
        val mim_minutes = ArrayAdapter.createFromResource(
            activity as Context,
            R.array.mim_minutes,
            android.R.layout.simple_spinner_item
        )
        val seconds_and_minutes = ArrayAdapter.createFromResource(
            activity as Context,
            R.array.seconds_and_minutes,
            android.R.layout.simple_spinner_item
        )

        SpinnerLength?.adapter = mim_minutes
        SpinnerFan?.adapter = seconds_and_minutes
        SpinnerPump?.adapter = seconds_and_minutes

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
            Log.i("AUTO_FAN", "Auto fan switch clicked")
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

        buttonUpdate?.setOnClickListener {
            //If the page is still open
            if (etPumpTime != null) {
                val inputPumpTime = etPumpTime.text.toString().toIntOrNull()
                val inputFanTime = etCoolTime.text.toString().toIntOrNull()
                val inputIntervalTime = etTimelapseInterval.text.toString().toIntOrNull()
                val inputLengthTime = etTimelapseLength.text.toString().toIntOrNull()
                val inputFPS = etFPS.text.toString().toIntOrNull()
                val inputMaxTemp = etMaxTemp.text.toString().toIntOrNull()
                pumpTimeCat = SpinnerPump?.selectedItem.toString()
                fanTimeCat = SpinnerFan?.selectedItem.toString()
                timelapseIntervalTimeCat = SpinnerInterval?.selectedItem.toString()
                timelapseLengthTimeCat = SpinnerLength?.selectedItem.toString()
                //Is the pump input valid?
                if (inputPumpTime != null && inputPumpTime > 0 && inputPumpTime < 150) {
                    controls!!.pumpTime = secondsConversion(inputPumpTime, pumpTimeCat!!)

                    //Is the fan input valid?
                    if (inputFanTime != null && inputFanTime > 0 && inputFanTime < 150) {
                        controls!!.fanTime = secondsConversion(inputFanTime, fanTimeCat!!)

                        //Is the timelapse interval input valid?
                        if (inputIntervalTime != null && inputIntervalTime > 0 && inputIntervalTime < 150) {
                            controls!!.timelapseInterval = secondsConversion(inputIntervalTime, timelapseIntervalTimeCat!!)

                            //Is the timelapse length input valid?
                            if (inputLengthTime != null && inputLengthTime > 0 && inputLengthTime < 150) {
                                controls!!.timelapseLength = minutesConversion(inputLengthTime, timelapseIntervalTimeCat!!)

                                //Is the timelapse fps input valid?
                                if (inputFPS != null && inputFPS > 0 && inputFPS <= 60) {
                                    controls!!.timelapseFps = inputFPS

                                    //Is the max temperature input valid?
                                    if (inputMaxTemp != null && inputMaxTemp > 5 && inputMaxTemp <= 50) {
                                        controls!!.maxTemp = inputMaxTemp

                                        //Update when all inputs are valid
                                        updateDatabase()
                                        Toast.makeText(activity, "Updating data", Toast.LENGTH_SHORT).show()

                                    } else { Toast.makeText(activity, "Invalid max temperature input", Toast.LENGTH_SHORT).show() }
                                } else { Toast.makeText(activity, "Invalid frames per second input", Toast.LENGTH_SHORT).show() }
                            } else { Toast.makeText(activity, "Invalid timelapse length input", Toast.LENGTH_SHORT).show() }
                        } else { Toast.makeText(activity, "Invalid timelapse interval input",Toast.LENGTH_SHORT).show() }
                    } else { Toast.makeText(activity, "Invalid fan input", Toast.LENGTH_SHORT).show() }
                } else { Toast.makeText(activity, "Invalid pump input", Toast.LENGTH_SHORT).show() }
            }
        }


    }

    fun autoControls(pump: Int, fan: Int, timelapse: Int) {
        switchAutoPump?.isChecked = pump == 1
        switchAutoFan?.isChecked = fan == 1
        switchAutoTimelapse?.isChecked = timelapse == 1
    }


    private fun updateDatabase() {
        controlRef.updateChildren(controls!!.toMap()).addOnSuccessListener {
            Log.v("Data Update", "Data Sucessfully Updated")
        }.addOnFailureListener { Log.e("Data Update", "Failed to update data") }
    }

    fun secondsConversion(inputSeconds: Int, timeCategory: String): Int {
        when (timeCategory) {
            "seconds" -> {}
            "minutes" -> inputSeconds.times(60f)
            "hours" -> inputSeconds.times(60 * 60f)
            "days" -> inputSeconds.times(60 * 60 * 60f)
        }
        return inputSeconds
    }

    fun minutesConversion(inputMinutes: Int, timeCategory: String): Int {
        var newTime = 0f
        when (timeCategory) {
            "seconds" -> newTime = inputMinutes.div(60f)
            "minutes" -> {
                newTime = inputMinutes.toFloat()
            }
            "hours" -> newTime = inputMinutes.times(60 * 60f)
            "days" -> newTime = inputMinutes.times(60 * 60 * 60f)
        }
        return newTime.roundToInt()
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
        Log.v("Fragment Create", "Returning the layout")
        return inflator.inflate(R.layout.fragment_settings, container, false)
    }
}