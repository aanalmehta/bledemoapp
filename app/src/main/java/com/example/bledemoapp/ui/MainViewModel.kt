package com.example.bledemoapp.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import com.example.bledemoapp.ble.BleManager
import com.example.bledemoapp.model.BaseScannedDevice
import com.example.bledemoapp.repositories.DeviceRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * MainViewModel class
 *
 * @author Aanal Shah
 */
class MainViewModel : ViewModel() {
    var bleDeviceList = MutableLiveData<java.util.ArrayList<BaseScannedDevice>>()
    var totalRecords = MutableLiveData<Int>()
    var uploadSuccess = MutableLiveData<Boolean>()
    lateinit var deviceList: ArrayList<BaseScannedDevice>

    private fun parseScannedResult(bleDeviceList: ArrayList<BaseScannedDevice>) {
        this.viewModelScope.launch {
            withContext(Dispatchers.Default) {
                DeviceRepo.insertAll(bleDeviceList)
                val count = DeviceRepo.getTotalDeviceData()
                totalRecords.postValue(count)
            }
        }
    }

    fun startScanning() {
        viewModelScope.launch {
            BleManager.startScanning().asFlow().collect {
                bleDeviceList.postValue(it)
                parseScannedResult(it)
                deviceList = it.clone() as ArrayList<BaseScannedDevice>
                saveDataToServer()
            }
        }
    }

    private suspend fun saveDataToServer() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                if (::deviceList.isInitialized && deviceList.isNotEmpty()) {
                    val response = DeviceRepo.saveDataToServer(deviceList[0])
                    if (response.data != null) {
                        deviceList.removeAt(0)
                        saveDataToServer()
                    } else {
                        uploadSuccess.postValue(false)
                    }
                } else {
                    uploadSuccess.postValue(true)
                }
            }
        }
    }
}
