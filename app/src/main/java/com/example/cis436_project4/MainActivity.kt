package com.example.cis436_project4

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.cis436_project4.databinding.ActivityMainBinding
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.DelicateCoroutinesApi
import org.json.JSONArray
import org.json.JSONObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var navController: NavController
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Navigation Bar Handling
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.item_1 -> {
                    navController.navigate(R.id.swipingFragment)
                    true
                }
                R.id.item_2 -> {
                    navController.navigate(R.id.productsFragment)
                    true
                }
                R.id.item_3 -> {
                    navController.navigate(R.id.profileFragment)
                    true
                }
                else -> false
            }
        }

    // Call Makeup API to access product information
        GlobalScope.launch(Dispatchers.IO) {
            // ONLY UNCOMMENT LINE BELOW IF you want to clear entire database and populate it from scratch
            //RoomDatabaseProvider.getInstance(this@MainActivity).clearAllTables()

            if (isDatabaseEmpty()) {
                getProductData()
            }
        }
    } // end onCreate

    //Makeup API Interaction
    private fun getProductData() {
        val productTypes = listOf("blush", "bronzer", "eyebrow", "eyeliner", "eyeshadow",
                                  "foundation", "lip_liner", "lipstick", "mascara", "nail_polish")

        val queue = Volley.newRequestQueue(this)

        // Call API for each product type
        for(type in productTypes) {
            val makeupURL = "http://makeup-api.herokuapp.com/api/v1/products.json?product_type=$type"
            //Request string response from URL
            val stringRequest = StringRequest(
                Request.Method.GET, makeupURL,
                { response ->
                    // Call populate database function to populate the database
                    populateDatabase(response)
                },
                { error ->
                    // Handle errors when performing API call
                    Log.e("MainActivity", "Error: ${error.message}")
                })

            //Add request to RequestQueue
            queue.add(stringRequest)
        }
    } //end getProductData

    // Populate database using API response
    private fun populateDatabase(response: String) {
        try {
            val productArray = JSONArray(response)

            // Iterate over each product in the JSON response
            for (i in 0 until productArray.length()) {
                val product: JSONObject = productArray.getJSONObject(i)
                // Store product info in Product entity class
                val productInfo = Product(
                    productID = product.getString("id"),
                    brand = product.getString("brand"),
                    name = product.getString("name"),
                    price = product.getString("price"),
                    imageLink = product.getString("image_link"),
                    websiteLink = product.getString("product_link"),
                    description = product.getString("description"),
                    type = product.getString("product_type"),
                    tags = product.getString("tag_list")
                )
                // Insert product into database
                insertProduct(productInfo)
            }
            // Insert default customer into database
            insertUser()

            // Insert products into user collection
            insertIntoCollection()

            // Insert all preferences into preferences
            insertPreference()

            // Insert product-preference connections into productPreferences
            insertProductPreference()

        } catch (e: Exception) {
            // Handle errors when parsing API response
            Log.e("MainActivity", "Error parsing API response: ${e.message}")
        }
    } // end populateDatabase

    // Insert product into Room Database
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertProduct(product: Product) {
        GlobalScope.launch(Dispatchers.IO) {
            // Access database instance
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val productDao = database.productDao()

            // INSERT product into Product table
            productDao.insert(product)
        }
    } // end insertProduct

    // Insert customer into Room Database
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertUser() {
        GlobalScope.launch(Dispatchers.IO) {
            // Access database instance
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val userDao = database.userDao()

            // INSERT one user into User table
            val userInfo = User(
                userID = "1",
                username = "admin",
                email = "admin@email.com"
            )
            userDao.insert(userInfo)
        }
    } // end insert User

    // Insert 4 product's into user 1's collection in Room Database
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertIntoCollection() {
        GlobalScope.launch(Dispatchers.IO) {
            // Access database instance
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val userCollectionDao = database.userCollectionDao()

            // Define array of product IDs
            val productIDs = listOf("987", "986", "985", "1032")

            // Iterate over the product IDs and INSERT each product into UserCollection table
            for (productID in productIDs) {
                val userProd = UserCollection(
                    userID = "1",
                    productID = productID
                )
                userCollectionDao.insert(userProd)
            }
        }
    } // end insertIntoCollection

    // Insert preferences into Room Database
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertPreference() {
        GlobalScope.launch(Dispatchers.IO) {
            // Access database instance
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val preferenceDao = database.preferenceDao()

            // Define list of brands
            val brands = listOf(
                "almay", "alva", "anna sui", "annabelle", "benefit", "boosh", "burt's bees", "butter london",
                "c'est moi", "cargo cosmetics", "china glaze", "clinique", "coastal classic creation", "colourpop",
                "covergirl", "dalish", "deciem", "dior", "dr. hauschka", "e.l.f.", "essie", "fenty", "glossier",
                "green people", "iman", "l'oreal", "lotus cosmetics usa", "maia's mineral galaxy", "marcelle", "marienatie",
                "maybelline", "milani", "mineral fusion", "misa", "mistura", "moov", "nudus", "nyx", "orly", "pacifica",
                "penny lane organics", "physicians formula", "piggy paint", "pure anada", "rejuva minerals", "revlon",
                "sally b's skin yummies", "salon perfect", "sante", "sinful colours", "smashbox", "stila", "suncoat",
                "w3llpeople", "wet n wild", "zorah", "zorah biocosmetiques"
            )

            // Define list of product types
            val productTypes = listOf(
                "blush", "bronzer", "eyebrow", "eyeliner", "eyeshadow", "foundation", "lip_liner", "lipstick", "mascara", "nail_polish"
            )

            var prefID = 1
            // Iterate over each brand and add to preference table
            for (brand in brands) {
                val preference = Preference(
                    preferenceID = prefID,
                    type = "brand",
                    value = brand
                )
                preferenceDao.insert(preference)
                prefID++
            }
            // Iterate over each product type and add to preference table
            for (product in productTypes) {
                val preference = Preference(
                    preferenceID = prefID,
                    type = "product_type",
                    value = product
                )
                preferenceDao.insert(preference)
                prefID++
            }
        }
    } // end insertPreference

    // Insert product-preference relationship into productPreference
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertProductPreference() {
        GlobalScope.launch(Dispatchers.IO) {
            // Access database instance
            val productDao = RoomDatabaseProvider.getInstance(this@MainActivity).productDao()
            val productPreferenceDao =
                RoomDatabaseProvider.getInstance(this@MainActivity).productPreferenceDao()
            val preferenceDao =
                RoomDatabaseProvider.getInstance(this@MainActivity).preferenceDao()

            // Get all products in the product table
            val products = productDao.getAllProducts()
            // Iterate over all products in order to connect to a preference
            for (product in products) {
                // Get preference id based on brand
                val brandPreference = preferenceDao.getPreference("brand", product.brand)
                // Get preference id based on product type if it's not null or empty
                val typePreference =
                    product.type?.takeIf { it.isNotBlank() }?.let { preferenceDao.getPreference("product_type", it) }

                // Combine both preferences into a single list
                val preferences = mutableListOf<Preference>().apply {
                    addAll(brandPreference)
                    typePreference?.let { addAll(it) }
                }

                // INSERT product-preference connection into ProductPreference table
                for (preference in preferences) {
                    val productPreference =
                        ProductPreference(product.productID, preference.preferenceID)
                    productPreferenceDao.insert(productPreference)
                }
            }
        }
    } // end insertProductPreference

    // Check if database is empty
    private fun isDatabaseEmpty(): Boolean {
        // Access database
        val database = RoomDatabaseProvider.getInstance(this)
        val productDao = database.productDao()
        return productDao.getAllProducts().isEmpty()
    }
}