package com.example.cis436_project4

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ProductDetailsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_product_details, container, false)

        // Find the back button
        val backButton = rootView.findViewById<Button>(R.id.btnBack)

        // Set an OnClickListener for the back button
        backButton.setOnClickListener {
            // Navigate back to the products page
            findNavController().navigateUp()
        }

        return rootView
    }
}