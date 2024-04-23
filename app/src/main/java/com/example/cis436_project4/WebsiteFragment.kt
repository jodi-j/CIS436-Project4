package com.example.cis436_project4

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.cis436_project4.databinding.FragmentWebsiteBinding

class WebsiteFragment : Fragment() {
    private lateinit var binding: FragmentWebsiteBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentWebsiteBinding.inflate(inflater, container, false)
        val rootView = binding.root

        val backButton = rootView.findViewById<Button>(R.id.btnBackToProduct)
        // Set an OnClickListener for the back button
        backButton.setOnClickListener {
            // Navigate back to the products page
            findNavController().navigateUp()
        }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enable JavaScript (if needed)
        binding.webView.settings.javaScriptEnabled = true

        // Load the URL
        val url = arguments?.getString("url")
        if (url != null) {
            binding.webView.loadUrl(url)
            binding.webView.webViewClient = WebViewClient()
        }
    }

}