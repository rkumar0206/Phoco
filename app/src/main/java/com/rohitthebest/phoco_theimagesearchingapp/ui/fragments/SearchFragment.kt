package com.rohitthebest.phoco_theimagesearchingapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import com.rohitthebest.phoco_theimagesearchingapp.R
import com.rohitthebest.phoco_theimagesearchingapp.databinding.FragmentSearchBinding
import com.rohitthebest.phoco_theimagesearchingapp.ui.adapters.SpinnerSearchIconAdapter
import com.rohitthebest.phoco_theimagesearchingapp.utils.APIName
import com.rohitthebest.phoco_theimagesearchingapp.utils.APIsInfo


class SearchFragment : Fragment(R.layout.fragment_search) {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var spinnerAdapter: SpinnerSearchIconAdapter
    private lateinit var spinnerList: ArrayList<APIsInfo>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchBinding.bind(view)

        spinnerList = ArrayList()

        initSpinnerList()

        spinnerAdapter = SpinnerSearchIconAdapter(requireContext(), spinnerList)

        setUpWebsiteSpinner()
    }

    private fun setUpWebsiteSpinner() {

        binding.searchWithWebsiteSpinner.adapter = spinnerAdapter

        binding.searchWithWebsiteSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {

                val clickedItem = parent?.getItemAtPosition(position) as APIsInfo

                when (clickedItem.apiName) {

                    APIName.UNSPLASH -> binding.searchBoxACT.hint = "Search on ${getString(R.string.unsplash)}"
                    APIName.PIXABAY -> binding.searchBoxACT.hint = "Search on ${getString(R.string.pixabay)}"
                    APIName.PEXELS -> binding.searchBoxACT.hint = "Search on ${getString(R.string.pexels)}"
                    APIName.WEB -> binding.searchBoxACT.hint = "Search on ${getString(R.string.web)}"

                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                //TODO("Not yet implemented")
            }
        }
    }

    private fun initSpinnerList() {

        spinnerList.add(APIsInfo(APIName.UNSPLASH, R.drawable.logo_unsplash))
        spinnerList.add(APIsInfo(APIName.PIXABAY, R.drawable.logo_pixabay_square))
        spinnerList.add(APIsInfo(APIName.PEXELS, R.drawable.logo_pexels))
        spinnerList.add(APIsInfo(APIName.WEB, R.drawable.ic_baseline_web_24))

    }


    override fun onDestroyView() {
        super.onDestroyView()

        _binding = null
    }

}