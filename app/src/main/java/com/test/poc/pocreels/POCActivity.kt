package com.test.poc.pocreels

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.MediaController
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import com.github.hiteshsondhi88.libffmpeg.FFmpeg
import com.github.hiteshsondhi88.libffmpeg.FFmpegExecuteResponseHandler
import com.test.poc.pocreels.databinding.ActivityPocBinding
import com.test.poc.pocreels.utils.getRealPathFromURI
import com.test.poc.pocreels.utils.ll
import com.test.poc.pocreels.utils.saveVideoToStorage
import java.io.File

class POCActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPocBinding
    private var currentUri: Uri? = null
    private var savedFile: File? = null

    // declaring a null variable for MediaController
    private var mediaControls: MediaController? = null

    private val PICK_VIDEO_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

      //  FFmpeg.getInstance(this) // Initialize FFmpeg
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
          //  applyColorFilterAndSaveVideo(this@POCActivity, it)
            colorFilter()
        } ?: {
            Toast.makeText(applicationContext, "Please select any file", Toast.LENGTH_SHORT)
        }
    }

    fun save() {
        currentUri?.let {
            val file = saveVideoToStorage(this@POCActivity, it)
            ll("File after saving :\n -- <$file>\n -- <${file.absolutePath}>")

            // val outputVideoPath = "${externalCacheDir?.absolutePath}/output_video.mp4"
           // val outputVideoPath = "${getExternalFilesDir(Environment.DIRECTORY_MOVIES)}/result"
            val outputVideoPath = (getExternalFilesDir(Environment.DIRECTORY_MOVIES)?.absolutePath
                ?:"files00" ) + "/result.mp4"

            ll("output path : $outputVideoPath")

            val startTime = "00:00:10"  // Replace with your start time
            val endTime = "00:00:20"    // Replace with your end time
           // val inputUri = "android.resource://com.example.app/raw/input_video"

             trimVideo(it.getRealPathFromURI(this),outputVideoPath,startTime,endTime)


            /*val ffmpegCommand = arrayOf(
                "-i", file.absolutePath,
                "-vf", "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131",
                "-c:v", "libx264",
                "-c:a", "copy",
                outputVideoPath
            )

            val result = FFmpeg.execute(ffmpegCommand)

            if (result == Config.RETURN_CODE_SUCCESS) {
                Log.i("jjj", "Success")
                // Video processing success
            } else {

                Log.i("jjj", "Failed. result = $result")
                // Video processing failed
            }
*/

            //   applyColorFilterAndSaveVideo(this@POCActivity,file.toUri())

        } ?: {
            Toast.makeText(applicationContext, "Please select any file", Toast.LENGTH_SHORT)
        }


    }

    // Function to trim the video
    private fun trimVideo(inputUri: String, outputFilePath: String, startTime: String, endTime: String) {
        val command = arrayOf(
            "-i", inputUri,
            "-ss", startTime,
            "-to", endTime,
            "-c", "copy",
            outputFilePath
        )

        FFmpeg.getInstance(this).execute(command,object: FFmpegExecuteResponseHandler{
            override fun onStart() {
              ll("onStart called")
            }

            override fun onFinish() {
                ll("OnFinish called")
            }

            override fun onSuccess(message: String?) {
                ll("OnSuccess called, message : $message")
            }

            override fun onProgress(message: String?) {
                ll("OnProgress called, message : $message")
            }

            override fun onFailure(message: String?) {
                ll("OnFailure called, message : $message")
            }

        })
    }

    private fun colorFilterVideo(inputUri: String, outputFilePath: String) {
        val command = arrayOf(
            "-i", inputUri,
            "-vf", "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131",
            "-c:v", "libx264",
            "-c:a", "copy",
            outputFilePath
        )

        FFmpeg.getInstance(this).execute(command,object: FFmpegExecuteResponseHandler{
            override fun onStart() {
                ll("onStart called")
            }

            override fun onFinish() {
                ll("OnFinish called")
            }

            override fun onSuccess(message: String?) {
                ll("OnSuccess called, message : $message")
            }

            override fun onProgress(message: String?) {
                ll("OnProgress called, message : $message")
            }

            override fun onFailure(message: String?) {
                ll("OnFailure called, message : $message")
            }

        })
    }

    // Example usage
/*    private fun exampleUsage() {
        val inputUri = "input_video_uri"
        val outputFilePath = "output_video_path"
        val startTime = "00:00:10"  // Replace with your start time
        val endTime = "00:00:20"    // Replace with your end time

        trimVideo(inputUri, outputFilePath, startTime, endTime)
    }*/

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

    fun colorFilter() {
        // Create a PorterDuffColorFilter that will tint the video with a sepia tone.
        // val sepiaFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)

        // Set the color filter on the VideoView.
        //   binding.simpleVideoView.color = sepiaFilter

        val mediaPlayer = MediaPlayer.create(this, R.raw.video_file)

        // Create a PorterDuffColorFilter that will tint the video with a sepia tone.
        //val sepiaFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY)
        val sepiaFilter = PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.SRC_ATOP)

        // Set the color filter on the MediaPlayer.
        //  mediaPlayer.colorFilter(sepiaFilter)


        // Play the video.
        mediaPlayer.start()

    }

    /* fun grayScaleFilter() {
         // Create a ColorMatrixColorFilter that will convert the video to grayscale.
         val grayscaleFilter = ColorMatrixColorFilter(
             floatArrayOf(
                 0.2989, 0.5870, 0.1140, 0.0,
                 0.2989, 0.5870, 0.1140, 0.0,
                 0.2989, 0.5870, 0.1140, 0.0,
                 0.0, 0.0, 0.0, 1.0
             )
         )

 // Set the color filter on the VideoView.
         binding.simpleVideoView.colorFilter = grayscaleFilter
     }*/
}