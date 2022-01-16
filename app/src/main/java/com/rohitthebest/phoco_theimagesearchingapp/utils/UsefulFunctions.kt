package com.rohitthebest.phoco_theimagesearchingapp.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.itextpdf.text.*
import com.itextpdf.text.pdf.PdfWriter
import com.rohitthebest.phoco_theimagesearchingapp.Constants.NO_INTERNET_MESSAGE
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.SavedImage
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.UserInfo
import com.rohitthebest.phoco_theimagesearchingapp.remote.pexelsData.PexelPhoto
import com.rohitthebest.phoco_theimagesearchingapp.remote.pixabayData.PixabayPhoto
import com.rohitthebest.phoco_theimagesearchingapp.remote.unsplashData.UnsplashPhoto
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.APIName
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.APIsInfo
import com.rohitthebest.phoco_theimagesearchingapp.utils.dataHelperClass.ImageDownloadLinksAndInfo
import com.rohitthebest.phoco_theimagesearchingapp.utils.glideSVG.SvgSoftwareLayerSetter
import es.dmoral.toasty.Toasty
import kotlinx.coroutines.*
import java.io.*
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

fun View.showSnackBar(
    msg: String,
    length: Int = Snackbar.LENGTH_LONG,
    actionMessage: CharSequence?,
    action: (View) -> Unit
) {
    val snackBar = Snackbar.make(this, msg, length)

    if (actionMessage != null) {

        val snackbarActionTextView =
            snackBar.view.findViewById<TextView>(com.google.android.material.R.id.snackbar_action)

        snackbarActionTextView.isAllCaps = false

        snackBar.setAction(actionMessage) {
            action(it)
        }.show()
    } else {
        snackBar.show()
    }
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

suspend fun getImageBitmapUsingGlide(
    context: Context,
    imageUrl: String,
    onResourceReady: (Bitmap) -> Unit
) {

    //val wallpaperManager = WallpaperManager.getInstance(context)

    withContext(Dispatchers.IO) {

        Glide.with(context)
            .asBitmap()
            .load(imageUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {

                    try {

                        onResourceReady(resource)
//                        wallpaperManager.setBitmap(resource)
//                        showToast(context, "Image set as Home screen wallpaper")
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

fun Bitmap.getImageUri(context: Context): Uri? {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        val resolver = context.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "image.jpeg")
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_PICTURES}/Phoco"
            )
        }

        val uri = resolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let {
            resolver.openOutputStream(it).use { fout ->

                try {

                    this.compress(Bitmap.CompressFormat.JPEG, 100, fout)
                    fout?.close()

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }
        }

        return uri

    } else {

        val bytes = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 100, bytes)

        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            this,
            "imageTemp",
            null
        )

        return Uri.parse(path)
    }
}


fun Int.getHexColorString() = String.format("#%06X", 0xFFFFFFFF and this.toLong())

fun String.isValidString(): Boolean {

    return this.trim().isNotEmpty() && this.trim() != "" && this.trim() != "null"
}

