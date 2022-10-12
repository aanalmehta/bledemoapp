package com.example.bledemoapp

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.bledemoapp.api.model.retrofit.RetrofitManager
import com.example.bledemoapp.database.AppDatabase
import kotlinx.coroutines.CoroutineExceptionHandler

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        dbInstance = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            getString(R.string.app_name)
        ).build()

        RetrofitManager.initBaseRetrofit()
    }

    companion object {
        var instance: App? = null
        var dbInstance: AppDatabase? = null

        /**
         * returns application context
         */
        fun applicationContext(): Context {
            return instance!!.applicationContext
        }

        val coroutineExceptionHandler = CoroutineExceptionHandler { _, e -> }
    }
}