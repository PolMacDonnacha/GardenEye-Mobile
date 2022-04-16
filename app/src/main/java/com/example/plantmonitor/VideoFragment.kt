package com.example.plantmonitor

import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import kotlinx.android.synthetic.main.fragment_video.*
import kotlinx.android.synthetic.main.timelapses.*
import java.net.URI

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "name"


class VideoFragment : Fragment() {
    private var videoName: String? = null
    var mediaController: MediaController? = null
    private var timelapseRef: StorageReference? = null
    var streamURI: Uri? = null
    private var storage: FirebaseStorage? = null

    override fun onResume() {
        btnDelete.setOnClickListener{
            if(streamURI != null){
                timelapseRef!!.delete().addOnSuccessListener{
                    Toast.makeText(activity, "$videoName deleted successfully", Toast.LENGTH_SHORT).show()
                    requireActivity().onBackPressed()
                }.addOnFailureListener {
                    Toast.makeText(activity, "Failed to delete $videoName", Toast.LENGTH_SHORT).show()
                }
            }
        }
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        storage = Firebase.storage
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        arguments?.let {
            videoName = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_video, container, false)
        timelapseRef = storage!!.reference.child("Timelapses/$videoName")

        timelapseRef!!.downloadUrl.addOnSuccessListener { uri ->
            streamURI = uri
            Log.d("Firebase", "Timelapse URI is: $uri")

            if (videoTitle != null) {
                var timelapseVideo = view.findViewById<VideoView>(R.id.timelapseVideo)
                videoTitle.text = videoName
                mediaController = MediaController(activity)
                mediaController!!.setAnchorView(timelapseVideo)
                timelapseVideo.setMediaController(mediaController)
                timelapseVideo.setVideoURI(uri)
                timelapseVideo.requestFocus()
                timelapseVideo.setOnPreparedListener {
                    it.start()
                }
            }
        }
        return view
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String) =
            VideoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, name)
                }
            }
    }
}