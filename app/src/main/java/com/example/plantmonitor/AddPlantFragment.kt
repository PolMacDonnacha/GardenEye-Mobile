package com.example.plantmonitor

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add_plant.*

class AddPlantFragment : Fragment() {
    private var plantName: String? = null
    private var earliestPlanting: String? = null
    private var latestPlanting: String? = null
    private var daysToSprout: Int? = null
    private var daysToMature: Int? = null
    private var ph: Double? = null
    private var seedDepth: Double? = null
    private var seedApart: Double? = null
    private var transplantSpace: Double? = null
    private var rowSpace: Double? = null
    private var soilMoisture: Int? = null
    private var lightNeeded: Int? = null
    private var airTemp: Double? = null
    private var soilTemp: Double? = null
    private var humidity: Int? = null
    private var nitrogen: Double? = null
    private var phosphorous: Double? = null
    private var potassium: Double? = null
    private var companions: List<String>? = null
    private var rivals: List<String>? = null
    private var plant: Plant? = null
    var plantsReference: DatabaseReference? = null

        override fun onResume() {
            if(btnAdd != null){
                btnAdd.setOnClickListener{
                    if(etName != null){
                        if (etName.text.isNotEmpty() && etEarliestPlanting.text.isNotEmpty() &&
                            etLatestPlanting.text.isNotEmpty() && etPlantMoisture.text.isNotEmpty() && etAirTemp.text.isNotEmpty()){
                            plantName = etName.text.toString()
                            earliestPlanting = etEarliestPlanting.text.toString()
                            latestPlanting = etLatestPlanting.text.toString()
                            daysToSprout = etDaysToSprout.text.toString().toIntOrNull()
                            daysToMature = etDaysToMature.text.toString().toIntOrNull()
                            ph = etPH.text.toString().toDoubleOrNull()
                            seedDepth = etSeedDepth.text.toString().toDoubleOrNull()
                            seedApart = etSeedApart.text.toString().toDoubleOrNull()
                            transplantSpace = etTransplantSpacing.text.toString().toDoubleOrNull()
                            rowSpace = etRowSpacing.text.toString().toDoubleOrNull()
                            soilMoisture = etPlantMoisture.text.toString().toIntOrNull()
                            lightNeeded = etPlantLightHours.text.toString().toIntOrNull()
                            airTemp = etAirTemp.text.toString().toDoubleOrNull()
                            soilTemp = etSoilTemp.text.toString().toDoubleOrNull()
                            humidity = etPlantHumidity.text.toString().toIntOrNull()
                            nitrogen = etNitrogen.text.toString().toDoubleOrNull()
                            phosphorous = etPhosphorous.text.toString().toDoubleOrNull()
                            potassium = etPotassium.text.toString().toDoubleOrNull()
                            if(etCompanions.text.isNotEmpty()){
                                companions = if(etCompanions.text.toString().contains(',')){
                                    etCompanions.text.toString().split(',')
                                } else{
                                    listOf(etCompanions.text.toString())
                                }
                            }
                            if(etRival.text.isNotEmpty()){
                                rivals = if(etRival.text.toString().contains(',')){
                                    etRival.text.toString().split(',')
                                } else{
                                    listOf(etRival.text.toString())
                                }
                            }
                            plant = Plant(plantName,earliestPlanting,latestPlanting,daysToSprout,daysToMature,seedDepth,seedApart,transplantSpace,
                            rowSpace,soilMoisture,lightNeeded,ph,nitrogen,phosphorous,potassium,airTemp,soilTemp,humidity,companions,rivals)

                            plantsReference!!.child(plant!!.name!!).setValue(plant).addOnSuccessListener{
                                Toast.makeText(activity, "Plant added successfully", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(activity, "Failed to add plant, check network connection", Toast.LENGTH_SHORT).show()
                            }
                        }
                        else{
                            Toast.makeText(activity, "Please fill out the required fields", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                if(btnClear != null){
                    btnClear.setOnClickListener{
                        if(etName != null){
                            etName.text = null
                            etEarliestPlanting.text = null
                            etLatestPlanting.text = null
                            etDaysToSprout.text = null
                            etDaysToMature.text = null
                            etPH.text = null
                            etSeedDepth.text = null
                            etSeedApart.text = null
                            etTransplantSpacing.text = null
                            etRowSpacing.text = null
                            etPlantMoisture.text = null
                            etPlantLightHours.text = null
                            etAirTemp.text = null
                            etSoilTemp.text = null
                            etPlantHumidity.text = null
                            etNitrogen.text = null
                            etPhosphorous.text = null
                            etPotassium.text = null
                            etCompanions.text = null
                            etRival.text = null
                        }
                    }
                }

            }
            super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        plantsReference = FirebaseDatabase.getInstance().getReference("Plants")
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_plant, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            AddPlantFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }
}