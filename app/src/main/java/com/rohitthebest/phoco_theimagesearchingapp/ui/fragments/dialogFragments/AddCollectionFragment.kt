package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments.dialogFragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.rohitthebest.phoco_theimagesearchingapp.Constants.EDIT_TEXT_EMPTY_MESSAGE
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.database.entity.Collection
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentAddCollectionBinding
import com.rohitthebest.phoco_theimagesearchingapp.utils.*
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.CollectionViewModel
import dagger.hilt.android.AndroidEntryPoint

private const val TAG = "AddCollectionFragment"

@AndroidEntryPoint
class AddCollectionFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: FragmentAddCollectionBinding? = null
    private val binding get() = _binding!!

    private val collectionViewModel by viewModels<CollectionViewModel>()

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

    private var isRefreshEnabled = false

    override fun onClick(v: View?) {

        when (v?.id) {

            binding.submitBtn.id -> {

                if (validateForm()) {

                    isRefreshEnabled = true

                    checkForDuplicatesAndInsertNewCollection()
                }
            }

            binding.cancelBtn.id -> {

                dismiss()
            }
        }
    }


    private fun checkForDuplicatesAndInsertNewCollection() {

        var isDuplicateExist = false

        collectionViewModel.getAllCollection().observe(viewLifecycleOwner, {

            if (isRefreshEnabled) {

                if (it.isNotEmpty()) {

                    for (i in it) {

                        if (i.collectionName.equals(
                                        binding.collectionNameET.editText?.text?.trim().toString(),
                                        ignoreCase = true)
                        ) {

                            isDuplicateExist = true
                            break
                        }
                    }

                    if (isDuplicateExist) {

                        showToasty(requireContext(), "Collection already exists", ToastyType.ERROR)
                    } else {

                        insertNewCollectionToDatabase()
                    }

                } else {

                    insertNewCollectionToDatabase()
                }

                isRefreshEnabled = false
            }
        })
    }

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

        showSnackBar(binding.root, "Collection added")

        dismiss()
    }

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