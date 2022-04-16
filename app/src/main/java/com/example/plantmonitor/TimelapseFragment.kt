package com.example.plantmonitor

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.plantmonitor.placeholder.PlaceholderContent
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.google.firebase.storage.ktx.component1
import Timelapse
import android.widget.Toast
import kotlinx.coroutines.awaitAll

/**
 * A fragment representing a list of Timelapses.
 */
class TimelapseFragment : Fragment(), FirebaseStorageRecyclerViewAdapter.VideoListener {

    private var columnCount = 1
    private var timelapseRef: StorageReference? = null
    private lateinit var plantAdapter: FirebaseStorageRecyclerViewAdapter
    private lateinit var recycler: RecyclerView
    private var timelapseList = mutableListOf<Timelapse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val storage = Firebase.storage
        timelapseRef = storage.reference.child("Timelapses")

        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
    }

    override fun onResume() {
        super.onResume()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_timelapse_list, container, false)

        Toast.makeText(activity, "Loading video names", Toast.LENGTH_SHORT).show()
        timelapseList = mutableListOf<Timelapse>()
        timelapseRef!!.listAll()
            .addOnSuccessListener { (items) ->
                Log.d("Firebase_Storage","Successful listAll!")
                items.forEach { item ->
                    Log.d("ITEM Name ==============",item.name)
                    // All the items under listRef.
                    var timelapse = Timelapse(item.name)
                    timelapseList.add(timelapse)
                    Log.i("LIST_REF_Timelapse",timelapse.toString())
                }
                startTimelapseRecycler(view)
            }
            .addOnFailureListener {
                Toast.makeText(activity,"There was a problem loading Firebase Storage data",Toast.LENGTH_SHORT).show()
            }
        Log.d("timelapseList","List is empty == " + timelapseList.isNullOrEmpty())
        return view
    }

    private fun startTimelapseRecycler(view: View) {
        Log.d("PLANT_RECYCLER", "Starting recycler")
        recycler = view.findViewById(R.id.timelapseListRecycler)
        recycler.layoutManager = LinearLayoutManager(activity)
        plantAdapter = FirebaseStorageRecyclerViewAdapter(timelapseList, this)
        recycler.adapter = plantAdapter
    }

    companion object {

        const val ARG_COLUMN_COUNT = "column-count"
        @JvmStatic
        fun newInstance(columnCount: Int) =
            TimelapseFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
    override fun onItemClick(timelapse: Timelapse) {
        Toast.makeText(activity, "${timelapse.name} selected", Toast.LENGTH_SHORT).show()
        Log.i("Timelapse Selected", "${timelapse.name} selected")
        val videoFragment: Fragment = VideoFragment.newInstance(
            timelapse.name
        )
        val transaction = activity?.supportFragmentManager!!.beginTransaction()
        transaction.replace(R.id.fragment_container, videoFragment).addToBackStack(null).commit();

    }
}