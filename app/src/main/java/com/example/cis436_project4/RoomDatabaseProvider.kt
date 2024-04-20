package com.example.cis436_project4

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Product::class, Preference::class, UserPreference::class, ProductPreference::class, UserCollection::class], version = 2, exportSchema = false)
abstract class RoomDatabaseProvider : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun preferenceDao(): PreferenceDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun productPreferenceDao(): ProductPreferenceDao
    abstract fun userCollectionDao(): UserCollectionDao

    // method to delete all tables
    override fun clearAllTables() {
        // Execute a transaction to delete all data from all tables
        runInTransaction {
            // Call the DAO methods to delete all data from each table
            productDao().deleteAll()
            // Add more DAO methods to delete data from other tables if needed
            // e.g., userDao().deleteAll()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: RoomDatabaseProvider? = null

        fun getInstance(context: Context): RoomDatabaseProvider {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        RoomDatabaseProvider::class.java,
                        "my_app_database"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}