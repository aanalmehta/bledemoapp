package com.example.bledemoapp.database.tables

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

/**
 * Device table
 *
 * @param id device unique id returned from server
 * @param uid device MAC id for BLE device and Device model id for Non BLE device
 * @param deviceModelId device model id. Any of [SupportedDevices.DEVICE_MODEL]
 * @param displayName device display name.
 * @param hasBluetooth device contain bluetooth or not.
 * @param firmwareVersion device current firmware version.
 * @param isDefault device is default or not.
 */
@kotlinx.parcelize.Parcelize
@Entity(tableName = "Device")
data class DeviceEntity(
    @ColumnInfo(name = "uid") var uid: String = "",
    @ColumnInfo(name = "display_name") var displayName: String,
    @ColumnInfo(name = "rssi") var rssi: Int = 0,
    @ColumnInfo(name = "event_type") var mEventType: Int,
    @ColumnInfo(name = "primary_phy") var mPrimaryPhy: Int,
    @ColumnInfo(name = "secondary_phy") var mSecondaryPhy: Int = 0,
    @ColumnInfo(name = "advertising_sid") var mAdvertisingSid: Int,
    @ColumnInfo(name = "tx_power") var mTxPower: Int,
    @ColumnInfo(name = "periodic_advertising_interval") var mPeriodicAdvertisingInterval: Int = 0,
    @ColumnInfo(name = "data_status") var dataStatus: Int,
    @ColumnInfo(name = "tx_power_level") var mTxPowerLevel: Int = 0,
    @ColumnInfo(name = "is_connectable") var isConnectable: Int = 0,
    @ColumnInfo(name = "is_legacy") var isLegacy: Int = 0,
    @ColumnInfo(name = "device_name") var mDeviceName: String = "",
    @ColumnInfo(name = "advertisement_bytes") var advertisementBytes: String = "",
    @ColumnInfo(name = "manufacturer_bytes") var manufacturerBytes: String = "",
    @ColumnInfo(name = "service_uuids") var serviceUUIDs: String = "",
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L
) : Parcelable
