package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

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
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentLoginSignupBinding
import com.rohitthebest.phoco_theimagesearchingapp.remote.AuthToken
import com.rohitthebest.phoco_theimagesearchingapp.remote.Resources
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.apiViewModels.PhocoViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "LoginSignUpFragment"

@AndroidEntryPoint
class LoginSignUpFragment : Fragment(R.layout.fragment_login_signup), View.OnClickListener {

    private val phocoViewModel by viewModels<PhocoViewModel>()

    private var _binding: FragmentLoginSignupBinding? = null
    private val binding get() = _binding!!

    private var authTokens: AuthToken? = null

    private var isLoginPageVisible = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentLoginSignupBinding.bind(view)

        setUpSignUpTextViewClick()
        setUpLoginTextViewClick()

        authTokens = getSavedAuthToken(requireActivity())

        if (authTokens != null) {

            findNavController().navigate(R.id.action_loginSignUpFragment_to_profileFragment)
        }

        observeTokenResponse()
        observePhocoUserResponse()
        observeSignUpResponse()

        initListeners()
        textWatchers()
    }


    private fun initListeners() {

        binding.loginBtn.setOnClickListener(this)
        binding.signUpBtn.setOnClickListener(this)
        binding.backBtn.setOnClickListener(this)   // show loginCL
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.loginBtn.id -> {

                if (requireContext().isInternetAvailable()) {

                    if (validateLoginFields()) {

                        phocoViewModel.loginUser(
                            binding.loginUsernameET.editText?.text.toString().trim(),
                            binding.loginPasswordET.editText?.text.toString().trim()
                        )
                    }

                } else {

                    requireContext().showNoInternetMessage()
                }
            }

            binding.signUpBtn.id -> {

                //todo : validate fields and call signup function from phocoViewModel
            }

            binding.backBtn.id -> {

                if (!isLoginPageVisible) {

                    // todo : show the loginCL
                }
            }
        }
    }

    private fun textWatchers() {

        // log in
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

        //sign up

        //todo :  initialize sign up textWatcher

    }

    private fun setUpSignUpTextViewClick() {

        val text = getString(R.string.don_t_have_an_account_sign_up)

        val startIndex = 23
        val endIndex = text.length

        val spannableString = SpannableString(text)

        val signUpClickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {

                hideLoginCL()
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

    private fun setUpLoginTextViewClick() {

        val text = getString(R.string.already_have_an_account_login)

        val startIndex = 25
        val endIndex = text.length

        val spannableString = SpannableString(text)

        val loginClickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {

                showLoginCL()
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)

                ds.color = ContextCompat.getColor(requireContext(), R.color.color_pink)
                ds.isUnderlineText = false
            }
        }

        spannableString.setSpan(
            loginClickableSpan,
            startIndex,
            endIndex,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        binding.alreadyHaveAnAccoutnTV.text = spannableString
        binding.alreadyHaveAnAccoutnTV.movementMethod = LinkMovementMethod.getInstance()

    }

    private fun validateLoginFields(): Boolean {

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

                    // todo : show loading view
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

                    //todo : show loading view
                }

                is Resources.Success -> {

                    // todo : hide loading view

                    Log.d(TAG, "observePhocoUserResponse: Phoco User received")

                    Log.d(TAG, "observePhocoUserResponse: ${it.data}")

                    val phocoUser = it.data

                    phocoUser?.let { user ->

                        Log.d(TAG, "observePhocoUserResponse: user saved in shared preference")
                        saveUserProfileSharedPreferences(requireActivity(), user)

                        findNavController().navigate(R.id.action_loginSignUpFragment_to_profileFragment)
                    }

                }

                else -> {

                    Log.d(TAG, "observePhocoUserResponse: error occurred: ${it.message}")

                    showToast(requireContext(), it.message.toString())
                }
            }
        })
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

    private fun showLoginCL() {

        try {
            isLoginPageVisible = !isLoginPageVisible


            // todo : perform some animation
            binding.signupCL.hide()
            binding.loginCL.show()
        } catch (e: IllegalStateException) {

            e.printStackTrace()
        }
    }

    private fun hideLoginCL() {

        try {
            isLoginPageVisible = !isLoginPageVisible


            // todo : perform some animation
            binding.loginCL.hide()
            binding.signupCL.show()

        } catch (e: IllegalStateException) {

            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}