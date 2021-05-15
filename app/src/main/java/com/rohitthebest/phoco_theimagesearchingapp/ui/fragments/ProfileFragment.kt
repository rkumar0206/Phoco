package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.*
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

            // check if last updated date for getting the tokens is not more than 15 days
            authTokens?.let {

                if (calculateNumberOfDays(
                        it.dateWhenTokenReceived,
                        System.currentTimeMillis()
                    ) < 15
                ) {

                    binding.loginCL.hide()
                    Log.d(TAG, "onViewCreated: getting new tokens using refresh token")
                    phocoViewModel.getNewTokens(it.refreshToken)
                } else {

                    Log.d(
                        TAG,
                        "onViewCreated: number of days is greater than 15 and login is required"
                    )

                    //login again to receive new refresh token and access token
                    binding.loginCL.show()
                }
            }
        }

        observeTokenResponse()
        observePhocoUserResponse()

        initListeners()
        textWatchers()
    }

    private fun textWatchers() {

        binding.loginPasswordET.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.isEmpty()!!) {

                    binding.loginPasswordET.error = "Please enter the password"
                } else {

                    binding.loginPasswordET.error = null
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun initListeners() {

        binding.loginBtn.setOnClickListener(this)
        binding.forgotPasswordTV.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.loginBtn.id -> {

                if (validateFields()) {

                    Log.d(TAG, "onClick: Validation successful")

                    if (requireContext().isInternetAvailable()) {

                        phocoViewModel.loginUser(
                            binding.loginUsernameET.editText?.text.toString().trim(),
                            binding.loginPasswordET.editText?.text.toString().trim()
                        )
                    } else {

                        requireContext().showNoInternetMessage()
                    }
                }
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

    private fun validateFields(): Boolean {

        if (!binding.loginUsernameET.editText?.text.toString().trim().isValidString()) {

            binding.loginUsernameET.editText?.error = "Please specify username"
            return false
        }

        if (!binding.loginPasswordET.editText?.text.toString().trim().isValidString()) {

            binding.loginPasswordET.editText?.showKeyboard(requireActivity())
            binding.loginPasswordET.error = "Please enter the password"
            return false
        }

        return binding.loginUsernameET.editText?.text.toString().trim().isValidString()
                && binding.loginPasswordET.editText?.text.toString().trim().isValidString()
    }

    private fun observeTokenResponse() {

        phocoViewModel.phocoTokenResponse.observe(viewLifecycleOwner, {

            when (it) {

                is Resources.Loading -> {

                    Log.d(TAG, "observeTokenResponse: Loading....")
                    // show loading ui
                }

                is Resources.Success -> {

                    Log.d(TAG, "observeTokenResponse: Login Successful")

                    Log.d(TAG, "observeTokenResponse: ${it.data}")

                    authTokens = AuthToken(
                        it.data?.refreshToken.toString(),
                        "Bearer " + it.data?.accessToken.toString(),
                        System.currentTimeMillis()
                    )

                    // getting the user info

                    authTokens?.let { tokens ->

                        // saving the auth tokens to shared preference
                        saveAuthTokenInSharedPreferences(requireActivity(), tokens)

                        if (requireContext().isInternetAvailable()) {

                            phocoViewModel.getPhocoUser(
                                primaryKey = null,
                                username = binding.loginUsernameET.editText?.text.toString().trim(),
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

                    binding.loginUsernameET.editText?.error =
                        "No active account found with the given credential"
                    binding.loginUsernameET.editText?.showKeyboard(requireActivity())
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


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}