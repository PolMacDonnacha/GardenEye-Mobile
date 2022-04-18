package com.example.plantmonitor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_home.*


class HomeFragment : Fragment() {


    var main: MainActivity? = MainActivity()
    private var controlRef:DatabaseReference? = null
    private var plotRef:DatabaseReference? = null
    var controls: Controls? = null
    var plot: Plot? = null
    private var storage = FirebaseStorage.getInstance()
    var storageRef = storage.reference
    var liveRef: StorageReference = storageRef.child("liveImage.jpg")

    override fun onResume() {
        Log.i("RESUMING","Resuming Home Fragment")
        Toast.makeText(activity, "Loading data. Please wait..", Toast.LENGTH_LONG).show()
        // Read from the database controls when they change
        controlRef?.addValueEventListener(object : ValueEventListener {
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
                Log.d("Firebase", "timelapseSwitch is: " + controls!!.timelapseSwitch)
                autoControls(controls!!.autoPump, controls!!.autoCool, controls!!.autoTimelapse)
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("Firebase", "Failed to read value.", error.toException())
            }
        })
        // Read from the database
        plotRef?.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(dataSnapshot:DataSnapshot ) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                plot = dataSnapshot.getValue(Plot::class.java)
                Log.d("Firebase", "Temperature is: " + plot!!.Temperature)
                Log.d("Firebase", "Humidity is: " + plot!!.Humidity)
                Log.d("Firebase", "Moisture is: " + plot!!.SoilMoisture)
                Log.d("Firebase", "Light is: " + plot!!.LightLevel)
                Log.d("Firebase", "Time Watered is: " + plot!!.TimeWatered)
                //If the plot data elements are available, fill in info
                if(tvTemp!= null) {
                    tvTemp.text = getString(R.string.temperature,plot!!.Temperature.toString())
                    tvHumidity.text = getString(R.string.humidity,plot!!.Humidity.toString())
                    tvMoisture.text = getString(R.string.moisture,plot!!.SoilMoisture.toString())
                    tvLight.text = getString(R.string.light,plot!!.LightLevel.toString())
                    textViewLastWatered.text = plot!!.TimeWatered
                }
                liveRef.downloadUrl.addOnSuccessListener { uri ->
                    Log.d("Firebase", "Image URI is: $uri")
                    Picasso.with(activity).invalidate(uri)//Prevents image not downloading
                    var img = Picasso.with(activity).load(uri).networkPolicy(NetworkPolicy.NO_STORE)
                        .memoryPolicy(
                            MemoryPolicy.NO_STORE,
                            MemoryPolicy.NO_CACHE
                        )
                    if(imageViewLive != null){
                    img.fit().centerCrop().into(imageViewLive)
                    }
                    //net policy :      NetworkPolicy.NO_CACHE
                }.addOnFailureListener {
                    Toast.makeText(activity,"There was a problem displaying the image on the homepage",
                        Toast.LENGTH_SHORT).show()
                    Log.w("Image Error","Failed to show image on homepage")
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("Firebase", "Failed to read value.", error.toException())
            }
        })
        super.onResume()
    }

    override fun onPause() {
        Log.i("PAUSING","Pausing Home Fragment")
        super.onPause()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        controlRef = main?.database?.getReference("/PiDevices/0/control")
        plotRef = main?.database?.getReference("/PiDevices/0/plot")
    }
    private fun updateDatabase() {
        Log.v("Controls",controls?.toMap().toString())
        controlRef!!.updateChildren(controls!!.toMap())
    }




    override fun onCreateView(
        inflator: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflator.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        pumpToggle?.setOnClickListener {
            if (pumpToggle!!.isChecked) {
                controls!!.pumpSwitch = 1
                Log.v("PUMP","Pump switched to ON")
            } else {
                controls!!.pumpSwitch = 0
                Log.v("PUMP","Pump switched to OFF")
            }
            updateDatabase()
        }
        fanToggle?.setOnClickListener {
            if (fanToggle!!.isChecked) {
                controls!!.fanSwitch = 1
                Log.v("FAN","Fan switched to ON")
            } else {
                controls!!.fanSwitch = 0
                Log.v("FAN","Fan switched to OFF")
            }
            updateDatabase()
        }
        timelapseToggle?.setOnClickListener {
            if (timelapseToggle!!.isChecked) {
                controls!!.timelapseSwitch = 1
                Log.v("TIMELAPSE","Timelapse switched to ON")
            } else {
                controls!!.timelapseSwitch = 0
                Log.v("TIMELAPSE","Timelapse switched to OFF")
            }
            updateDatabase()
        }
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

    }


    fun autoControls(pump: Int, fan: Int, timelapse: Int) {
        if (pump == 1){              // When pump is set to automatic
            switchAutoPump?.isChecked = true         //Set auto switch to checked
            pumpToggle?.isChecked = false            //Set toggle switch to unchecked
            pumpToggle?.isClickable = false          //Make toggle switch to unclickable
        }
        else if(controls!!.pumpSwitch == 1){         // When pump is not set to automatic, check if the manual switch is on
            switchAutoPump?.isChecked = false
            pumpToggle?.isChecked = true
            pumpToggle?.isClickable = true
        }
        else{       //Automatic and manual switches are off
            switchAutoPump?.isChecked = false
            pumpToggle?.isChecked = false
            pumpToggle?.isClickable = true
        }

        if (fan == 1){              // When fan is set to automatic
            switchAutoFan?.isChecked = true         //Set auto switch to checked
            fanToggle?.isChecked = false            //Set toggle switch to unchecked
            fanToggle?.isClickable = false          //Make toggle switch to unclickable
        }
        else if(controls!!.fanSwitch == 1){         // When fan is not set to automatic, check if the manual switch is on
            switchAutoFan?.isChecked = false
            fanToggle?.isChecked = true
            fanToggle?.isClickable = true
        }
        else{       //Automatic and manual switches are off
            switchAutoFan?.isChecked = false
            fanToggle?.isChecked = false
            fanToggle?.isClickable = true
        }

        if (timelapse == 1){              // When camera is set to automatic
            switchAutoTimelapse?.isChecked = true         //Set auto switch to checked
            timelapseToggle?.isChecked = false            //Set toggle switch to unchecked
            timelapseToggle?.isClickable = false          //Make toggle switch to unclickable
        }
        else if(controls!!.timelapseSwitch == 1){         // When camera is not set to automatic, check if the manual switch is on
            switchAutoTimelapse?.isChecked = false
            timelapseToggle?.isChecked = true
            timelapseToggle?.isClickable = true
        }
        else{       //Automatic and manual switches are off
            switchAutoTimelapse?.isChecked = false
            timelapseToggle?.isChecked = false
            timelapseToggle?.isClickable = true
        }
    }

}