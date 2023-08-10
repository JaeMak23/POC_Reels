package com.test.poc.pocreels.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import java.io.File
import java.io.FileOutputStream

/*fun applyColorFilterAndSaveVideo(context: Context, videoUri: Uri) {
    // Same as before...

    val inputVideoPath = videoUri.getRealPathFromURI(context)
    val outputVideoPath = "${context.externalCacheDir?.absolutePath}/output_video.mp4"

    *//*  val rc = FFmpeg.execute("-i $inputVideoPath -c:v libxvid $outputVideoPath")
      Log.i(
          "jjj",
          String.format(
              "${Config.TAG} :Command execution %s.",
              (if (rc == 0) "completed successfully" else "failed with rc=$rc")
          )
      )*//*

    *//*  val ffmpegCommand = arrayOf(
          "-i", inputVideoPath,
          outputVideoPath
      )*//*
    val ffmpegCommand = arrayOf(
        "-i", inputVideoPath,
        "-vf", "colorchannelmixer=.393:.769:.189:0:.349:.686:.168:0:.272:.534:.131",
        "-c:v", "libx264",
        "-c:a", "copy",
        outputVideoPath
    )


    Log.i("jjj", "URI path : $videoUri")
    Log.i("jjj", "input path : $inputVideoPath")
    Log.i("jjj", "output path : $outputVideoPath")

    val result = FFmpeg.execute(ffmpegCommand)

    //  val result = FFmpeg.execute(arrayOf("-i", inputVideoPath, outputVideoPath))

    if (result == Config.RETURN_CODE_SUCCESS) {
        //       if (rc == Config.RETURN_CODE_SUCCESS) {
        Log.i("jjj", "Success")
        // Video processing success
    } else {

        Log.i("jjj", "Failed.")
        // Video processing failed
    }
}*/

// Same as before...

// }


fun Uri.getRealPathFromURI(context: Context): String {
    val projection = arrayOf(MediaStore.Video.Media.DATA)
    val cursor = context.contentResolver.query(this, projection, null, null, null)
    cursor?.moveToFirst()
    val columnIndex = cursor?.getColumnIndex(MediaStore.Video.Media.DATA)
    val videoPath = cursor?.getString(columnIndex ?: 0) ?: ""
    cursor?.close()
    return videoPath
}

fun saveVideoToStorage(context: Context, uri: Uri): File {
    // Get the file name from the URI.
    val fileName = uri.lastPathSegment ?: "video.mp4"

    ll("file name : $fileName")


    // Create a file in the external storage directory with the specified file name.
    val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MOVIES), fileName)

    ll("file path : ${file.absolutePath}")

    // Create an input stream from the URI.
    val inputStream = context.contentResolver.openInputStream(uri)

    // Create an output stream to the file.
    val outputStream = FileOutputStream(file)

    // Copy the data from the input stream to the output stream.
    val bytes = ByteArray(1024)
    var read: Int
    while (inputStream?.read(bytes).also { read = it ?: -1 } != -1) {
        outputStream.write(bytes, 0, read)
    }

    // Close the streams.
    inputStream?.close()
    outputStream.close()

    // Return the file.
    return file
}

fun ll(msg: Any?) = Log.i("jjj", "$msg")