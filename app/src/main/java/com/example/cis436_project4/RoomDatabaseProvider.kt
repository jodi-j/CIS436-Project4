package com.example.cis436_project4

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [User::class, Product::class, Preference::class, UserPreference::class, ProductPreference::class, UserCollection::class], version = 1, exportSchema = false)
abstract class RoomDatabaseProvider : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao
    abstract fun preferenceDao(): PreferenceDao
    abstract fun userPreferenceDao(): UserPreferenceDao
    abstract fun productPreferenceDao(): ProductPreferenceDao
    abstract fun userCollectionDao(): UserCollectionDao

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
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}