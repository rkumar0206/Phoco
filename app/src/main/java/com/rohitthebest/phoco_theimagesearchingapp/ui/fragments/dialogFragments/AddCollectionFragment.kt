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
import com.rohitthebest.phoco_theimagesearchingapp.utils.ToastyType
import com.rohitthebest.phoco_theimagesearchingapp.utils.isValidString
import com.rohitthebest.phoco_theimagesearchingapp.utils.showToasty
import com.rohitthebest.phoco_theimagesearchingapp.viewmodels.databaseViewModels.CollectionViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

private const val TAG = "AddCollectionFragment"

@AndroidEntryPoint
class AddCollectionFragment : BottomSheetDialogFragment(), View.OnClickListener {

    private var _binding: FragmentAddCollectionBinding? = null
    private val binding get() = _binding!!

    private val collectionViewModel by viewModels<CollectionViewModel>()

    private var isCollectionKeyReceived = false
    private var receivedCollectionKey = ""
    private lateinit var receivedCollection: Collection

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

        getPassedArguments()

        textWatcher()

        initListeners()
    }

    private fun getPassedArguments() {

        try {
            if (!arguments?.isEmpty!!) {

                val args = arguments?.let {

                    AddCollectionFragmentArgs.fromBundle(it)
                }

                if (args?.collectionKey?.isValidString()!!) {

                    isCollectionKeyReceived = true
                    receivedCollectionKey = args.collectionKey!!
                    getCollection()
                }
            }
        } catch (e: NullPointerException) {

            isCollectionKeyReceived = false
            e.printStackTrace()
        }
    }

    private fun getCollection() {

        collectionViewModel.getCollectionByKey(receivedCollectionKey).observe(viewLifecycleOwner, {

            receivedCollection = it
            updateUI()
        })
    }

    private fun updateUI() {

        binding.apply {

            collectionNameET.editText?.setText(receivedCollection.collectionName)
            collectionDescriptionET.editText?.setText(receivedCollection.collectionDescription)

            dialogTitle.text = getString(R.string.edit_collection)
            submitBtn.text = getString(R.string.save)
        }
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

                            if (isCollectionKeyReceived &&
                                    i.collectionName.equals(receivedCollection.collectionName, true)) {

                                continue
                            } else {

                                isDuplicateExist = true
                                break
                            }

                        }
                    }

                    if (isDuplicateExist) {

                        showToasty(requireContext(), "Collection already exists", ToastyType.ERROR)
                    } else {

                        insertOrUpdateCollection()
                    }

                } else {

                    insertOrUpdateCollection()
                }

                isRefreshEnabled = false
            }
        })
    }

    private fun insertOrUpdateCollection() {

        if (!isCollectionKeyReceived) {

            Log.d(TAG, "insertNewCollectionToDatabase: ")

            val collection = Collection(
                key = UUID.randomUUID().toString(),
                collectionName = binding.collectionNameET.editText?.text?.trim().toString(),
                collectionDescription = binding.collectionDescriptionET.editText?.text?.trim()
                    .toString(),
                collectionImageUrl = "",
                uid = ""
            )

            collectionViewModel.insertCollection(collection)

            showToasty(requireContext(), "Collection added")
        } else {

            receivedCollection.apply {

                this.timestamp = System.currentTimeMillis()
                this.collectionName = binding.collectionNameET.editText?.text?.trim().toString()
                this.collectionDescription = binding.collectionDescriptionET.editText?.text?.trim()
                    .toString()
                this.collectionImageUrl = ""
            }

            collectionViewModel.updateCollection(receivedCollection)

            showToasty(requireContext(), "Collection updated")
        }

        dismiss()
    }

    private fun validateForm(): Boolean {

        if (!binding.collectionNameET.editText?.text.toString().isValidString()) {

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

    companion object {

        @JvmStatic
        fun newInstance(bundle: Bundle?): AddCollectionFragment {

            val fragment = AddCollectionFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}