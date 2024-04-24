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
        // Fetch preferences from the database
        lifecycleScope.launch(Dispatchers.IO) {
            preferences = preferenceDao.getAllPreferences() // Perform on background thread

            // Fetch user preferences
            val userId = "1" // Replace "1" with the actual user ID
            val userPrefs = userPreferenceDao.getUserPreferences(userId) // Perform on background thread

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
        val userId = "1"
        btnSubmitUpdates.setOnClickListener {
            lifecycleScope.launch(Dispatchers.IO) {

                // Delete deselected preferences from the userPreference table
                /*val selectedPreferenceIds = selectedChips.mapNotNull { chip ->
                    preferenceDao.getPrefID(chip.text.toString()) // Perform on background thread
                }
                val allPreferenceIds = preferences.map { it.preferenceID }
                val deselectedPreferenceIds = allPreferenceIds.filterNot { selectedPreferenceIds.contains(it) }
                for (prefId in deselectedPreferenceIds) {
                    userPreferenceDao.deleteUserPreference(userId, prefId) // Perform on background thread
                }

                // Log deselected preference IDs
                Log.d("UpdateFragment", "Deselected Preference IDs: $deselectedPreferenceIds")*/

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