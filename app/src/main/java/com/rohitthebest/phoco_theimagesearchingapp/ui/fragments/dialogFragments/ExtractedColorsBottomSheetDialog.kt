package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments.dialogFragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.rohitthebest.phoco_theimagesearchingapp.Constants.EXTRACTED_COLORS_IMAGE_URL_KEY
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.ExtractedColorsBottomSheetLayoutBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "ExtractedColorsBottomSh"

data class SwatchColors(
        var vibrantSwatch: Palette.Swatch? = null,
        var darkVibrantSwatch: Palette.Swatch? = null,
        var lightVibrantSwatch: Palette.Swatch? = null,
        var mutedSwatch: Palette.Swatch? = null,
        var darkMutedSwatch: Palette.Swatch? = null,
        var lightMutedSwatch: Palette.Swatch? = null,
)

class ExtractedColorsBottomSheetDialog : BottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: ExtractedColorsBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!

    private var imageUrl = ""

    private var swatchColors: SwatchColors? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.extracted_colors_bottom_sheet_layout, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = ExtractedColorsBottomSheetLayoutBinding.bind(view)

        binding.progressBarCV.show()

        getPassedArguments()

        initListeners()
    }

    private fun getPassedArguments() {

        if (!arguments?.isEmpty!!) {

            imageUrl = arguments?.getString(EXTRACTED_COLORS_IMAGE_URL_KEY).toString()

            if (imageUrl.isValidString()) {

                Log.d(TAG, "getPassedArguments: $imageUrl")

                CoroutineScope(Dispatchers.Main).launch {

                    extractColorsFromTheImage(requireContext(), imageUrl)
                }
            } else {

                Log.d(TAG, "getPassedArguments: Something went wrong in arguments")
                showErrorMessageAndDismiss()
            }
        }
    }


    private fun initListeners() {

        binding.vibrantSwatchView.setOnClickListener(this)
        binding.darkVibrantSwatchView.setOnClickListener(this)
        binding.lightVibrantSwatchView.setOnClickListener(this)
        binding.mutedSwatchView.setOnClickListener(this)
        binding.darkMutedSwatchView.setOnClickListener(this)
        binding.lightMutedSwatchView.setOnClickListener(this)

        binding.vibrantSwatchTV.setOnClickListener(this)
        binding.darkVirantSwatchTV.setOnClickListener(this)
        binding.lightVibrantSwatchTV.setOnClickListener(this)
        binding.mutedSwatchTV.setOnClickListener(this)
        binding.darkMutedSwatchTV.setOnClickListener(this)
        binding.lightMutedSwatchTV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.vibrantSwatchView.id -> {

                swatchColors?.let {

                    showDialog(it.vibrantSwatch!!)
                }

            }
            binding.darkVibrantSwatchView.id -> {

                swatchColors?.let {

                    showDialog(it.darkVibrantSwatch!!)
                }

            }
            binding.lightVibrantSwatchView.id -> {

                swatchColors?.let {

                    showDialog(it.lightVibrantSwatch!!)
                }

            }
            binding.mutedSwatchView.id -> {

                swatchColors?.let {

                    showDialog(it.mutedSwatch!!)
                }

            }
            binding.darkMutedSwatchView.id -> {

                swatchColors?.let {

                    showDialog(it.darkMutedSwatch!!)
                }

            }
            binding.lightMutedSwatchView.id -> {

                swatchColors?.let {

                    showDialog(it.lightMutedSwatch!!)
                }

            }

            binding.vibrantSwatchTV.id -> {

                swatchColors?.vibrantSwatch?.rgb?.getHexColorString()?.let { copyToClipBoard(requireActivity(), it) }
                showToast(requireContext(), "Background color copied")
            }
            binding.darkVirantSwatchTV.id -> {
                swatchColors?.darkVibrantSwatch?.rgb?.getHexColorString()?.let { copyToClipBoard(requireActivity(), it) }
                showToast(requireContext(), "Background color copied")

            }
            binding.lightVibrantSwatchTV.id -> {
                swatchColors?.lightVibrantSwatch?.rgb?.getHexColorString()?.let { copyToClipBoard(requireActivity(), it) }
                showToast(requireContext(), "Background color copied")

            }
            binding.mutedSwatchTV.id -> {
                swatchColors?.mutedSwatch?.rgb?.getHexColorString()?.let { copyToClipBoard(requireActivity(), it) }
                showToast(requireContext(), "Background color copied")

            }
            binding.darkMutedSwatchTV.id -> {
                swatchColors?.darkMutedSwatch?.rgb?.getHexColorString()?.let { copyToClipBoard(requireActivity(), it) }
                showToast(requireContext(), "Background color copied")

            }
            binding.lightMutedSwatchTV.id -> {
                swatchColors?.lightMutedSwatch?.rgb?.getHexColorString()?.let { copyToClipBoard(requireActivity(), it) }
                showToast(requireContext(), "Background color copied")

            }
        }
    }

    private fun showDialog(swatch: Palette.Swatch) {

        MaterialAlertDialogBuilder(requireContext())
                .setTitle("Color info")
                .setMessage("Background color : ${swatch.rgb.getHexColorString()}\n\n" +
                        "Title text color : ${swatch.titleTextColor.getHexColorString()}\n\n" +
                        "Body text color : ${swatch.bodyTextColor.getHexColorString()}")
                .setPositiveButton("Ok") { dialog, _ ->

                    dialog.dismiss()
                }
                .create()
                .show()
    }


    private suspend fun extractColorsFromTheImage(context: Context, imageUrl: String) {

        withContext(Dispatchers.IO) {

            Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .addListener(object : RequestListener<Bitmap> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Bitmap>?, isFirstResource: Boolean): Boolean {

                            binding.progressBarCV.hide()
                            showErrorMessageAndDismiss()
                            return false
                        }

                        override fun onResourceReady(resource: Bitmap?, model: Any?, target: Target<Bitmap>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            //TODO("Not yet implemented")
                            return false
                        }
                    })
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {


                            val paletteBuilder = Palette.Builder(resource)

                            paletteBuilder.maximumColorCount(100).generate { palette ->

                                Log.d(TAG, "onResourceReady: vibrantSwatch : ${palette?.vibrantSwatch}")
                                Log.d(TAG, "onResourceReady: darkVibrantSwatch : ${palette?.darkVibrantSwatch}")
                                Log.d(TAG, "onResourceReady: lightVibrantSwatch : ${palette?.lightVibrantSwatch}")
                                Log.d(TAG, "onResourceReady: mutedSwatch : ${palette?.mutedSwatch}")
                                Log.d(TAG, "onResourceReady: darkMutedSwatch : ${palette?.darkMutedSwatch}")
                                Log.d(TAG, "onResourceReady: lightMutedSwatch : ${palette?.lightMutedSwatch}")

                                swatchColors = SwatchColors(
                                        palette?.vibrantSwatch,
                                        palette?.darkVibrantSwatch,
                                        palette?.lightVibrantSwatch,
                                        palette?.mutedSwatch,
                                        palette?.darkMutedSwatch,
                                        palette?.lightMutedSwatch,
                                )

                                swatchColors?.let {

                                    updateViews(it)
                                }
                            }
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {
                            //TODO("Not yet implemented")
                        }
                    })

        }
    }

    private fun showErrorMessageAndDismiss() {

        showToasty(requireContext(), "Something went wrong!!!", ToastyType.ERROR)
        dismiss()
    }

    private fun updateViews(swatchColors: SwatchColors) {

        // Vibrant swatch
        if (swatchColors.vibrantSwatch != null) {

            binding.vibrantSwatchView.show()
            binding.vibrantSwatchTV.show()

            binding.vibrantSwatchView.setBackgroundColor(swatchColors.vibrantSwatch!!.rgb)
            binding.vibrantSwatchTV.setTextColor(swatchColors.vibrantSwatch!!.bodyTextColor)
            binding.vibrantSwatchTV.text = swatchColors.vibrantSwatch!!.rgb.getHexColorString()
        } else {

            binding.vibrantSwatchView.hide()
            binding.vibrantSwatchTV.hide()
        }

        //dark vibrant swatch
        if (swatchColors.darkVibrantSwatch != null) {

            binding.darkVibrantSwatchView.show()
            binding.darkVirantSwatchTV.show()

            binding.darkVibrantSwatchView.setBackgroundColor(swatchColors.darkVibrantSwatch!!.rgb)
            binding.darkVirantSwatchTV.setTextColor(swatchColors.darkVibrantSwatch!!.bodyTextColor)
            binding.darkVirantSwatchTV.text = swatchColors.darkVibrantSwatch!!.rgb.getHexColorString()
        } else {

            binding.darkVibrantSwatchView.hide()
            binding.darkVirantSwatchTV.hide()
        }

        //light vibrant swatch
        if (swatchColors.lightVibrantSwatch != null) {

            binding.lightVibrantSwatchView.show()
            binding.lightVibrantSwatchTV.show()

            binding.lightVibrantSwatchView.setBackgroundColor(swatchColors.lightVibrantSwatch!!.rgb)
            binding.lightVibrantSwatchTV.setTextColor(swatchColors.lightVibrantSwatch!!.bodyTextColor)
            binding.lightVibrantSwatchTV.text = swatchColors.lightVibrantSwatch!!.rgb.getHexColorString()
        } else {

            binding.lightVibrantSwatchView.hide()
            binding.lightVibrantSwatchTV.hide()
        }

        //muted swatch
        if (swatchColors.mutedSwatch != null) {

            binding.mutedSwatchView.show()
            binding.mutedSwatchTV.show()

            binding.mutedSwatchView.setBackgroundColor(swatchColors.mutedSwatch!!.rgb)
            binding.mutedSwatchTV.setTextColor(swatchColors.mutedSwatch!!.bodyTextColor)
            binding.mutedSwatchTV.text = swatchColors.mutedSwatch!!.rgb.getHexColorString()
        } else {

            binding.mutedSwatchView.hide()
            binding.mutedSwatchTV.hide()
        }

        // dark muted swatch
        if (swatchColors.darkMutedSwatch != null) {

            binding.darkMutedSwatchView.show()
            binding.darkMutedSwatchTV.show()

            binding.darkMutedSwatchView.setBackgroundColor(swatchColors.darkMutedSwatch!!.rgb)
            binding.darkMutedSwatchTV.setTextColor(swatchColors.darkMutedSwatch!!.bodyTextColor)
            binding.darkMutedSwatchTV.text = swatchColors.darkMutedSwatch!!.rgb.getHexColorString()
        } else {

            binding.darkMutedSwatchView.hide()
            binding.darkMutedSwatchTV.hide()
        }

        //light muted swatch
        if (swatchColors.lightMutedSwatch != null) {

            binding.lightMutedSwatchView.show()
            binding.lightMutedSwatchTV.show()

            binding.lightMutedSwatchView.setBackgroundColor(swatchColors.lightMutedSwatch!!.rgb)
            binding.lightMutedSwatchTV.setTextColor(swatchColors.lightMutedSwatch!!.bodyTextColor)
            binding.lightMutedSwatchTV.text = swatchColors.lightMutedSwatch!!.rgb.getHexColorString()
        } else {

            binding.lightMutedSwatchView.hide()
            binding.lightMutedSwatchTV.hide()
        }

        binding.progressBarCV.hide()
    }

    companion object {

        @JvmStatic
        fun newInstance(bundle: Bundle): ExtractedColorsBottomSheetDialog {

            val fragment = ExtractedColorsBottomSheetDialog()
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}