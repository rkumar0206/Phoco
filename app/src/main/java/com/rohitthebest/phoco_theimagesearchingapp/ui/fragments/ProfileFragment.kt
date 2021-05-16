package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.AuthToken
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.data.phocoData.PhocoUser
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentProfileBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.PhocoViewModel
import dagger.hilt.android.AndroidEntryPoint

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

            // todo : navigate to loginsignup fragment

            isLoggedInBefore = false
        } else {

            Log.d(TAG, "onViewCreated: auth token is not null")

            // check if last updated date for getting the tokens is not more than 15 days
            authTokens?.let {

                if (calculateNumberOfDays(
                        it.dateWhenTokenReceived,
                        System.currentTimeMillis()
                    ) < 15
                ) {

                    //todo : remove the block comment after developemnt mode
                    /*phocoViewModel.getNewTokens(it.refreshToken)*/
                    isLoggedInBefore = true

                    phocoUser = getUserProfileData(requireActivity())

                    phocoUser?.let { user -> updateUI(user) }

                } else {

                    Log.d(
                        TAG,
                        "onViewCreated: number of days is greater than 15 and login is required"
                    )

                    //login again to receive new refresh token and access token
                    // todo : navigate to loginsignup fragment
                }
            }
        }

        observeTokenResponse()
        observePhocoUserResponse()

        initListeners()
    }



    private fun initListeners() {

        binding.backButton.setOnClickListener(this)
        binding.editProfileBtn.setOnClickListener(this)
        binding.followBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {


            binding.backButton.id -> {

                requireActivity().onBackPressed()
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

    private fun updateUI(phocoUser: PhocoUser) {

        binding.nameOfTheUserTV.text = phocoUser.name
        binding.profileUsernameTV.text = phocoUser.username

        Glide.with(this)
            .load(phocoUser.user_image_url)
            .placeholder(R.drawable.ic_round_account_circle_24)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.profileImageIV)
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}