package com.example.bledemoapp.repositories

import com.example.bledemoapp.api.model.retrofit.RetrofitManager
import com.example.bledemoapp.api.model.retrofit.model.ApiResponse
import com.example.bledemoapp.api.model.retrofit.model.device.SaveDeviceResponse
import com.example.bledemoapp.database.AppDatabase
import com.example.bledemoapp.database.dao.DeviceDao
import com.example.bledemoapp.database.tables.DeviceEntity
import com.example.bledemoapp.model.BaseScannedDevice

/**
 * Repository class for Devices
 *
 * @author Aanal Shah
 */
object DeviceRepo {

    private val deviceDao: DeviceDao = AppDatabase.getDatabase().deviceDao()

    /**
     * Insert all device in local DB
     *
     * @param scannedDeviceList devices to insert
     */
    suspend fun insertAll(scannedDeviceList: ArrayList<BaseScannedDevice>) {
        if (scannedDeviceList.isNotEmpty()) {
            // Insert data to local database
            val devices = ArrayList<DeviceEntity>()
            scannedDeviceList.forEach {
                val device = DeviceEntity(
                    uid = it.uid,
                    displayName = it.displayName?: "",
                    rssi = it.rssi,
                    mEventType = it.mEventType ?: 0,
                    mPrimaryPhy = it.mPrimaryPhy ?: 0,
                    mSecondaryPhy = it.mSecondaryPhy ?: 0,
                    mAdvertisingSid = it.mAdvertisingSid ?: 0,
                    mTxPower = it.mTxPower ?: 0,
                    mPeriodicAdvertisingInterval = it.mPeriodicAdvertisingInterval ?: 0,
                    dataStatus = it.dataStatus ?: 0,
                    mTxPowerLevel = it.mTxPowerLevel ?: 0,
                    isConnectable = if (it.isConnectable == true) 1 else 0,
                    isLegacy = if (it.isLegacy == true) 1 else 0,
                    mDeviceName = it.mDeviceName ?: "",
                    advertisementBytes = it.advertisementBytes ?: "",
                    manufacturerBytes = it.manufacturerBytes ?: "",
                    serviceUUIDs = it.serviceUUIDs ?: ""
                )
                devices.add(device)
            }
            deviceDao.insertScannedDevice(*devices.toTypedArray())
        }
    }

    suspend fun getTotalDeviceData(): Int {
        return deviceDao.getDeviceDataCount()
    }

    suspend fun saveDataToServer(baseScannedDevices: BaseScannedDevice): ApiResponse<SaveDeviceResponse.Response> {
        return RetrofitManager.executeRequest(
            RetrofitManager.retrofitService.saveDevice(baseScannedDevices)
        )
    }
}
