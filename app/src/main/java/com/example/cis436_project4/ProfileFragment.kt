package com.example.cis436_project4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_profile, container, false)

        lifecycleScope.launch {
            val userPreferenceDao = RoomDatabaseProvider.getInstance(requireContext()).userPreferenceDao()
            val preferenceDao = RoomDatabaseProvider.getInstance(requireContext()).preferenceDao()

            // Fetch user preferences from the database for the current user
            val userPrefs = withContext(Dispatchers.IO) {
                userPreferenceDao.getUserPreferences("1") // Assuming user ID is "1"
            }

            // Iterate over each user preference and fetch the corresponding preference from the preference table
            for (userPref in userPrefs) {
                val preferenceId = userPref.preferenceID

                // Fetch the preference from the preference table using the preference ID
                val preference = withContext(Dispatchers.IO) {
                    preferenceDao.getPreferenceById(preferenceId)
                }

                // Add the preference as a chip to the appropriate chip group
                val chipGroup: ChipGroup? = when (preference.type) {
                    "brand" -> rootView.findViewById(R.id.ChipGroupBrands)
                    "product_type" -> rootView.findViewById(R.id.ChipGroupProducts)
                    else -> null
                }
                chipGroup?.let {
                    // Add chip dynamically
                    val chip = Chip(requireContext())
                    chip.text = preference.value
                    chip.isClickable = false
                    chip.isCheckable = false
                    chipGroup.addView(chip)
                }
            }

            // Navigate to UpdatePreferenceFragment when "Update Preferences" is clicked
            val btnUpdate = rootView.findViewById<Button>(R.id.btnUpdate)
            btnUpdate.setOnClickListener{
                findNavController().navigate(R.id.action_profileFragment_to_updatePreferenceFragment)
            }
        }

        return rootView
    }
}