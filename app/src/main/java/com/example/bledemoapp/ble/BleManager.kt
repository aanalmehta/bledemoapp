package com.example.bledemoapp.ble

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bledemoapp.App
import com.example.bledemoapp.model.BaseScannedDevice
import com.example.bledemoapp.utils.Extensions.containsBleDevice
import com.example.bledemoapp.utils.Extensions.debugLog


object BleManager {
    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())
    private val SCAN_PERIOD: Long = 10000

    var scanner: BluetoothLeScanner
    var bluetoothManager =
        App.instance?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    var bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    var bleDeviceList = ArrayList<BaseScannedDevice>()

    init {
        scanner = bluetoothAdapter.bluetoothLeScanner
    }

    fun startScanning(): LiveData<ArrayList<BaseScannedDevice>> {
        val observer = MutableLiveData<ArrayList<BaseScannedDevice>>()
        if (!scanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                scanning = false
                scanner.stopScan(scanCallback)
                if (bleDeviceList.isNotEmpty()) {
                    observer.value = bleDeviceList
                }
            }, SCAN_PERIOD)
            scanning = true
            scanner.startScan(scanCallback)
        } else {
            scanning = false
            scanner.stopScan(scanCallback)
        }
        return observer
    }

    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            if (isBluetoothPermissionGranted()) {
                val device = BaseScannedDevice()
                device.rssi = result.rssi
                device.uid = result.device.address
                device.displayName = result.device.name
                device.mEventType = result.device.type
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    device.mPrimaryPhy = result.primaryPhy
                    device.mSecondaryPhy = result.secondaryPhy
                    device.mAdvertisingSid = result.advertisingSid
                    device.mTxPower = result.txPower
                    device.mPeriodicAdvertisingInterval = result.periodicAdvertisingInterval
                    device.isConnectable = result.isConnectable
                    device.dataStatus = result.dataStatus
                    device.isLegacy = result.isLegacy
                }
                device.mTxPowerLevel = result.scanRecord?.txPowerLevel
                device.mDeviceName = result.scanRecord?.deviceName
                device.advertisementBytes = result.scanRecord?.bytes.toString()
                device.manufacturerBytes = result.scanRecord?.manufacturerSpecificData.toString()
                device.serviceUUIDs = result.scanRecord?.serviceUuids.toString()
                if (bleDeviceList.isEmpty() || !bleDeviceList.containsBleDevice(device.uid)) {
                    ("onScanResult => device ${device.mDeviceName}, UUID ${device.uid}").debugLog()
                    bleDeviceList.add(device)
                }
            }
        }

        override fun onBatchScanResults(results: List<ScanResult?>?) {

        }

        override fun onScanFailed(errorCode: Int) {
            ("onScanFailed ErrorCode=> $errorCode").debugLog()
        }
    }

    fun isBluetoothPermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ActivityCompat.checkSelfPermission(
                App.instance!!,
                Manifest.permission.BLUETOOTH_CONNECT
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            return true
        }
    }
}