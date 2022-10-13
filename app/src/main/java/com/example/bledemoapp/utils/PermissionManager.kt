package com.example.bledemoapp.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.bledemoapp.App
import com.eazypermissions.common.model.PermissionResult
import com.eazypermissions.dsl.PermissionManager
import com.example.bledemoapp.MainActivity
import com.example.bledemoapp.R
import com.example.bledemoapp.model.LiveDataResult

object PermissionManager {

    /**
     * Check if all required permissions are granted to connect to BLE device
     * @return true if all permissions are granted else false
     */
    fun isBlePermissionsGranted(): Boolean {
        if (!isLocationOrBLEPermissionGranted()) {
            return false
        } else if (!isBleEnabled()) {
            return false
        } else if (isLocationPermissionRequired() && !isLocationEnabled()) {
            return false
        }
        return true
    }

    /**
     * Check if Bluetooth is enabled or not
     * @return true if bluetooth is enabled else false
     */
    private fun isBleEnabled(): Boolean {
        return isBluetoothEnabled()
    }

    private fun isBluetoothEnabled(): Boolean {
        val bluetoothManager = App.instance?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled) {
            return false
        }
        return true
    }

    private fun isLocationOrBLEPermissionGranted(): Boolean {
        val requiredPermission = if (isLocationPermissionRequired()) {
            Manifest.permission.ACCESS_FINE_LOCATION
        } else {
            Manifest.permission.BLUETOOTH_CONNECT
        }
        val permissionStatus = App.instance?.checkCallingOrSelfPermission(requiredPermission)
        permissionStatus?.let {
            if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }

    private fun isLocationPermissionRequired(): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S
    }

    /**
     * Check if location service is enabled or not
     * @return true if location service is enabled else false
     */
    private fun isLocationEnabled(): Boolean {
        var locationEnabled = true
        try {
            locationEnabled = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val lm =
                    App.instance?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                lm.isLocationEnabled
            } else {
                val mode: Int = Settings.Secure.getInt(
                    App.instance?.contentResolver,
                    Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF
                )
                mode != Settings.Secure.LOCATION_MODE_OFF
            }
        } catch (exception: Throwable) {
            exception.printStackTrace()
        }
        return locationEnabled
    }

    /**
     * get All required permission to connect to BLE device
     * @return observable to notify observer about operation success or failure
     */
    fun getAllBlePermissions(activity: FragmentActivity?): LiveData<LiveDataResult<Boolean>> {
        val observer = MutableLiveData<LiveDataResult<Boolean>>()
        activity?.let {
            getLocationOrBLEPermission(activity, observer)
        }
        return observer
    }

    /**
     * get Location permission
     * @param activity activity refrence
     * @param observable to notify observer about operation success or failure
     */
    private fun getLocationOrBLEPermission(
        activity: FragmentActivity,
        observer: MutableLiveData<LiveDataResult<Boolean>>
    ) {
        val requiredPermission = if (isLocationPermissionRequired()) {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        }
        val rationalMessage = if (isLocationPermissionRequired()) {
            activity.getString(R.string.permission_location_require_msg, activity.getString(R.string.app_name))
        } else {
            activity.getString(R.string.permission_bluetooth_require_msg, activity.getString(R.string.app_name))
        }
        requestPermission(
            activity,
            *requiredPermission
        ) {
            when (this) {
                is PermissionResult.PermissionGranted -> {
                    if (!isBleEnabled()) {
                        askToTurnOnBluetooth(activity, observer)
                    } else {
                        askToTurnOnLocation(activity, observer)
                    }
                }
                is PermissionResult.PermissionDenied -> {
                    observer.postValue(LiveDataResult.Error(""))
                }
                is PermissionResult.ShowRational -> {
                    permissionShowRationalAlert(
                        rationalMessage,
                        activity,
                        observer
                    )
                }
                is PermissionResult.PermissionDeniedPermanently -> {
                    permissionDeniedForeverAlert(
                        rationalMessage,
                        activity
                    )
                    observer.postValue(LiveDataResult.Error(""))
                }
            }
        }
    }

    /**
     * Request runtime permission from user
     * @param activity activity refrence
     * @param permissions required permissions
     * @param resultBlock lambda [PermissionResult]
     */
    fun requestPermission(
        activity: Activity?,
        vararg permissions: String,
        resultBlock: PermissionResult.() -> Unit
    ) {
        activity?.let {
            kotlin.runCatching {
                if (it is MainActivity) {
                    PermissionManager.requestPermissions(it, *permissions) {
                        requestCode = 1
                        resultCallback = resultBlock
                    }
                }
            }
        }
    }

    /**
     * Redirect user to app settings to enable permission if user deny permission for ever
     * @param msg Message to display
     * @param activity activity refrence
     */
    private fun permissionDeniedForeverAlert(
        msg: String,
        activity: FragmentActivity
    ) {
        AlertView(activity).showAlert(
            msg,
            positiveButton = activity.getString(R.string.permission_dialog_goto_setting_btn),
            positiveButtonListener = {
                val intent = Intent(
                    Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + activity.getPackageName())
                )
                intent.addCategory(Intent.CATEGORY_DEFAULT)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                activity.startActivity(intent)
            },
            negativeButton = activity.getString(R.string.dialog_cancel_btn)
        )
    }

    /**
     * Redirect user to Location settings to enable GPS
     * @param activity activity refrence
     * @param observer to notify operation success or failure
     */
    private fun askToTurnOnLocation(
        activity: FragmentActivity,
        observer: MutableLiveData<LiveDataResult<Boolean>>
    ) {
        if (isLocationPermissionRequired() && !isLocationEnabled()) {
            AlertView(activity).showAlert(
                activity.getString(R.string.permission_dialog_turn_on_location_msg),
                positiveButton = activity.getString(R.string.permission_dialog_goto_setting_btn),
                positiveButtonListener = {
                    activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                },
                negativeButton = activity.getString(R.string.dialog_cancel_btn)
            )
            observer.postValue(LiveDataResult.Error(""))
        } else {
            // callback delayed because turning on Bluetooth takes at least 1 second
            Handler(Looper.getMainLooper()).postDelayed({ observer.postValue(LiveDataResult.Success(true)) }, 1000)
        }
    }

    /**
     * Ask user to turn on device Bluetooth. if user allow turn bluetooth programmatically
     * @param activity activity refrence
     * @param observer to notify operation success or failure
     */
    @SuppressLint("MissingPermission")
    private fun askToTurnOnBluetooth(
        activity: FragmentActivity,
        observer: MutableLiveData<LiveDataResult<Boolean>>
    ) {
        if (!isBleEnabled()) {
            AlertView(activity).showAlert(
                activity.getString(R.string.permission_dialog_turn_on_bluetooth_msg),
                positiveButton = activity.getString(R.string.dialog_turn_on_btn),
                positiveButtonListener = {
                    if (Build.VERSION.SDK_INT <= 32) {
                        BluetoothAdapter.getDefaultAdapter()?.enable()
                        askToTurnOnLocation(activity, observer)
                    } else {
                        kotlin.runCatching {
                            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                            activity.startActivity(enableBtIntent)
                        }
                        observer.postValue(LiveDataResult.Error(""))
                    }
                },
                negativeButton = activity.getString(R.string.dialog_cancel_btn)
            ) {
                observer.postValue(LiveDataResult.Error(""))
            }
        }
    }

    /**
     * Show permission rational message to user if require
     * @param msg Message to display
     * @param activity activity refrence
     * @param observer to notify operation success or failure
     */
    private fun permissionShowRationalAlert(
        msg: String,
        activity: FragmentActivity,
        observer: MutableLiveData<LiveDataResult<Boolean>>
    ) {
        AlertView(activity).showAlert(
            msg,
            positiveButton = activity.getString(R.string.permission_dialog_allow_btn),
            positiveButtonListener = {
                if (msg.equals(activity.getString(R.string.permission_location_require_msg, activity.getString(R.string.app_name)), true) ||
                    msg.equals(activity.getString(R.string.permission_bluetooth_require_msg, activity.getString(R.string.app_name)), true)
                ) {
                    getLocationOrBLEPermission(activity, observer)
                }
            },
            negativeButton = activity.getString(R.string.dialog_cancel_btn)
        ) {
            observer.postValue(LiveDataResult.Error(""))
        }
    }
}