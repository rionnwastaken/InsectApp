package com.example.randominsect.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.randominsect.data.dao.InsectDao
import com.example.randominsect.data.model.Insect

@Database(entities = [Insect::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    // Define abstract getters for all DAOs in your app
    abstract fun insectDao(): InsectDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return existing instance if available, otherwise build a new singleton instance
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "insect_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
