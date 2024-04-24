package com.example.cis436_project4;
import androidx.room.Entity
import androidx.room.PrimaryKey

// User Table
@Entity
data class User (
    @PrimaryKey val userID : String,
    val username : String,
    val email: String
)

// Product Table
@Entity
data class Product(
    @PrimaryKey val productID : String,
    val brand : String,
    val name : String,
    val price : String?,
    val imageLink : String?,
    val websiteLink : String?,
    val description : String?,
    val type: String?,
    val tags: String?
)

// Preference Table
@Entity
data class Preference(
    @PrimaryKey val preferenceID : Int,
    val type : String,
    val value : String
)

// User Preference Table
@Entity(primaryKeys = ["userID", "preferenceID"])
data class UserPreference(
    val userID: String,
    val preferenceID: Int
)

// Product Preference Table
@Entity(primaryKeys = ["productID", "preferenceID"])
data class ProductPreference (
    val productID: String,
    val preferenceID: Int
)

// User Collection Table
@Entity(primaryKeys = ["userID", "productID"])
data class UserCollection (
    val userID: String,
    val productID: String
)