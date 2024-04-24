package com.example.cis436_project4

import android.os.Bundle
import android.util.Log
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

class UpdatePreferenceFragment : Fragment() {
    private val selectedChips = mutableListOf<Chip>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_update_preference, container, false)
        val preferenceDao = RoomDatabaseProvider.getInstance(requireContext()).preferenceDao()
        val userPreferenceDao = RoomDatabaseProvider.getInstance(requireContext()).userPreferenceDao()
        lateinit var preferences: List<Preference>
        val userId = "1"
        // Fetch preferences from the database
        lifecycleScope.launch(Dispatchers.IO) {
            preferences = preferenceDao.getAllPreferences()

            // Fetch user preferences
            val userPrefs = userPreferenceDao.getUserPreferences(userId)

            // Update UI on the main thread
            withContext(Dispatchers.Main) {
                // Iterate over the preferences and add chips dynamically
                for (preference in preferences) {
                    val chipGroup: ChipGroup? = when (preference.type) {
                        "brand" -> rootView.findViewById(R.id.ChipGroupBrands)
                        "product_type" -> rootView.findViewById(R.id.ChipGroupProducts)
                        else -> null
                    }
                    chipGroup?.let {
                        // Add chip dynamically
                        val chip = Chip(requireContext())
                        chip.text = preference.value
                        chip.isCheckable = true
                        chip.isChecked =
                            userPrefs.any { it.preferenceID == preference.preferenceID } // Check if the preference is selected by the user
                        chip.setOnCheckedChangeListener { _, isChecked ->
                            if (isChecked) {
                                selectedChips.add(chip)
                            } else {
                                selectedChips.remove(chip)

                                // If deselected, delete the preference from the user preference table
                                lifecycleScope.launch(Dispatchers.IO) {
                                    val preferenceId = preferenceDao.getPrefID(chip.text.toString())
                                    preferenceId?.let { prefId ->
                                        userPreferenceDao.deleteUserPreference(userId, prefId)
                                    }
                                }
                            }
                        }
                        // Add chip to chip group
                        chipGroup.addView(chip)
                    }
                }
            }
        }

        // Submit Button handling
        val btnSubmitUpdates = rootView.findViewById<Button>(R.id.btnSubmitUpdates)
        btnSubmitUpdates.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {
                // Insert selected preferences into the userPreference table; THIS WORKS FS
                for (chip in selectedChips) {
                     val preferenceId = preferenceDao.getPrefID(chip.text.toString()) // Perform on background thread
                     preferenceId?.let { prefId ->
                         val userPreference = UserPreference(userId, prefId)
                         userPreferenceDao.insertUserPreference(userPreference) // Perform on background thread
                     }
                }

                // Log user preferences
                val userPreferences = userPreferenceDao.getUserPreferences(userId) // Perform on background thread
                for (pref in userPreferences) {
                    Log.d("UpdateFragment", "${pref.userID}, ${pref.preferenceID}")
                }

                //Navigate back to profile page
                withContext(Dispatchers.Main) {
                    findNavController().navigateUp()
                }
            }
        }
        return rootView
    }
}