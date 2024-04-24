package com.example.cis436_project4

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    // SELECT all from User table
    @Query("SELECT * FROM User")
    fun getAllUsers(): List<User>

    // INSERT a user
    @Insert
    fun insert(user: User)

    // DELETE everything from table
    @Query("DELETE FROM User")
    fun deleteAll()
}

@Dao
interface ProductDao {
    // SELECT one product
    @Query("SELECT * FROM Product p WHERE p.productID = :productId")
    fun getOneProduct(productId: String): List<Product>

    // SELECT all from Product table
    @Query("SELECT * FROM Product")
    fun getAllProducts(): List<Product>

    // SELECT all products in user's collection
    @Query("SELECT * " +
            "FROM Product p " +
            "JOIN UserCollection uc ON p.productID = uc.productID " +
            "WHERE uc.userID = :userId")
    fun getProductsInUserBag(userId: String): List<Product>

    // SELECT all products based on user's preferences
    @Query("SELECT DISTINCT * " +
            "FROM Product p " +
            "JOIN ProductPreference pp ON p.productID = pp.productID " +
            "JOIN Preference pref ON pp.preferenceID = pref.preferenceID " +
            "JOIN UserPreference up ON pref.preferenceID = up.preferenceID " +
            "WHERE up.userID = :userId")
    fun getProductsBasedOnUserPreferences(userId: String): List<Product>

    // INSERT a product
    @Insert
    fun insert(product: Product)

    // DELETE everything from table
    @Query("DELETE FROM Product")
    fun deleteAll()
}


@Dao
interface PreferenceDao {
    // SELECT all from Preference table
    @Query("SELECT * FROM Preference")
    fun getAllPreferences(): List<Preference>

    // SELECT all of user's preferences
    @Query("SELECT p.preferenceID, p.type, p.value " +
            "FROM Preference p " +
            "JOIN UserPreference up ON p.preferenceID = up.preferenceID " +
            "WHERE up.userID = :userId")
    fun getUserPreferences(userId: String): List<Preference>

    // SELECT a preference by type and value
    @Query("SELECT * FROM Preference WHERE type = :type AND value = :value")
    fun getPreference(type: String, value: String): List<Preference>

    // SELECT a preference ID by value
    @Query("SELECT preferenceID FROM Preference WHERE value = :value")
    fun getPrefID(value: String): Int?

    // SELECT a preference by ID
    @Query("SELECT * FROM preference WHERE preferenceID = :preferenceId")
    fun getPreferenceById(preferenceId: Int): Preference

    // INSERT a preference
    @Insert
    fun insert(preference: Preference)

    // DELETE everything from table
    @Query("DELETE FROM Preference")
    fun deleteAll()
}


@Dao
interface UserPreferenceDao {
    // SELECT all of a user's preferences from UserPreference table
    @Query("SELECT * FROM UserPreference WHERE userID = :userId")
    fun getUserPreferences(userId: String): List<UserPreference>

    // INSERT a user preference
    @Insert
    fun insert(userPreference: UserPreference)

    // INSERT preference into user's preferences
    @Insert
    fun insertUserPreference(userPreference: UserPreference)

    // DELETE preference from user's preferences
    @Query("DELETE FROM UserPreference WHERE userID = :userId AND preferenceID = :preferenceId")
    fun deleteUserPreference(userId: String, preferenceId: Int)

    // DELETE everything in table
    @Query("DELETE FROM UserPreference")
    fun deleteAll()
}


@Dao
interface ProductPreferenceDao {
    // SELECT all from ProductPreferences
    @Query("SELECT * FROM ProductPreference")
    fun getAllProdPrefs(): List<ProductPreference>

    // SELECT all of the product preferences for a specific product
    @Query("SELECT * FROM ProductPreference WHERE productID = :productId")
    fun getProductPreferences(productId: String): List<ProductPreference>

    // INSERT a product preference
    @Insert
    fun insert(productPreference: ProductPreference)

    // DELETE everything from table
    @Query("DELETE FROM ProductPreference")
    fun deleteAll()
}

@Dao
interface UserCollectionDao {
    // SELECT all of the products in a user's collection
    @Query("SELECT * FROM UserCollection WHERE userID = :userId")
    fun getUserCollection(userId: String): List<UserCollection>

    // INSERT into user collection
    @Insert
    fun insert(userCollection: UserCollection)

    // INSERT a product into user's collection
    @Insert
    fun insertProductIntoUserBag(userCollection: UserCollection)

    // DELETE everything from table
    @Query("DELETE FROM UserCollection")
    fun deleteAll()
}
