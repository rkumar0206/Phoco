package com.rohitthebest.phoco_theimagesearchingapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.WallpaperManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.rohitthebest.phoco_theimagesearchingapp.Constants.NO_INTERNET_MESSAGE
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.pexelsData.PexelPhoto
import com.rohitthebest.phoco_theimagesearchingapp.data.pixabayData.PixabayPhoto
import com.rohitthebest.phoco_theimagesearchingapp.data.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.UserInfo
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.random.Random

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

    Log.d(TAG, "showToasty: $message")

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

fun showSnackBar(
        view: View,
        message: String = "",
        duration: Int = Snackbar.LENGTH_SHORT,
        textColor: String = "#779dfe"
) {

    Log.d(TAG, "showSnackBar: $message")

    Snackbar.make(view, message, duration)
            .setTextColor(Color.parseColor(textColor))
            .show()
}

fun View.hide() {

    try {

        this.visibility = View.GONE

    } catch (e: IllegalStateException) {
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

fun Int.getHexColorString() = String.format("#%06X", 0xFFFFFFFF and this.toLong())

fun String.isValidString(): Boolean {

    return this.trim().isNotEmpty() && this.trim() != "" && this.trim() != "null"
}

fun hideKeyBoard(activity: Activity) {

    Log.d(TAG, "hideKeyBoard: ")

    try {

        GlobalScope.launch {

            closeKeyboard(activity)
        }

    } catch (e: Exception) {

        e.printStackTrace()
    }
}

suspend fun closeKeyboard(activity: Activity) {

    Log.d(TAG, "closeKeyboard: ")

    try {
        withContext(Dispatchers.IO) {

            val view = activity.currentFocus

            if (view != null) {

                val inputMethodManager =
                        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
            }

        }
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}


fun View.showKeyboard(activity: Activity) {
    try {

        Log.d(TAG, "showKeyboard: ")

        this.requestFocus()

        val inputMethodManager =
                activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
    } catch (e: java.lang.Exception) {
        e.printStackTrace()
    }
}

/**
 * this function can convert an integer into  a string of a given base/radix
 * this function does not assure that you will find its logic in mathematics
 * it considers that the ordered digits are 0-9 then a-z then A-Z then !@#$%^&
 * @author Mohit kumar
 * @param radix
 */
fun Long.toStringM(radix: Int = 0): String {

    val values = arrayOf(
            "0",
            "1",
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "a",
            "b",
            "c",
            "d",
            "e",
            "f",
            "g",
            "h",
            "i",
            "j",
            "k",
            "l",
            "m",
            "n",
            "o",
            "p",
            "q",
            "r",
            "s",
            "t",
            "u",
            "v",
            "w",
            "x",
            "y",
            "z",
            "A",
            "B",
            "C",
            "D",
            "E",
            "F",
            "G",
            "H",
            "I",
            "J",
            "K",
            "L",
            "M",
            "N",
            "O",
            "P",
            "Q",
            "R",
            "S",
            "T",
            "U",
            "V",
            "W",
            "X",
            "Y",
            "Z",
            "!",
            "@",
            "#",
            "$",
            "%",
            "^",
            "&"
    )
    var str = ""
    var d = this
    var r: Int

    if (radix in 1..69) {

        if (d <= 0) {
            return d.toString()
        }

        while (d != 0L) {

            r = (d % radix).toInt()
            d /= radix
            str = values[r] + str
        }

        return str
    }

    return d.toString()
}


fun generateKey(appendString: String = "", radix: Int = 69): String {

    return "${System.currentTimeMillis().toStringM(radix)}_${
        Random.nextLong(
                100,
                9223372036854775
        ).toStringM(radix)
    }$appendString"
}


fun generateSavedImage(imageToBeSaved: Any, apiName: APIName): SavedImage {

    val savedImage = SavedImage().apply {

        key = generateKey()
        collectionKey = ""
    }

    return when (apiName) {

        APIName.UNSPLASH -> {

            val unsplashPhoto = imageToBeSaved as UnsplashPhoto

            savedImage.apply {

                apiInfo = APIsInfo(apiName, R.drawable.logo_unsplash)
                imageId = unsplashPhoto.id
                imageName = unsplashPhoto.alt_description ?: generateKey()
                imageUrls = ImageDownloadLinksAndInfo.ImageUrls(unsplashPhoto.urls.small, unsplashPhoto.urls.regular, unsplashPhoto.links.download)
                userInfo = UserInfo(
                        unsplashPhoto.user.name,
                        unsplashPhoto.user.id,
                        unsplashPhoto.user.profile_image.medium
                )
                uid = ""
            }
        }

        APIName.PIXABAY -> {

            val pixabayPhoto = imageToBeSaved as PixabayPhoto

            savedImage.apply {

                apiInfo = APIsInfo(apiName, R.drawable.logo_pixabay_square)
                imageId = pixabayPhoto.id.toString()
                imageName = generateKey("_pixabay")   //name of the image
                imageUrls = ImageDownloadLinksAndInfo
                    .ImageUrls(
                        pixabayPhoto.previewURL,
                        pixabayPhoto.previewURL,
                        pixabayPhoto.largeImageURL
                    )
                userInfo = UserInfo(
                    pixabayPhoto.user,
                    pixabayPhoto.user_id.toString(),
                    pixabayPhoto.userImageURL
                )
                uid = ""
            }
        }

        APIName.PEXELS -> {

            val pexelPhoto = imageToBeSaved as PexelPhoto

            savedImage.apply {

                apiInfo = APIsInfo(apiName, R.drawable.logo_pexels)
                imageId = pexelPhoto.id.toString()
                imageName = generateKey("_pexel")   //name of the image
                imageUrls = ImageDownloadLinksAndInfo
                    .ImageUrls(pexelPhoto.src.medium, pexelPhoto.src.large, pexelPhoto.src.original)
                userInfo = UserInfo(
                    pexelPhoto.photographer,
                    pexelPhoto.photographer_id.toString(),
                    pexelPhoto.photographer_url
                )
                uid = ""
            }
        }

        else -> SavedImage()
    }

}

fun showDownloadOptionPopupMenu(activity: Activity, view: View, imageDownloadLinksAndInfo: ImageDownloadLinksAndInfo) {

    val popup = PopupMenu(activity, view)

    popup.menuInflater.inflate(R.menu.downlaod_options_menu, popup.menu)

    popup.show()

    popup.setOnMenuItemClickListener {

        return@setOnMenuItemClickListener when (it.itemId) {

            R.id.menu_small_download_option -> {

                downloadFile(
                        activity,
                        imageDownloadLinksAndInfo.imageUrls.small,
                        imageDownloadLinksAndInfo.imageName + ".jpg"
                )

                true
            }

            R.id.menu_medium_download_option -> {

                downloadFile(
                        activity,
                        imageDownloadLinksAndInfo.imageUrls.medium,
                        imageDownloadLinksAndInfo.imageName + ".jpg"
                )

                true
            }

            R.id.menu_original_download_option -> {

                downloadFile(
                        activity,
                        imageDownloadLinksAndInfo.imageUrls.original,
                        imageDownloadLinksAndInfo.imageName + ".jpg"
                )

                true
            }


            else -> false
        }
    }

}


fun shareAsText(context: Context, message: String, subject: String? = "") {

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, message)

    context.startActivity(Intent.createChooser(intent, "Share via"))
}




