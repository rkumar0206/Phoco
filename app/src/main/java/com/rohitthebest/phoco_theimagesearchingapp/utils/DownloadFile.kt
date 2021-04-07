package com.rohitthebest.phoco_theimagesearchingapp.utils

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.rohitthebest.phoco_theimagesearchingapp.R


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class DownloadFile {

    /**
     * Used to download the file from url.
     *
     *
     * 1. Download the file using Download Manager.
     *
     * @param url      Url.
     * @param fileName File Name.
     */
    fun downloadFile(activity: Activity, url: String?, fileName: String?) {

        try {
            if (url != null && url.isNotEmpty()) {

                val uri: Uri = Uri.parse(url)

                val request = DownloadManager.Request(uri)
                request.setMimeType(getMimeType(uri.toString()))
                request.setTitle(fileName)
                request.setDescription(activity.getString(R.string.downloading_file))

                try {
                    request.allowScanningByMediaScanner()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "Phoco/$fileName"
                )
                val dm = activity.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                dm.enqueue(request)
            }
        } catch (e: IllegalStateException) {
            Toast.makeText(
                    activity,
                    "Please insert an SD card to download file",
                    Toast.LENGTH_SHORT
            ).show()
        }
    }

    /**
     * Used to get MimeType from url.
     *
     * @param url Url.
     * @return Mime Type for the given url.
     */
    private fun getMimeType(url: String): String? {
        var type: String? = null
        val extension = MimeTypeMap.getFileExtensionFromUrl(url)
        if (extension != null) {
            val mime = MimeTypeMap.getSingleton()
            type = mime.getMimeTypeFromExtension(extension)
        }
        return type
    }
}