fun hideKeyBoard(activity: Activity) {

    Log.d(TAG, "hideKeyBoard: ")

    try {

        CoroutineScope(Dispatchers.IO).launch {

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


fun generateSavedImage(imageToBeSaved: Any, apiName: APIName): SavedImage {

    val savedImage = SavedImage().apply {

        key = UUID.randomUUID().toString()
        collectionKey = ""
    }

    return when (apiName) {

        APIName.UNSPLASH -> {

            val unsplashPhoto = imageToBeSaved as UnsplashPhoto

            savedImage.apply {

                apiInfo = APIsInfo(apiName, R.drawable.logo_unsplash)
                imageId = unsplashPhoto.id
                imageName =
                    unsplashPhoto.alt_description ?: UUID.randomUUID().toString() + "_unsplash"
                imageUrls = ImageDownloadLinksAndInfo.ImageUrls(
                    unsplashPhoto.urls.small,
                    unsplashPhoto.urls.regular,
                    unsplashPhoto.links.download
                )
                userInfo = UserInfo(
                    userName = unsplashPhoto.user.name,
                    userIdOrUserName = unsplashPhoto.user.username,
                    userImageUrl = unsplashPhoto.user.profile_image.medium
                )
                uid = ""
                width = unsplashPhoto.width
                height = unsplashPhoto.height
            }
        }

        APIName.PIXABAY -> {

            val pixabayPhoto = imageToBeSaved as PixabayPhoto

            savedImage.apply {

                apiInfo = APIsInfo(apiName, R.drawable.logo_pixabay_square)
                imageId = pixabayPhoto.id.toString()
                imageName = UUID.randomUUID().toString() + "_pixabay"   //name of the image
                imageUrls = ImageDownloadLinksAndInfo
                    .ImageUrls(
                        pixabayPhoto.previewURL,
                        pixabayPhoto.largeImageURL,
                        pixabayPhoto.largeImageURL
                    )
                userInfo = UserInfo(
                    pixabayPhoto.user,
                    pixabayPhoto.user_id.toString(),
                    pixabayPhoto.userImageURL
                )
                uid = ""
                width = pixabayPhoto.imageWidth
                height = pixabayPhoto.imageHeight
            }
        }

        APIName.PEXELS -> {

            val pexelPhoto = imageToBeSaved as PexelPhoto

            savedImage.apply {

                apiInfo = APIsInfo(apiName, R.drawable.logo_pexels)
                imageId = pexelPhoto.id.toString()
                imageName = UUID.randomUUID().toString() + "_pexel"   //name of the image
                imageUrls = ImageDownloadLinksAndInfo
                    .ImageUrls(pexelPhoto.src.medium, pexelPhoto.src.large, pexelPhoto.src.original)
                userInfo = UserInfo(
                    userName = pexelPhoto.photographer,
                    userIdOrUserName = pexelPhoto.photographer_id.toString(),
                    userImageUrl = pexelPhoto.photographer_url
                )
                uid = ""
                width = pexelPhoto.width
                height = pexelPhoto.height
            }
        }

        else -> SavedImage()
    }

}

fun showDownloadOptionPopupMenu(
    activity: Activity,
    view: View,
    imageDownloadLinksAndInfo: ImageDownloadLinksAndInfo
) {

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


fun shareAsText(activity: Activity, message: String, subject: String? = "") {

    val intent = Intent(Intent.ACTION_SEND)
    intent.type = "text/plain"
    intent.putExtra(Intent.EXTRA_SUBJECT, subject)
    intent.putExtra(Intent.EXTRA_TEXT, message)

    activity.startActivity(Intent.createChooser(intent, "Share via"))
}

fun copyToClipBoard(activity: Activity, text: String) {

    val clipboardManager =
            activity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager

    val clipData = ClipData.newPlainText("text", text)

    clipboardManager.setPrimaryClip(clipData)
}

fun openLinkInBrowser(context: Context, url: String?) {

    if (context.isInternetAvailable()) {
        url?.let {

            try {
                Log.d(TAG, "Loading Url in default browser.")
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(it))
                context.startActivity(intent)
            } catch (e: Exception) {
                showToast(context, e.message.toString())
                e.printStackTrace()
            }
        }
    } else {
        context.showNoInternetMessage()
    }
}


fun showShareOptionPopoupMenu(
    activity: Activity,
    view: View,
    imageUrls: ImageDownloadLinksAndInfo.ImageUrls
) {

    val popupMenu = PopupMenu(activity, view)

    popupMenu.menuInflater.inflate(R.menu.image_share_menu, popupMenu.menu)

    popupMenu.show()

    popupMenu.setOnMenuItemClickListener {

        return@setOnMenuItemClickListener when (it.itemId) {

            R.id.menu_share_image_link -> {

                shareAsText(
                    activity,
                    "Follow this link to download the image :\n\n${imageUrls.original}",
                    "Image download link"
                )

                true
            }

            R.id.menu_share_image -> {

                handleShareImageMenu(activity, imageUrl = imageUrls.medium)

                true
            }

//            R.id.menu_share_image_medium -> {
//
//                handleShareImageMenu(activity, imageUrls.medium)
//
//                true
//            }
//            R.id.menu_share_image_original -> {
//
//                handleShareImageMenu(activity, imageUrls.original)
//                true
//            }

            else -> false
        }
    }
}

fun handleShareImageMenu(activity: Activity, imageUrl: String) {

    showToast(activity, "Please wait...downloading file...")

    CoroutineScope(Dispatchers.Main).launch {

        getImageBitmapUsingGlide(
            activity,
            imageUrl,
        ) { bitmap ->

            CoroutineScope(Dispatchers.Main).launch {

                saveBitmapToCacheDirectoryAndShare(activity, bitmap)
            }
        }
    }
}

private suspend fun saveBitmapToCacheDirectoryAndShare(activity: Activity, bitmap: Bitmap?) {

    withContext(Dispatchers.IO) {
        try {

            val cachePath = File(activity.cacheDir, "images")
            cachePath.mkdirs()
            val fos = FileOutputStream("$cachePath/image.png") //overwrites the image everytime
            bitmap?.compress(Bitmap.CompressFormat.PNG, 100, fos)
            fos.close()

            //sharing the image
            shareImage(activity)

        } catch (e: IOException) {
            e.printStackTrace()
        }

    }

}

private fun shareImage(activity: Activity) {

    val imagePath = File(activity.cacheDir, "images")
    val newFile = File(imagePath, "image.png")

    val contentUri = FileProvider.getUriForFile(
        activity,
        "com.rohitthebest.phoco_theimagesearchingapp.provider",
        newFile
    )

    if (contentUri != null) {

        shareUri(
            activity,
            contentUri
        )
    }
}

fun shareUri(activity: Activity, contentUri: Uri) {

    val shareIntent = Intent()
    shareIntent.action = Intent.ACTION_SEND
    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
    shareIntent.setDataAndType(contentUri, activity.contentResolver.getType(contentUri))
    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri)
    activity.startActivity(Intent.createChooser(shareIntent, "Share Via"))

}


/*
fun calculateNumberOfDays(startDateTimestamp: Long, endDateTimestamp: Long): Int =
    ((endDateTimestamp - startDateTimestamp) / (1000 * 60 * 60 * 24)).toInt()

suspend fun getFileNameFromUri(context: Context, uri: Uri): String? {

    return withContext(Dispatchers.IO) {

        val cursor = context.contentResolver.query(
            uri,
            null,
            null,
            null,
            null
        )

        cursor?.use {

            val nameIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

            it.moveToFirst()
            it.getString(nameIndex)
        }

    }
}

suspend fun Uri.getFile(context: Context): File? {

    return withContext(Dispatchers.Main) {

        getFileNameFromUri(context, this@getFile)?.let { name ->

            val parcelFileDescriptor = context.contentResolver.openFileDescriptor(
                this@getFile, "r", null
            )

            val inputStream = FileInputStream(parcelFileDescriptor?.fileDescriptor)
            val file = File(context.cacheDir, name)

            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)

            file
        }
    }
}
*/


@SuppressLint("CheckResult")
inline fun setImageToImageViewUsingGlide(
    context: Context,
    imageView: ImageView,
    imageUrl: String?,
    crossinline onLoadFailed: () -> Unit,
    crossinline onResourceReady: () -> Unit
) {

    Glide.with(context)
        .load(imageUrl)
        .apply {
            this.error(R.drawable.ic_outline_error_outline_24)
        }
        .listener(object : RequestListener<Drawable> {

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {

                onLoadFailed()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {

                onResourceReady()
                return false
            }

        })
        .transition(DrawableTransitionOptions.withCrossFade())
        .into(imageView)

}

@SuppressLint("CheckResult")
suspend fun setSvgImageUrlToImageViewUsingGlide(
    context: Context,
    imageView: ImageView,
    imageUrl: String
) {

    withContext(Dispatchers.IO) {

        val requestBuilder = Glide.with(context)
            .`as`(PictureDrawable::class.java)
            .apply {
                this.error(R.drawable.ic_outline_error_outline_24)
            }
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(SvgSoftwareLayerSetter())


        val uri = Uri.parse(imageUrl)

        withContext(Dispatchers.Main) {

            requestBuilder.load(uri).into(imageView)
        }
    }
}

fun getSavedImagesLinksAsString(
    savedImagesUrls: List<String>,
    collection: Collection? = null
): String {

    val str = StringBuilder("")

    if (savedImagesUrls.isNotEmpty()) {

        if (collection == null) {

            // savedImages consist all the saved images
            str.append("All saved photos (${savedImagesUrls.size})\n")
        } else {

            str.append("${collection.collectionName} collection (${savedImagesUrls.size})\n")
        }

        savedImagesUrls.forEach { savedImage ->

            str.append("\n\n")
            //str.append("------------------------------\n")
            str.append(savedImage)
            //str.append("------------------------------")
        }
    }

    return str.toString()
}

fun getSavedImagesLinksAsItextParagraph(
    savedImagesUrls: List<String>,
    collection: Collection? = null
): Paragraph {

    val font: Font =
        FontFactory.getFont(FontFactory.COURIER, 14f, BaseColor.BLACK)

    val font2 = FontFactory.getFont(FontFactory.TIMES_ITALIC, 14f, Font.UNDERLINE, BaseColor.BLUE)

    val paragraph = Paragraph("", font)

    if (savedImagesUrls.isNotEmpty()) {

        if (collection == null) {

            paragraph.add("All saved photos (${savedImagesUrls.size})\n\n")

        } else {

            paragraph.add("${collection.collectionName} collection (${savedImagesUrls.size})\n\n")
        }

        savedImagesUrls.forEach { savedImageUrl ->

            val anchor = Anchor(savedImageUrl, font2)
            anchor.reference = savedImageUrl

            paragraph.add(anchor)
            paragraph.add("\n\n")
        }
    }

    return paragraph
}


fun shareSavedImagesLinksAsPdf(
    activity: Activity,
    paragraph: Paragraph
) {

    try {

        val cachePath = File(activity.cacheDir, "documents")
        cachePath.mkdirs()
        val fout =
            FileOutputStream("$cachePath/phoco_collection.pdf") //overwrites the image everytime

        makePdfOfString(fout, paragraph)

        val pdfPath = File(activity.cacheDir, "documents")
        val newFile = File(pdfPath, "phoco_collection.pdf")

        val contentUri = FileProvider.getUriForFile(
            activity,
            "com.rohitthebest.phoco_theimagesearchingapp.provider",
            newFile
        )

        if (contentUri != null) {

            shareUri(
                activity,
                contentUri
            )
        }

    } catch (e: IOException) {
        e.printStackTrace()
    }

}

fun exportSavedImagesLinkToFile(
    activity: Activity,
    paragraph: Paragraph,
    fileName: String = "collection"
): Uri? {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

        val resolver = activity.contentResolver

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, "$fileName.pdf")
            put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
            put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                "${Environment.DIRECTORY_DOCUMENTS}/Phoco"
            )
        }

        val uri = resolver?.insert(
            MediaStore.Files.getContentUri("external"),
            contentValues
        )

        uri?.let { pdfUri ->

            resolver.openOutputStream(pdfUri).use { fout ->

                try {

                    makePdfOfString(fout, paragraph)

                    return pdfUri

                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
            }

        }
    } else {

        try {
            val file = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS + "/Phoco/$fileName.pdf")

            makePdfOfString(FileOutputStream(file), paragraph)

            return file.toUri()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return null
}

fun makePdfOfString(fout: OutputStream?, paragraph: Paragraph) {

    try {
        val document = Document()

        PdfWriter.getInstance(document, fout)

        document.open()

        document.add(paragraph)

        document.close()

        fout?.close()
    } catch (e: Exception) {

        e.printStackTrace()
    }

}




