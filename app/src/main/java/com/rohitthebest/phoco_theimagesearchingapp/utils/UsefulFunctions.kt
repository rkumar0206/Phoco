package com.rohitthebest.phoco_theimagesearchingapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.Toast
import es.dmoral.toasty.Toasty
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


@SuppressLint("SimpleDateFormat")
fun getCurrentDate(): String {

    val sdf = SimpleDateFormat("dd/MM/yyyy")
    return sdf.format(Date())
}

