package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments.dialogFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rohitthebest.phoco_theimagesearchingapp.Constants.EDIT_TEXT_EMPTY_MESSAGE
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentAddCollectionBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.showToasty
import com.rohitthebest.phoco_theimagesearchingapp.utils.validateString
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddCollectionFragment"

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

                if (validateForm()) {

                    showToasty(requireContext(), "Collection Added")
                    //insertNewCollectionToDatabase()
                }
            }

            binding.cancelBtn.id -> {

                dismiss()
            }
        }
    }

/*
    private fun insertNewCollectionToDatabase() {

        Log.d(TAG, "insertNewCollectionToDatabase: ")

        val collection = Collection(
            key = generateKey(),
            collectionName = binding.collectionNameET.editText?.text?.trim().toString(),
            collectionDescription = binding.collectionDescriptionET.editText?.text?.trim()
                .toString(),
            collectionImageUrl = "",
            uid = ""
        )

        collectionViewModel.insertCollection(collection)

        showToasty(requireContext(), "Collection added")

        dismiss()
    }
*/

    private fun validateForm(): Boolean {

        if (!binding.collectionNameET.editText?.text.toString().validateString()) {

            binding.collectionNameET.error = EDIT_TEXT_EMPTY_MESSAGE
            return false
        }

        return binding.collectionNameET.error == null
    }

    private fun textWatcher() {

        binding.collectionNameET.editText?.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                if (s?.trim()?.isEmpty()!!) {

                    binding.collectionNameET.error = EDIT_TEXT_EMPTY_MESSAGE
                } else {

                    binding.collectionNameET.error = null
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}