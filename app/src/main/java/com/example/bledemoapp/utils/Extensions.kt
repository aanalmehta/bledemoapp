package com.example.bledemoapp.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.bledemoapp.MainActivity
import com.example.bledemoapp.model.BaseScannedDevice

object Extensions {

    fun ArrayList<BaseScannedDevice>.containsBleDevice(name: String?): Boolean {
        return find {
            it.uid == name
        } != null
    }

    fun String.debugLog(tag: String = "avt") {
        Log.d(tag, this)
    }

    fun String.showToast(context: Context) {
        Toast.makeText(context, this,
            Toast.LENGTH_LONG).show()
    }
}