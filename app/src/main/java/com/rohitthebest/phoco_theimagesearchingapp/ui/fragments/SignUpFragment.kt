package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentSignupBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_signup) {

    private var _binding: FragmentSignupBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSignupBinding.bind(view)

        binding.backBtn.setOnClickListener {

            requireActivity().onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }
}