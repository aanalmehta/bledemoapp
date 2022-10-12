package com.example.bledemoapp.model

open class BaseScannedDevice(
    var uid: String = "",
    var displayName: String? = "",
    var rssi: Int = 0,
    var mEventType: Int? = 0,
    var mPrimaryPhy: Int? = 0,
    var mSecondaryPhy: Int? = 0,
    var mAdvertisingSid: Int? = 0,
    var mTxPower: Int? = 0,
    var mPeriodicAdvertisingInterval: Int? = 0,
    var dataStatus: Int? = 0,
    var mTxPowerLevel: Int? = 0,
    var isConnectable: Boolean? = false,
    var isLegacy: Boolean? = false,
    var mDeviceName: String? = "",
    var advertisementBytes: String? = "",
    var manufacturerBytes: String? = "",
    var serviceUUIDs: String? = "",
)
