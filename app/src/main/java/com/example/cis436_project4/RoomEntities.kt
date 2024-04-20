package com.example.cis436_project4;
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User (
    @PrimaryKey val userID : String,
    val username : String,
    val email: String
)

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

@Entity
data class Preference(
    @PrimaryKey val preferenceID : String,
    val type : String,
    val value : String
)

@Entity(primaryKeys = ["userID", "preferenceID"])
data class UserPreference(
    val userID: String,
    val preferenceID: String
)

@Entity(primaryKeys = ["productID", "preferenceID"])
data class ProductPreference (
    val productID: String,
    val preferenceID: String
)

@Entity(primaryKeys = ["userID", "productID"])
data class UserCollection (
    val userID: String,
    val productID: String
)