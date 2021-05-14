package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.api.UserResponse
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentProfileBinding
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.PhocoViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("SetTextI18n")
class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val phocoViewModel by viewModels<PhocoViewModel>()

    private var accessToken = ""
    private var userResponse: UserResponse? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfileBinding.bind(view)

        findNavController().navigate(R.id.action_profileFragment_to_signUpFragment)
    }

    private fun observeSignUpResponse() {

        phocoViewModel.phocoUserResponseSignUp.observe(viewLifecycleOwner, {

            when (it) {

                is Resources.Loading -> {

                }

                is Resources.Success -> {

                }

                else -> {

                }
            }
        })
    }

    private fun observeTokenResponse() {

        phocoViewModel.phocoTokenResponse.observe(viewLifecycleOwner, {


            when (it) {

                is Resources.Loading -> {

                }

                is Resources.Success -> {

                }

                else -> {


                }
            }
        })
    }

    private fun observePhocoUserResponse() {

        phocoViewModel.phocoPhocoUserResponse.observe(viewLifecycleOwner, {


            when (it) {

                is Resources.Loading -> {

                }

                is Resources.Success -> {

                }

                else -> {

                }
            }

        })
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}