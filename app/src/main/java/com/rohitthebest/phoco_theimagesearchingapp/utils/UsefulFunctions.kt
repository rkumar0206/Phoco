package com.rohitthebest.phoco_theimagesearchingapp.utils

import android.annotation.SuppressLint
import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.rohitthebest.phoco_theimagesearchingapp.Constants.NO_INTERNET_MESSAGE
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val TAG = "UsefulFunctions"

enum class ToastyType {

    SUCCESS,
    ERROR,
    INFO,
    WARNING,
    NORMAL
}

fun showToast(context: Context, message: String, duration: Int = Toast.LENGTH_SHORT) {

    try {
        Log.d(TAG, message)
        Log.d(TAG, "showToast: $message")
        Toast.makeText(context, message, duration).show()
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

fun showToasty(
        context: Context,
        message: String,
        type: ToastyType = ToastyType.SUCCESS,
        withIcon: Boolean = true,
        duration: Int = Toast.LENGTH_SHORT
) {

    when (type) {

        ToastyType.SUCCESS -> {
            Toasty.success(context, message, duration, withIcon).show()
        }
        ToastyType.ERROR -> {
            Toasty.error(context, message, duration, withIcon).show()
        }
        ToastyType.WARNING -> {
            Toasty.warning(context, message, duration, withIcon).show()
        }
        ToastyType.INFO -> {
            Toasty.info(context, message, duration, withIcon).show()
        }
        ToastyType.NORMAL -> {
            Toasty.normal(context, message, duration).show()
        }

    }
}

fun View.hide() {

    try {

        this.visibility = View.GONE

    }catch (e : IllegalStateException) {
        e.printStackTrace()
    }
}
fun View.show() {

    try {

        this.visibility = View.VISIBLE

    }catch (e : IllegalStateException) {
        e.printStackTrace()
    }
}

fun View.invisible() {

    try {

        this.visibility = View.INVISIBLE

    } catch (e: IllegalStateException) {
        e.printStackTrace()
    }
}

fun Context.isInternetAvailable(): Boolean {

    return CheckNetworkConnection().isInternetAvailable(this)
}


fun Context.showNoInternetMessage() {

    showToasty(this, NO_INTERNET_MESSAGE, ToastyType.ERROR)
}

fun Context.clearAppCache() {

    this.cacheDir.deleteRecursively()
    Log.i(TAG, "clearAppCache: Cleared Successfully")
}

@SuppressLint("SimpleDateFormat")
fun getCurrentDate(): String {

    val sdf = SimpleDateFormat("dd/MM/yyyy")
    return sdf.format(Date())
}

suspend fun setImageAsHomeScreenWallpaperFromImageUrl(context: Context, imageUrl: String) {

    val wallpaperManager = WallpaperManager.getInstance(context)

    withContext(Dispatchers.IO) {

        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    try {

                        wallpaperManager.setBitmap(resource)
                        showToast(context, "Image set as Home screen wallpaper")
                    } catch (e: IOException) {

                        e.printStackTrace()
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    //TODO("Not yet implemented")
                }

            })
    }

}

