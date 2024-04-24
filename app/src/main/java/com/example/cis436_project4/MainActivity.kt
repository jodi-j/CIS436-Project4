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


        // Call getProductData when activity starts if database is empty
        GlobalScope.launch(Dispatchers.IO) {
            //ONLY uncomment if you want to clear entire database and repopulate from scratch
            //RoomDatabaseProvider.getInstance(this@MainActivity).clearAllTables()


            if (isDatabaseEmpty()) {
                getProductData()
            }
        }
    }


    //Makeup API Interaction
    private fun getProductData() {
        val productTypes = listOf("blush", "bronzer", "eyebrow", "eyeliner", "eyeshadow",
            "foundation", "lip_liner", "lipstick", "mascara", "nail_polish")


        val queue = Volley.newRequestQueue(this)


        //Request string response from URL
        for(type in productTypes) {
            val makeupURL = "http://makeup-api.herokuapp.com/api/v1/products.json?product_type=$type"
            val stringRequest = StringRequest(
                Request.Method.GET, makeupURL,
                { response ->
                    populateDatabase(response)
                },
                { error ->


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


            for (i in 0 until productArray.length()) {
                val product: JSONObject = productArray.getJSONObject(i)
                // Store product info in class
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
            // Handle parsing errors or other exceptions
            Log.e("MainActivity", "Error parsing API response: ${e.message}")
        }
    }


    // Insert product into Room Database
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertProduct(product: Product) {
        GlobalScope.launch(Dispatchers.IO) {
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val productDao = database.productDao()


            productDao.insert(product)


            // log all products
            /*val products = productDao.getAllProducts() // fetch all products from the database
            for (prod in products) {
                Log.d("MainActivity", "Product ID: ${prod.productID},  ${prod.brand}, ${prod.type}, ${prod.tags} ")
            }*/
        }
    }


    // Insert customer into Room Database
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertUser() {
        GlobalScope.launch(Dispatchers.IO) {
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val userDao = database.userDao()


            // create and insert one user
            val userInfo = User(
                userID = "1",
                username = "admin",
                email = "admin@email.com"
            )
            userDao.insert(userInfo)


            // log all users in user table
            /*val users = userDao.getAllUsers()
            for (user in users) {
                Log.d("Main Activity", "User ID: ${user.userID}, ${user.username}, ${user.email}")
            }*/
        }
    }


    // Insert 4 product's into user 1's collection in Room Database
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertIntoCollection() {
        GlobalScope.launch(Dispatchers.IO) {
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val userCollectionDao = database.userCollectionDao()


            // Define an array of product IDs
            val productIDs = listOf("987", "986", "985", "1032")


            // Iterate over the product IDs and insert each product
            for (productID in productIDs) {
                val userProd = UserCollection(
                    userID = "1",
                    productID = productID
                )
                userCollectionDao.insert(userProd)
            }


            /*val collection = userCollectionDao.getUserCollection("1")
            for (product in collection) {
                Log.d("Main Activity", "User ID: ${product.userID}, ${product.productID}")
            }*/
        }
    }


    // Insert preferences into Room Database
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertPreference() {
        GlobalScope.launch(Dispatchers.IO) {
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val preferenceDao = database.preferenceDao()


            // Define lists of brands and product types
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
               // preferenceDao.insert(preference)
                prefID++
            }


            //Log all preferences
            /*val preferences = preferenceDao.getAllPreferences()
            for (preference in preferences) {
                Log.d("MainActivity", "${preference.preferenceID}, ${preference.type}, ${preference.value}")
            }*/


        }
    }


    // Insert product-preference relationship into productPreference
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertProductPreference() {
        GlobalScope.launch(Dispatchers.IO) {
            val productDao = RoomDatabaseProvider.getInstance(this@MainActivity).productDao()
            val productPreferenceDao =
                RoomDatabaseProvider.getInstance(this@MainActivity).productPreferenceDao()
            val preferenceDao =
                RoomDatabaseProvider.getInstance(this@MainActivity).preferenceDao()


            val products = productDao.getAllProducts()
            for (product in products) {
                // Get preference based on brand
                val brandPreference = preferenceDao.getPreference("brand", product.brand)
                // Get preference based on product type if it's not null or empty
                val typePreference =
                    product.type?.takeIf { it.isNotBlank() }?.let { preferenceDao.getPreference("product_type", it) }


                // Combine both preferences into a single list
                val preferences = mutableListOf<Preference>().apply {
                    addAll(brandPreference)
                    typePreference?.let { addAll(it) }
                }


                for (preference in preferences) {
                    val productPreference =
                        ProductPreference(product.productID, preference.preferenceID)
                    productPreferenceDao.insert(productPreference)
                }
            }


            // Log all product preferences
            val prodPrefs = productPreferenceDao.getAllProdPrefs()
            for (p in prodPrefs) {
                Log.d("Main Activity", "PrefID: ${p.preferenceID}, ProdID: ${p.productID}")
            }
        }
    }


    // Check if database is empty
    private fun isDatabaseEmpty(): Boolean {
        val database = RoomDatabaseProvider.getInstance(this)
        val productDao = database.productDao()
        return productDao.getAllProducts().isEmpty()
    }
}

