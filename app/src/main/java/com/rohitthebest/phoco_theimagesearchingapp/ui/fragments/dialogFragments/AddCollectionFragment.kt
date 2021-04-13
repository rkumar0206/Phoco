package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments.dialogFragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentAddCollectionBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.showToasty
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddCollectionFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: FragmentAddCollectionBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return LayoutInflater.from(requireContext())
            .inflate(R.layout.fragment_add_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentAddCollectionBinding.bind(view)

        textWatcher()

        initListeners()
    }

    private fun initListeners() {

        binding.cancelBtn.setOnClickListener(this)
        binding.submitBtn.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.submitBtn.id -> {

                showToasty(requireContext(), "Collection Added")
            }

            binding.cancelBtn.id -> {

                dismiss()
            }
        }
    }

    private fun textWatcher() {


    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}