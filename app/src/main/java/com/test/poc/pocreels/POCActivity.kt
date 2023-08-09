package com.test.poc.pocreels

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import com.test.poc.pocreels.databinding.ActivityMainBinding
import com.test.poc.pocreels.databinding.ActivityPocBinding
import com.test.poc.pocreels.utils.applyColorFilterAndSaveVideo
import com.test.poc.pocreels.utils.ll
import com.test.poc.pocreels.utils.saveVideoToStorage

class POCActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPocBinding
    private var currentUri: Uri? = null

    // declaring a null variable for MediaController
    private var mediaControls: MediaController? = null

    private val PICK_VIDEO_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView(R.layout.activity_poc)

        //  binding = ActivityMainBinding.inflate(layoutInflater)
        // setContentView(binding.root)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_poc)
        binding.activity = this

    }

    fun openGalleryForVideo() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_VIDEO_REQUEST)
    }

    fun openFromRes() {
        // set the absolute path of the video file which is going to be played
        currentUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.video_file)
        binding.textView.text = "video path :\n${currentUri?.path}"
    }

    fun playVideo() {
        binding.apply {
            if (mediaControls == null) {
                // creating an object of media controller class
                mediaControls = MediaController(this@POCActivity)

                // set the anchor view for the video view
                mediaControls!!.setAnchorView(binding.simpleVideoView)
            }

            // set the media controller for video view
            simpleVideoView.setMediaController(mediaControls)

            simpleVideoView.setVideoURI(currentUri)
            simpleVideoView.requestFocus()

            // starting the video
            simpleVideoView.start()

            // display a toast message
            // after the video is completed
            simpleVideoView.setOnCompletionListener {
                Toast.makeText(applicationContext, "Video completed", Toast.LENGTH_LONG).show()
                true
            }

            // display a toast message if any
            // error occurs while playing the video
            simpleVideoView.setOnErrorListener { mp, what, extra ->
                Toast.makeText(
                    applicationContext, "An Error Occurred " +
                            "While Playing Video !!!", Toast.LENGTH_LONG
                ).show()
                false
            }
        }
    }

    fun filterAndSave() {
        currentUri?.let {
            applyColorFilterAndSaveVideo(this@POCActivity, it)
        } ?: {
            Toast.makeText(applicationContext, "Please select any file", Toast.LENGTH_SHORT)
        }
    }

    fun save() {
        currentUri?.let {
            saveVideoToStorage(this@POCActivity, it)
        } ?: {
            Toast.makeText(applicationContext, "Please select any file", Toast.LENGTH_SHORT)
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == Activity.RESULT_OK) {
            ll("data  : $data")
            data?.let { ll("data?.data : ${it.data}") }

            val selectedVideoUri: Uri = data?.data ?: return

            currentUri = selectedVideoUri
            binding.textView.text = "video path :\n${currentUri?.path}"
            //  applyColorFilterAndSaveVideo(selectedVideoUri)
            // playVideo()
        }
    }

    // new codes here
}