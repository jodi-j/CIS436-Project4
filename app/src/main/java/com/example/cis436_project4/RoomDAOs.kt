package com.example.cis436_project4

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    // SELECT all from User table
    @Query("SELECT * FROM User")
    fun getAllUsers(): List<User>

    @Insert
    fun insert(user: User)

    @Query("DELETE FROM User")
    fun deleteAll()
}

@Dao
interface ProductDao {
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
    @Query("SELECT DISTINCT p.productID, p.name, p.brand " +
            "FROM Product p " +
            "JOIN ProductPreference pp ON p.productID = pp.productID " +
            "JOIN Preference pref ON pp.preferenceID = pref.preferenceID " +
            "JOIN UserPreference up ON pref.preferenceID = up.preferenceID " +
            "WHERE up.userID = :userId")
    fun getProductsBasedOnUserPreferences(userId: String): List<Product>

    @Insert
    fun insert(product: Product)

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

    @Insert
    fun insert(preference: Preference)

    @Query("DELETE FROM Preference")
    fun deleteAll()
}


@Dao
interface UserPreferenceDao {
    // SELECT all of a user's preferences from UserPreference table
    @Query("SELECT * FROM UserPreference WHERE userID = :userId")
    fun getUserPreferences(userId: String): List<UserPreference>

    @Insert
    fun insert(userPreference: UserPreference)

    // INSERT preference into user's preferences
    @Insert
    fun insertUserPreference(userPreference: UserPreference)

    // DELETE preference from user's preferences
    @Query("DELETE FROM UserPreference WHERE userID = :userId AND preferenceID = :preferenceId")
    fun deleteUserPreference(userId: String, preferenceId: String)

    @Query("DELETE FROM UserPreference")
    fun deleteAll()
}


@Dao
interface ProductPreferenceDao {
    // SELECT all of the product preferences for a specific product
    @Query("SELECT * FROM ProductPreference WHERE productID = :productId")
    fun getProductPreferences(productId: String): List<ProductPreference>

    @Insert
    fun insert(productPreference: ProductPreference)

    @Query("DELETE FROM ProductPreference")
    fun deleteAll()
}

@Dao
interface UserCollectionDao {
    // SELECT all of the products in a user's collection
    @Query("SELECT * FROM UserCollection WHERE userID = :userId")
    fun getUserCollection(userId: String): List<UserCollection>

    @Insert
    fun insert(userCollection: UserCollection)

    // INSERT a product into user's bag
    @Insert
    fun insertProductIntoUserBag(userCollection: UserCollection)

    @Query("DELETE FROM UserCollection")
    fun deleteAll()
}
