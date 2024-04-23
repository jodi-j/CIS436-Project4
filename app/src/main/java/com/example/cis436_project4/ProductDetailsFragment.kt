package com.example.cis436_project4

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.example.cis436_project4.databinding.FragmentProductDetailsBinding
import com.google.android.material.chip.Chip
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ProductDetailsFragment : Fragment() {
    private lateinit var binding: FragmentProductDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentProductDetailsBinding.inflate(inflater, container, false)
        val rootView = binding.root

        // Find the back button
        val backButton = rootView.findViewById<Button>(R.id.btnBack)
        // Set an OnClickListener for the back button
        backButton.setOnClickListener {
            // Navigate back to the products page
            findNavController().navigateUp()
        }

        // Retrieve productId from arguments
        val productId = arguments?.getString("productId")

        // Query product based on productId
        lifecycleScope.launch{
            try {
                val product = withContext(Dispatchers.IO) {
                    val productDao = RoomDatabaseProvider.getInstance(requireContext()).productDao()
                    productDao.getOneProduct(productId.toString())
                }

                if (product.isNotEmpty()) {
                    val prod = product[0]

                    binding.tvNameDet.text = prod.name
                    binding.tvBrandDet.text = prod.brand.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }
                    binding.tvDescriptionDet.text = prod.description

                    //TODO: add website view in order to see product link directly
                    binding.tvPurchaseDet.text = prod.websiteLink

                    if (prod.tags == "[]") {
                        binding.tvTempTags.text = "No tags for this product."
                    } else {
                        binding.tvTempTags.visibility = View.GONE
                        val tags =
                            prod.tags?.removeSurrounding("[", "]")?.split(",") // Remove brackets and split into a list of tags
                        if (tags != null) {
                            for (tag in tags) {
                                val chip = Chip(requireContext())
                                chip.text = tag
                                chip.isClickable = false
                                binding.cgTags.addView(chip)
                            }
                        }
                    }

                    // Set image from image link
                    val glideUrl = GlideUrl(
                        prod.imageLink, LazyHeaders.Builder()
                            .addHeader(
                                "User-Agent",
                                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit / 537.36(KHTML, like Gecko) Chrome  47.0.2526.106 Safari / 537.36"
                            )
                            .build()
                    )
                    val productImage = rootView.findViewById<ImageView>(R.id.ivProduct)
                    Glide.with(requireContext())
                        .load(glideUrl)
                        .placeholder(R.drawable.ic_makeupplaceholder)
                        .error(R.drawable.ic_makeupplaceholder)
                        .into(productImage)
                }

            } catch (e: Exception) {
                Log.e("ProductDetailsFragment", "Error fetching product", e)
            }
        }

        return rootView
    }
}