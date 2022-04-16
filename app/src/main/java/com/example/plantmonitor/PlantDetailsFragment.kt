package com.example.plantmonitor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_plant_details.*
import kotlinx.android.synthetic.main.fragment_settings.*


class PlantDetailsFragment : Fragment() {
    private var plantName: String? = null
    private var earliestPlanting: String? = null
    private var latestPlanting: String? = null
    private var daysToSprout: String? = null
    private var daysToMature: String? = null
    private var ph: String? = null
    private var seedDepth: String? = null
    private var seedApart: String? = null
    private var transplantSpace: String? = null
    private var rowSpace: String? = null
    private var soilMoisture: String? = null
    private var lightNeeded: String? = null
    private var airTemp: String? = null
    private var soilTemp: String? = null
    private var humidity: String? = null
    private var nitrogen: String? = null
    private var phosphorous: String? = null
    private var potassium: String? = null
    private var companions: String? = null
    private var rivals: String? = null
    private var controlRef: DatabaseReference? = null
    var controls: Controls? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var main: MainActivity? = MainActivity()
        controlRef = main?.database?.getReference("/PiDevices/0/control")
        arguments?.let {
            plantName = it.getString("name")
            earliestPlanting = it.getString("earliest")
            latestPlanting = it.getString("latest")
            daysToSprout = it.getString("sproutDays")
            daysToMature = it.getString("matureDays")
            ph = it.getString("ph")
            seedDepth = it.getString("depth")
            seedApart = it.getString("seedSpacing")
            transplantSpace = it.getString("plantSpacing")
            rowSpace = it.getString("rowSpacing")
            soilMoisture = it.getString("moisture")
            lightNeeded = it.getString("light")
            airTemp = it.getString("airTemp")
            soilTemp = it.getString("soilTemp")
            humidity = it.getString("humidity")
            nitrogen = it.getString("nitrogen")
            phosphorous = it.getString("phosphorous")
            potassium = it.getString("potassium")
            companions = it.getString("companions")
            rivals = it.getString("rivals")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_plant_details, container, false)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvEarliestPlanting = view.findViewById<TextView>(R.id.tvEarliestPlanting)
        val tvLatestPlanting = view.findViewById<TextView>(R.id.tvLatestPlanting)
        val tvDaysToSprout = view.findViewById<TextView>(R.id.tvDaysToSprout)
        val tvDaysToMature = view.findViewById<TextView>(R.id.tvDaysToMature)
        val tvPH = view.findViewById<TextView>(R.id.tvPH)
        val tvSeedDepth = view.findViewById<TextView>(R.id.tvSeedDepth)
        val tvTransplantSpacing = view.findViewById<TextView>(R.id.tvTransplantSpacing)
        val tvRowSpacing = view.findViewById<TextView>(R.id.tvRowSpacing)
        val tvPlantMoisture = view.findViewById<TextView>(R.id.tvPlantMoisture)
        val tvPlantLightHours = view.findViewById<TextView>(R.id.tvPlantLightHours)
        val tvAirTemp = view.findViewById<TextView>(R.id.tvAirTemp)
        val tvSoilTemp = view.findViewById<TextView>(R.id.tvSoilTemp)
        val tvPlantHumidity = view.findViewById<TextView>(R.id.tvPlantHumidity)
        val tvFertiliser = view.findViewById<TextView>(R.id.tvFertiliser)
        val tvCompanions = view.findViewById<TextView>(R.id.tvCompanions)
        val tvRival = view.findViewById<TextView>(R.id.tvRival)
        if(tvName != null){
        Log.d("PlantName",plantName!!)
            tvName.text = plantName
            tvEarliestPlanting.text = earliestPlanting
            tvLatestPlanting.text = latestPlanting
            tvDaysToSprout.text = daysToSprout
            tvDaysToMature.text = daysToMature
            tvPH.text = ph
            tvSeedDepth.text = getString(R.string.SeedDepthandSpace,seedDepth,seedApart)
            tvTransplantSpacing.text = getString(R.string.plantSpace,transplantSpace)
            tvRowSpacing.text = getString(R.string.rowSpace,rowSpace)
            tvPlantMoisture.text = getString(R.string.moisture,soilMoisture)
            tvPlantLightHours.text = lightNeeded
            tvAirTemp.text = getString(R.string.temperature,airTemp)
            tvSoilTemp.text = getString(R.string.temperature,soilTemp)
            tvPlantHumidity.text = getString(R.string.humidity,humidity)
            tvFertiliser.text = getString(R.string.nutrients,nitrogen,phosphorous,potassium)
            tvCompanions.text = companions
            tvRival.text = rivals
        }
        else
        {
            Toast.makeText(activity, "Something went wrong in displaying the data", Toast.LENGTH_SHORT).show()
        }
        return view
    }
    override fun onResume() {
        Log.i("RESUMING","Resuming PlantDetails Fragment")

        btnGrow.setOnClickListener{
            val retrievedTemperature :Int? = tvAirTemp.text.toString().replace("C","").toIntOrNull()
            val retrievedMoisture :Int? = tvPlantMoisture.text.toString().replace("%","").toIntOrNull()
            //Are the variables okay to use?
            if(retrievedTemperature != null && retrievedMoisture != null){
            controls!!.maxTemp = retrievedTemperature
            controls!!.idealSoilMoisture = retrievedMoisture
            Toast.makeText(activity, "Gardening system tending to $plantName", Toast.LENGTH_SHORT).show()
            updateDatabase()
            }
            else{
                Toast.makeText(activity, "Problem using this plant's data, please check the plant's information", Toast.LENGTH_LONG).show()
            }
        }

        // Read from the database controls when they change
        controlRef?.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                controls = dataSnapshot.getValue(Controls::class.java)
                Log.d("Firebase Plant Details", "Data Change Detected in Controls")

            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
                Log.d("Firebase", "Failed to read value.", error.toException())
            }
        })
        super.onResume()
    }
    private fun updateDatabase() {
        controlRef!!.updateChildren(controls!!.toMap())
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String?, earliest: String?, latest: String?, sproutDays: Int?, matureDays: Int?,
                        ph: Double?,depth: Double?,seedSpacing: Double?,plantSpacing: Double?,rowSpacing: Double?,moisture: Int?,light: Int?,
                        airTemp: Double?,soilTemp: Double?,humidity: Int?,nitrogen: Double?,phosphorous: Double?,potassium: Double?,companions: List<String>?,rivals: List<String>?) =
            PlantDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString("name", name)
                    putString("earliest", earliest)
                    putString("latest", latest)
                    putString("sproutDays", sproutDays.toString())
                    putString("matureDays", matureDays.toString())
                    putString("ph", ph.toString())
                    putString("depth", depth.toString())
                    putString("seedSpacing", seedSpacing.toString())
                    putString("plantSpacing", plantSpacing.toString())
                    putString("rowSpacing", rowSpacing.toString())
                    putString("moisture", moisture.toString())
                    putString("light", light.toString())
                    putString("airTemp", airTemp.toString())
                    putString("soilTemp", soilTemp.toString())
                    putString("humidity", humidity.toString())
                    putString("nitrogen", nitrogen.toString())
                    putString("phosphorous", phosphorous.toString())
                    putString("potassium", potassium.toString())
                    putString("companions", companions.toString())
                    putString("rivals", rivals.toString())
                }
            }
    }
}