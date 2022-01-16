package com.rohitthebest.phoco_theimagesearchingapp.utils

import android.os.Handler
import android.os.Looper
import android.util.Log
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.File
import java.io.FileInputStream

private const val TAG = "CustomRequestBodyForFil"

class CustomRequestBodyForFileUpload(
    private val file: File,
    private val contentType: String,
    private val callback: UploadCallback

) : RequestBody() {

    companion object {

        private const val DEFAULT_BUFFER_SIZE = 2048
    }

    override fun contentType(): MediaType? = contentType.toMediaTypeOrNull()

    override fun contentLength(): Long = file.length()

    override fun writeTo(sink: BufferedSink) {

        val length = file.length()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        val fileInputStream = FileInputStream(file)
        var uploaded = 0L

        fileInputStream.use { inputStream ->

            var read: Int
            val handler = Handler(Looper.getMainLooper())

            Log.d(TAG, "writeTo: Total Length : $length")

            while (inputStream.read(buffer).also { read = it } != -1) {

                //Log.d(TAG, "writeTo: Uploaded : $uploaded")

                uploaded += read
                sink.write(buffer, 0, read)
                handler.post(ProgressUpdater(uploaded, length))
            }
        }

    }

    interface UploadCallback {

        fun onProgressUpdate(percentage: Int)

    }

    inner class ProgressUpdater(
        private val uploaded: Long,
        private val totalSize: Long
    ) : Runnable {
        override fun run() {

            val progress = (100 * uploaded / totalSize).toInt()
            Log.d(TAG, "run: progress : $progress")
            callback.onProgressUpdate(progress)
        }
    }
}