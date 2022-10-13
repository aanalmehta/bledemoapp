package com.example.bledemoapp.ble

import android.Manifest
import android.annotation.SuppressLint
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
import androidx.core.util.forEach
import androidx.core.util.isNotEmpty
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bledemoapp.App
import com.example.bledemoapp.model.BaseScannedDevice
import com.example.bledemoapp.utils.Extensions.containsBleDevice
import com.example.bledemoapp.utils.Extensions.debugLog
import java.util.Arrays


object BleManager {
    private var scanning = false
    private val handler = Handler(Looper.getMainLooper())
    // Scan for 10 seconds
    private const val SCAN_PERIOD: Long = 10000

    private var scanner: BluetoothLeScanner
    private var bluetoothManager =
        App.instance?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    private var bluetoothAdapter: BluetoothAdapter = bluetoothManager.adapter
    var bleDeviceList = ArrayList<BaseScannedDevice>()

    init {
        scanner = bluetoothAdapter.bluetoothLeScanner
    }

    @SuppressLint("MissingPermission")
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
        @SuppressLint("MissingPermission")
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
                device.advertisementBytes = Arrays.toString(result.scanRecord?.bytes)
                val manufactureList = ArrayList<String>()
                if (result.scanRecord?.manufacturerSpecificData?.isNotEmpty() == true) {
                    result.scanRecord?.manufacturerSpecificData?.forEach { _, value ->
                        manufactureList.add(Arrays.toString(value))
                    }
                }
                device.manufacturerBytes = manufactureList.toString()
                val list = ArrayList<String>()
                if (result.scanRecord?.serviceUuids?.isNotEmpty() == true) {
                    result.scanRecord?.serviceUuids?.forEach {
                        list.add(it.uuid.toString())
                    }
                }
                device.serviceUUIDs = list.toString()
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