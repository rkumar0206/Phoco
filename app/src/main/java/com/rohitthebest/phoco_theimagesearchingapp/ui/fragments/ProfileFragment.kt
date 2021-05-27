package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.AuthToken
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.PhocoUser
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentProfileBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.PhocoViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "ProfileFragment"

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class ProfileFragment : Fragment(R.layout.fragment_profile), View.OnClickListener {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val phocoViewModel by viewModels<PhocoViewModel>()

    private var authTokens: AuthToken? = null
    private var phocoUser: PhocoUser? = null

    private var isLoggedInBefore = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfileBinding.bind(view)

        authTokens = getSavedAuthToken(requireActivity())

        if (authTokens == null) {

            Log.d(TAG, "onViewCreated: auth token was null")
            findNavController().navigate(R.id.action_profileFragment_to_loginSignUpFragment)
            isLoggedInBefore = false
        } else {

            Log.d(TAG, "onViewCreated: auth token is not null")

            // check if last updated date for getting the tokens is not more than 15 days
            authTokens?.let {

                Log.d(
                    TAG,
                    "onViewCreated: Number of days = ${
                        calculateNumberOfDays(
                            it.dateWhenTokenReceived,
                            System.currentTimeMillis()
                        )
                    }"
                )

                if (calculateNumberOfDays(
                        it.dateWhenTokenReceived,
                        System.currentTimeMillis()
                    ) < 15
                ) {


                    //phocoViewModel.getNewTokens(it.refreshToken)
                    isLoggedInBefore = true

                    phocoUser = getUserProfileData(requireActivity())

                    phocoUser?.let { user -> updateUI(user) }

                } else {

                    Log.d(
                        TAG,
                        "onViewCreated: number of days is greater than 15 and login is required"
                    )

                    //login again to receive new refresh token and access token
                    findNavController().navigate(R.id.action_profileFragment_to_loginSignUpFragment)
                }
            }
        }

        observeTokenResponse()
        observePhocoUserResponse()
        observeUploadImageResponse()

        initListeners()
    }


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

        if (isGranted) {
            Log.i(TAG, "Permission granted: ")
        } else {
            Log.i(TAG, "Permission denied: ")
        }
    }


    private val chooseImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->

        lifecycleScope.launch {

            val file = uri.getFile(requireContext())

            Log.d(TAG, "fileName: ${file?.name}")
            Log.d(TAG, "fileLength: ${file?.length()}")

            authTokens?.let { accessToken ->

                Log.d(TAG, ": start of upload image")

                file?.let { file ->

                    // todo : check for the size of the file (must be greater then 4 MB)
                    // todo : preview the image
                    // todo : after upload button is clicked then only upload the image

/*
                    phocoViewModel.uploadImage(
                        accessToken.accessToken,
                        file.name,
                        file,
                        file.name,
                        phocoUser?.pk.toString()
                    )
*/
                }
            }

        }
    }


    private fun initListeners() {

        binding.backButton.setOnClickListener(this)
        binding.editProfileBtn.setOnClickListener(this)
        binding.followBtn.setOnClickListener(this)
        binding.uploadImageFAB.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.backButton.id -> {

                requireActivity().onBackPressed()
            }

            binding.editProfileBtn.id -> {

                //todo : open edit profile fragment
            }

            binding.uploadImageFAB.id -> {

                if (checkForPermissionsGranted()) {

                    chooseImageLauncher.launch(
                        "image/*"
                    )
                } else {

                    onRequestPermission()
                }
            }

            binding.followBtn.id -> {

                //todo : follow or unfollow the user
            }
        }

    }

    private fun checkForPermissionsGranted(): Boolean {

        return ContextCompat.checkSelfPermission(
            requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun onRequestPermission() {

        when {

            // if permission is already granted
            ContextCompat.checkSelfPermission(
                requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED -> {

                binding.root.showSnackbar(
                    "Permission is granted",
                    actionMessage = null
                ) {}
            }

            // if the app deems that they should show the request permission rationale

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) -> {

                binding.root.showSnackbar(
                    "Permission is required for selecting image from your storage.",
                    actionMessage = "Ok"
                ) {

                    requestPermissionLauncher.launch(
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                }
            }

            // if permission hasn't been asked yet.
            else -> {
                requestPermissionLauncher.launch(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            }
        }
    }

    private fun observeTokenResponse() {

        phocoViewModel.phocoTokenResponse.observe(viewLifecycleOwner, {

            when (it) {

                is Resources.Loading -> {

                    Log.d(TAG, "observeTokenResponse: Loading....")

                    if (!isLoggedInBefore) {

                        //todo : show loading animation
                    }
                }

                is Resources.Success -> {

                    Log.d(TAG, "observeTokenResponse: Login Successful")

                    Log.d(TAG, "observeTokenResponse: ${it.data}")

                    authTokens = AuthToken(
                        it.data?.refreshToken.toString(),
                        "Bearer " + it.data?.accessToken.toString(),
                        System.currentTimeMillis()
                    )


                    authTokens?.let { tokens ->

                        // saving the auth tokens to shared preference
                        saveAuthTokenInSharedPreferences(requireActivity(), tokens)

                        if (requireContext().isInternetAvailable()) {

                            // getting the user info
                            phocoViewModel.getPhocoUser(
                                primaryKey = null,
                                username = phocoUser?.username,
                                accessToken = tokens.accessToken
                            )
                        } else {
                            requireContext().showNoInternetMessage()
                        }
                    }

                }

                else -> {

                    Log.d(TAG, "observeTokenResponse: Error occurred")

                    Log.d(TAG, "observeTokenResponse: Error : ${it.message}")

                }
            }
        })
    }

    private fun observePhocoUserResponse() {

        phocoViewModel.phocoPhocoUserResponse.observe(viewLifecycleOwner, {

            when (it) {

                is Resources.Loading -> {

                    Log.d(TAG, "observePhocoUserResponse: Loading...")
                }

                is Resources.Success -> {

                    Log.d(TAG, "observePhocoUserResponse: Phoco User received")

                    Log.d(TAG, "observePhocoUserResponse: ${it.data}")

                    val phocoUser = it.data

                    phocoUser?.let { user ->

                        saveUserProfileSharedPreferences(requireActivity(), user)
                        Log.d(TAG, "observePhocoUserResponse: user saved in shared preference")

                        updateUI(user)
                    }

                }

                else -> {

                    Log.d(TAG, "observePhocoUserResponse: Error occurred")

                    Log.d(TAG, "observePhocoUserResponse: error : ${it.message}")

                    showToast(requireContext(), it.message.toString())
                }
            }
        })
    }

    private fun observeUploadImageResponse() {

        phocoViewModel.uploadImage.observe(viewLifecycleOwner, {

            when (it) {

                is Resources.Loading -> {

                    Log.d(TAG, "observeUploadImageResponse: Loading")
                }

                is Resources.Success -> {

                    Log.d(TAG, "observeUploadImageResponse: Success")

                    val data = it.data

                    Log.d(TAG, "observeUploadImageResponse: $data")

                    showToast(requireContext(), "${it.data}")
                }

                else -> {

                    Log.d(TAG, "observeUploadImageResponse: Error")
                    showToast(requireContext(), it.message.toString(), Toast.LENGTH_LONG)
                }
            }

        })
    }

    private fun updateUI(phocoUser: PhocoUser) {

        binding.nameOfTheUserTV.text = phocoUser.name
        binding.profileUsernameTV.text = phocoUser.username
        binding.numberOfFollowersTV.text = phocoUser.followers.toString()
        binding.numberOfFollowingTV.text = phocoUser.following.toString()

        /*  Glide.with(this)
              .load(phocoUser.user_image_url)
              .placeholder(R.drawable.ic_round_account_circle_24)
              .transition(DrawableTransitionOptions.withCrossFade())
              .into(binding.profileImageIV)*/
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}