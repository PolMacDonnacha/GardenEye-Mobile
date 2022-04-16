package com.example.plantmonitor

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.plantmonitor.MyPlantRecyclerViewAdapter.Listener
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_plant_list.*

/**
 * A fragment representing a list of Plants.
 */
class PlantFragment : Fragment(), Listener {

    private var columnCount = 2
    private lateinit var plantAdapter: MyPlantRecyclerViewAdapter
    private lateinit var recycler: RecyclerView
    private var allPlants: MutableList<Plant> = mutableListOf()
    private var showPlants: MutableList<Plant> = mutableListOf()
    var myView: View? = null //For updating adapter with list of search results

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onResume() {
        if(btnSearch != null){
            btnSearch.setOnClickListener {
                showPlants.clear()
                if (etPlantName != null) {
                    val plantName = etPlantName.text
                    if (plantName.isNotEmpty()) {
                        for (plant in allPlants) {
                            if (plant.name.toString().contains(plantName, ignoreCase = true)) {
                                Log.v("Selected plant", plant.toString())
                                showPlants.add(plant)
                            }
                        }
                        recycler.adapter!!.notifyDataSetChanged()
                    } else {
                        showPlants.addAll(allPlants)
                        recycler.adapter!!.notifyDataSetChanged()
                        //return the whole list again
                    }
                }
            }
        }
        if(btnNewPlant != null){
            btnNewPlant.setOnClickListener{
                val addPlantFragment: Fragment = AddPlantFragment.newInstance()
                val transaction = activity?.supportFragmentManager!!.beginTransaction()
                transaction.replace(R.id.fragment_container, addPlantFragment).addToBackStack(null).commit();
            }
        }
        super.onResume()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_plant_list, container, false)
        allPlants = mutableListOf<Plant>()
        showPlants = mutableListOf<Plant>()

        val plantsReference = FirebaseDatabase.getInstance().getReference("Plants")
        Toast.makeText(activity, "Loading..", Toast.LENGTH_SHORT).show()

        plantsReference.get().addOnSuccessListener {
            if (it.exists()) {
                var plantsList = it.children.toList()
                Log.v("plantsList", plantsList.toString())
                Log.v("Number of plant objects", plantsList.size.toString())

                for (plantItem in plantsList) {
                    Log.v("Name", plantItem.key.toString())
                    var name = plantItem.key.toString()
                    if (plantItem != null) {
                        // for (plant in plantList){
                        Log.v("Plant properties", plantItem.value.toString())
                        var ePlantingMonth = plantItem.child("earliestPlantMonth").value.toString()
                        var lPlantingMonth = plantItem.child("latestPlantMonth").value.toString()
                        var dToSprout =
                            plantItem.child("daysToSprout").value.toString().toIntOrNull()
                        var dToMaturity =
                            plantItem.child("daysToMaturity").value.toString().toIntOrNull()
                        var seedDepth =
                            plantItem.child("seedDepthCm").value.toString().toDoubleOrNull()
                        var seedSpacing =
                            plantItem.child("seedSpacingCm").value.toString().toDoubleOrNull()
                        var plantSpacing =
                            plantItem.child("plantSpacingCm").value.toString().toDoubleOrNull()
                        var rowSpacing =
                            plantItem.child("rowSpacingCm").value.toString().toDoubleOrNull()
                        var moisture =
                            plantItem.child("idealMoisture").value.toString().toIntOrNull()
                        var lightHours =
                            plantItem.child("idealLightHours").value.toString().toIntOrNull()
                        var ph = plantItem.child("ph").value.toString().toDoubleOrNull()
                        var nitrogen = plantItem.child("nitrogen").value.toString().toDoubleOrNull()
                        var phosphorous =
                            plantItem.child("phosphorous").value.toString().toDoubleOrNull()
                        var potassium = plantItem.child("potassium").value.toString().toDoubleOrNull()
                        var idealAirTemp =
                            plantItem.child("idealAirTemp").value.toString().toDoubleOrNull()
                        var idealSoilTemp =
                            plantItem.child("idealSoilTemp").value.toString().toDoubleOrNull()

                        var humidity = plantItem.child("humidity").value.toString().toIntOrNull()

                        //Convert to string, remove square brackets and split by the commas
                        var companions =
                            plantItem.child("companions").value.toString().replace("[", "")
                                .replace("]", "").split(',')
                        var rivals = plantItem.child("rivals").value.toString().replace("[", "")
                            .replace("]", "").split(',')

                        allPlants.add(
                            Plant(
                                name, ePlantingMonth, lPlantingMonth, dToSprout, dToMaturity, seedDepth,
                                seedSpacing, plantSpacing, rowSpacing, moisture, lightHours, ph,
                                nitrogen, phosphorous, potassium, idealAirTemp, idealSoilTemp, humidity,
                                companions, rivals
                            )
                        )
                    }
                }
            }
            else {
                Toast.makeText(activity, "Plants reference does not exist", Toast.LENGTH_SHORT)
                    .show()
            }
            showPlants.addAll(allPlants)
            startPlantRecyler(view)
        }.addOnFailureListener {
            Toast.makeText(activity, "Failed to retrieve plant data", Toast.LENGTH_SHORT).show()

        }

        return view
    }

    private fun startPlantRecyler(view: View) {
        Log.d("PLANT_RECYCLER", "Starting recycler")
        recycler = view.findViewById(R.id.plantListRecycler)
        recycler.layoutManager = LinearLayoutManager(activity)
        plantAdapter = MyPlantRecyclerViewAdapter(showPlants, this)
        recycler.adapter = plantAdapter
    }


    companion object {
        const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance(columnCount: Int) =
            PlantFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }

    override fun onItemClick(plant: Plant) {
        //Toast.makeText(activity, "${plant.name} selected", Toast.LENGTH_SHORT).show()
        Log.i("Plant_Selected", "${plant.name} selected")
        val detailFragment: Fragment = PlantDetailsFragment.newInstance(
            plant.name, plant.earliestPlantMonth,
            plant.latestPlantMonth, plant.daysToSprout,
            plant.daysToMaturity, plant.pH, plant.seedDepthCm,
            plant.seedSpacingCm, plant.plantSpacingCm, plant.rowSpacingCm,
            plant.idealMoisture, plant.idealLightHours, plant.idealAirTemp,
            plant.idealSoilTemp, plant.humidity, plant.nitrogen, plant.phosphorous,
            plant.potassium, plant.companions, plant.rivals
        )
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.replace(R.id.fragment_container, detailFragment).addToBackStack(null).commit();

    }
}