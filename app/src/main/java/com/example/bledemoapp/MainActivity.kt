package com.example.bledemoapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.bledemoapp.databinding.ActivityMainBinding
import com.example.bledemoapp.model.LiveDataResult
import com.example.bledemoapp.ui.MainViewModel
import com.example.bledemoapp.ui.adapter.ScannedDeviceAdapter
import com.example.bledemoapp.utils.Extensions.debugLog
import com.example.bledemoapp.utils.Extensions.showToast
import com.example.bledemoapp.utils.PermissionManager
import com.example.bledemoapp.utils.ProgressUtils

class MainActivity : AppCompatActivity() {
    private lateinit var mainViewModel: MainViewModel
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ScannedDeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel =
            ViewModelProvider(this)[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initAdapter()

        if (!PermissionManager.isBlePermissionsGranted()) {
            PermissionManager.getAllBlePermissions(this).observe(
                this@MainActivity
            ) {
                when (it) {
                    is LiveDataResult.Success -> {
                        ProgressUtils.showProgressDialog(
                            this@MainActivity,
                            getString(R.string.dialog_scan_device)
                        )
                        mainViewModel.startScanning()
                    }
                    else -> {
                        "Something went wrong".debugLog()
                    }
                }
            }
        } else {
            ProgressUtils.showProgressDialog(
                this@MainActivity,
                getString(R.string.dialog_scan_device)
            )
            mainViewModel.startScanning()
        }

        mainViewModel.bleDeviceList.observe(this) {
            adapter.updateDeviceList(it)
        }

        mainViewModel.totalRecords.observe(this) {
            "Total scanned records in db: $it".showToast(this)
        }

        mainViewModel.uploadSuccess.observe(this) {
            if (it) {
                "Server Upload: SUCCESS".showToast(this)
            } else {
                "Something went wrong".showToast(this)
            }
            ProgressUtils.hideProgressDialog()
        }
    }

    private fun initAdapter() {
        adapter = ScannedDeviceAdapter(this)
        binding.rvScannedDevices.layoutManager = LinearLayoutManager(this)
        binding.rvScannedDevices.adapter = adapter
    }
}