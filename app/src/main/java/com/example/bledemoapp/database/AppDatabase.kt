package com.example.bledemoapp.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.bledemoapp.App
import com.example.bledemoapp.database.dao.DeviceDao
import com.example.bledemoapp.database.tables.DeviceEntity

/**
 * [Database] RoomDatabase to store the data locally
 * @author Aanal Shah
 */
@Database(entities = [DeviceEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(): AppDatabase {
            if (INSTANCE != null) {
                return INSTANCE as AppDatabase
            }
            synchronized(this) {
                val roomBuilder = Room.databaseBuilder(
                    App.applicationContext(),
                    AppDatabase::class.java,
                    "app_db"
                )
                val instance = roomBuilder.build()
                INSTANCE = instance
                return instance
            }
        }
    }
}