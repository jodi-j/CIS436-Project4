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
    private var productInfoList: MutableList<Product> = ArrayList()
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
    fun getProductData() {
        val productTypes = listOf("blush", "bronzer", "eyebrow", "eyeliner", "eyeshadow",
                                  "foundation", "lip_liner", "lipstick", "mascara", "nail_polish")

        val queue = Volley.newRequestQueue(this)

        //Request string response from URL
        for(type in productTypes) {
            val makeupURL = "https://makeup-api.herokuapp.com/api/v1/products.json?product_type=$type"
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
                    // TODO: handling for the tags
                )
                // Insert product into database
                insertProduct(productInfo)

                // Print all data in Products table into log
                //fetchDataFromDatabase()
            }

            // Insert default customer into database
            insertUser()

            // Insert products into user collection
            insertIntoCollection()

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
            val users = userDao.getAllUsers()
            for (user in users) {
                Log.d("Main Activity", "User ID: ${user.userID}, ${user.username}, ${user.email}")
            }
        }
    }

    // Insert 4 product's into user 1's collection
    @OptIn(DelicateCoroutinesApi::class)
    private fun insertIntoCollection() {
        GlobalScope.launch(Dispatchers.IO) {
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val userCollectionDao = database.userCollectionDao()

            // Insert first product
            val userProd1 = UserCollection(
                userID = "1",
                productID = "987"
            )
            userCollectionDao.insert(userProd1)

            // Insert second product
            val userProd2 = UserCollection(
                userID = "1",
                productID = "986"
            )
            userCollectionDao.insert(userProd2)

            // Insert third product
            val userProd3 = UserCollection(
                userID = "1",
                productID = "985"
            )
            userCollectionDao.insert(userProd3)

            val userProd4 = UserCollection(
                userID = "1",
                productID = "1032"
            )
            userCollectionDao.insert(userProd4)

            val collection = userCollectionDao.getUserCollection("1")
            for (product in collection) {
                Log.d("Main Activity", "User ID: ${product.userID}, ${product.productID}")
            }
        }
    }

    //Check if database is empty
    private fun isDatabaseEmpty(): Boolean {
        val database = RoomDatabaseProvider.getInstance(this)
        val productDao = database.productDao()
        return productDao.getAllProducts().isEmpty()
    }

    //Fetch data from the database
    @OptIn(DelicateCoroutinesApi::class)
    private fun fetchDataFromDatabase() {
        GlobalScope.launch(Dispatchers.IO) {
            val database = RoomDatabaseProvider.getInstance(this@MainActivity)
            val productDao = database.productDao()

            // Fetch all products from the database
            val products = productDao.getAllProducts()

            // Log the fetched products
            for (product in products) {
                Log.d("MainActivity", "Product ID: ${product.productID},  ${product.brand}, ${product.type}, ${product.tags} ")
            }
        }
    }
}