package com.example.cis436_project4

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.google.android.material.search.SearchBar
import com.google.android.material.search.SearchView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class ProductsFragment : Fragment() {
    private lateinit var linearLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_products, container, false)
        linearLayout = rootView.findViewById(R.id.linearLayout)

        lifecycleScope.launch {
            try {
                // Access database instance and get all products from the user's collection
                val products = withContext(Dispatchers.IO) {
                    val productDao = RoomDatabaseProvider.getInstance(requireContext()).productDao()
                    productDao.getProductsInUserBag("1")
                }

                // Iterate over each product and dynamically create a card for it
                for (product in products) {
                    val cardView = layoutInflater.inflate(R.layout.product_card, null) as CardView
                    val productNameTextView = cardView.findViewById<TextView>(R.id.tvProduct)
                    val productBrandTextView = cardView.findViewById<TextView>(R.id.tvBrand)
                    val btnDetails = cardView.findViewById<Button>(R.id.btnDetails)

                    productNameTextView.text = product.name
                    productBrandTextView.text = product.brand.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }

                    // Set image from image link
                    val glideUrl = GlideUrl(
                        product.imageLink, LazyHeaders.Builder()
                            .addHeader(
                                "User-Agent",
                                "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_11_2) AppleWebKit / 537.36(KHTML, like Gecko) Chrome  47.0.2526.106 Safari / 537.36"
                            )
                            .build()
                    )
                    val productImage = cardView.findViewById<ImageView>(R.id.imageViewProduct)
                    Glide.with(requireContext())
                        .load(glideUrl)
                        .placeholder(R.drawable.ic_makeupplaceholder)
                        .error(R.drawable.ic_makeupplaceholder)
                        .into(productImage)

                    // Navigate to the ProductDetails Fragment on "Details" click
                    btnDetails.setOnClickListener {
                        val bundle = Bundle()
                        bundle.putString("productId", product.productID)
                        findNavController().navigate(
                            R.id.action_productsFragment_to_productDetailsFragment,
                            bundle
                        )
                    }

                    // Add margins to the card view
                    val layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                    )
                    val margin = resources.getDimensionPixelSize(R.dimen.card_margin)
                    layoutParams.setMargins(margin, margin, margin, margin)
                    cardView.layoutParams = layoutParams

                    // Add margin to the bottom of the card view
                    layoutParams.bottomMargin = resources.getDimensionPixelSize(R.dimen.card_bottom_margin)
                    cardView.layoutParams = layoutParams

                    // Check if cardView already has a parent
                    val parent = cardView.parent
                    if (parent is ViewGroup) {
                        parent.removeView(cardView) // Remove cardView from parent if it exists
                    }
                    // Add card view to the linear layout
                    linearLayout.addView(cardView)
                }
            } catch (e: Exception) {
                // Handle errors
                Log.e("ProductsFragment", "Error fetching products", e)
            }
        }
        return rootView
    }
}