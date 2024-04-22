package com.example.cis436_project4

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
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
    private lateinit var searchBar: SearchBar
    private lateinit var searchView: SearchView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_products, container, false)
        val rootView = inflater.inflate(R.layout.fragment_products, container, false)
        val linearLayout: LinearLayout = rootView.findViewById(R.id.linearLayout)
        searchBar = rootView.findViewById(R.id.search_bar)
        searchView = rootView.findViewById(R.id.search_view)

        /*searchView
            .editText
            .setOnEditorActionListener { v: TextView?, actionId: Int, event: KeyEvent? ->
                searchBar.setText(searchView.text)
                searchView.hide()
                false
            }*/

        lifecycleScope.launch {
            try {
                // Retrieve product information and populate card views
                val products = withContext(Dispatchers.IO) {
                    /*val productDao = RoomDatabaseProvider.getInstance(requireContext()).productDao()
                    productDao.getAllProducts()*/
                    val productDao = RoomDatabaseProvider.getInstance(requireContext()).productDao()
                    productDao.getProductsInUserBag("1")
                }

                //val maxProducts = 3
                //var counter = 0

                for (product in products) {
                    Log.d("ProductFragment", "${product.productID}, ${product.brand}, ${product.name}")
                    /*if (counter >= maxProducts) {
                        break
                    }*/

                    val cardView = layoutInflater.inflate(R.layout.product_card, null) as CardView
                    val productNameTextView = cardView.findViewById<TextView>(R.id.tvProduct)
                    val productBrandTextView = cardView.findViewById<TextView>(R.id.tvBrand)

                    // Set product details to views in the card
                    productNameTextView.text = product.name
                    productBrandTextView.text = product.brand.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase(
                            Locale.ROOT
                        ) else it.toString()
                    }

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


                    // Check if cardView already has a parent
                    val parent = cardView.parent
                    if (parent is ViewGroup) {
                        parent.removeView(cardView) // Remove cardView from parent if it exists
                    }
                    // Add card view to the linear layout
                    linearLayout.addView(cardView)

                    //counter++
                }
            } catch (e: Exception) {
                Log.e("ProductsFragment", "Error fetching products", e)
            }
        }

        return rootView
    }

}