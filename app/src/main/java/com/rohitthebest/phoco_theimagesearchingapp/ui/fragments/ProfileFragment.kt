package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.data.AuthToken
import com.rohitthebest.phoco_theimagesearchingapp.data.Resources
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentProfileBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.getSavedAuthToken
import com.rohitthebest.phoco_theimagesearchingapp.utils.hide
import com.rohitthebest.phoco_theimagesearchingapp.utils.show
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentProfileBinding.bind(view)

        setUpSignUpTextViewClick()

        authTokens = getSavedAuthToken(requireActivity())

        if (authTokens == null) {

            Log.d(TAG, "onViewCreated: auth token is null")

            binding.loginCL.show()
        } else {

            Log.d(TAG, "onViewCreated: auth token is not null")

            binding.loginCL.hide()

            // check if last updated date for getting the tokens is not more than 15 days

            // if not : refresh the token

            // else show the login screen again
        }

        initListeners()
    }

    private fun initListeners() {

        binding.loginBtn.setOnClickListener(this)
        binding.forgotPasswordTV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.loginBtn.id -> {

                // login the user after the validation
            }

            binding.forgotPasswordTV.id -> {

                // open webview and send the email
            }
        }

    }


    private fun setUpSignUpTextViewClick() {

        val text = getString(R.string.don_t_have_an_account_sign_up)

        val startIndex = 23
        val endIndex = text.length

        val spannableString = SpannableString(text)

        val signUpClickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {

                // navigate to signup page
                findNavController().navigate(R.id.action_profileFragment_to_signUpFragment)
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)

                ds.color = ContextCompat.getColor(requireContext(), R.color.color_pink)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(
            signUpClickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.dontHaveAccountTV.text = spannableString
        binding.dontHaveAccountTV.movementMethod = LinkMovementMethod.getInstance()
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