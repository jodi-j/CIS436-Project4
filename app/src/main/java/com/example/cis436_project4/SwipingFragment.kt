package com.example.cis436_project4

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SwipingFragment : Fragment() {
    //variable for image view for product
    private lateinit var imageViewProduct: ImageView
    //all product text views
    private lateinit var textViewProductName: TextView
    private lateinit var textViewProductBrand: TextView
    private lateinit var textViewProductDescription: TextView
    private lateinit var buttonLike: Button
    private lateinit var buttonDislike: Button


    //variables to retrieve products from database (put them into a list)
    private lateinit var products: List<Product>
    private var currentIndex = 0




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_swiping, container, false)
        imageViewProduct = rootView.findViewById(R.id.ivProductImg)
        textViewProductName = rootView.findViewById(R.id.tvProdname)
        textViewProductBrand = rootView.findViewById(R.id.tvBrandname)
        textViewProductDescription = rootView.findViewById(R.id.tvProdDesc)
        buttonLike = rootView.findViewById(R.id.buttonLike)
        buttonDislike = rootView.findViewById(R.id.buttonDislike)

        loadProductsBasedOnPref()  // Initialize and load all products based on user preferences

        buttonLike.setOnClickListener {
            likeProduct()
        }

        buttonDislike.setOnClickListener {
            loadNextProduct()
        }

        return rootView
    }

    private fun loadProductsBasedOnPref() {
        lifecycleScope.launch {
            // Assuming user ID is "1"
            products = withContext(Dispatchers.IO) {
                //using userid 1
                RoomDatabaseProvider.getInstance(requireContext()).productDao().getProductsBasedOnUserPreferences("1")
            }
            if (products.isNotEmpty()) {
                updateUI(products[currentIndex])
            }
            else{

                Log.e("SwipingFragment", "Debug msg: Product List empty from user pref.")
            }
        }
    }

    private fun loadNextProduct() {
        if (currentIndex < products.size - 1) {
            currentIndex++
        } else {
            currentIndex = 0  // Loop back to the first product if at the end of the list
        }
        updateUI(products[currentIndex])
    }


    //using chatgpt to debug this
    private fun likeProduct() {
        if (this::products.isInitialized && products.isNotEmpty()) {
            val currentProduct = products[currentIndex]
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val result = RoomDatabaseProvider.getInstance(requireContext()).userCollectionDao()
                        .insertProductIntoUserBag(UserCollection(userID = "1", productID = currentProduct.productID))
                    if (result.equals(-1L)) { // -1 means insertion was ignored due to conflict
                        Log.d("SwipingFragment", "Product already in the bag")

                    }




                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        //debug messages!!
                        Log.e("SwipingFragment", "Error inserting product into bag: ${e.message}")
                        Log.d("SwipingFragment", "Here is the productID: ${currentProduct.productID}")
                    }
                }
                // Remove the liked product from the list if it was successfully added
                products = products.filter { it.productID != currentProduct.productID }
                //move to next product
                withContext(Dispatchers.Main) {
                    loadNextProduct()
                }
            }
        }
    }



    private fun updateUI(product: Product) {
        textViewProductName.text = product.name
        textViewProductBrand.text = product.brand
        textViewProductDescription.text = product.description

        Glide.with(this)
            .load(product.imageLink)
            .placeholder(R.drawable.ic_makeupplaceholder) // shown while the image is loading
            .error(R.drawable.ic_makeupplaceholder)       // shown if the image fails to load
            .into(imageViewProduct)
    }
}
