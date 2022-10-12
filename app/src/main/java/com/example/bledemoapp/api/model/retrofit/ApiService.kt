package com.example.bledemoapp.api.model.retrofit

import com.example.bledemoapp.model.BaseScannedDevice
import com.example.bledemoapp.api.model.retrofit.model.device.SaveDeviceResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

// all api details
interface ApiService {

    /**
     * Save user device selected device.
     *
     * @param deviceToSave device details.
     * @return SaveDeviceResponse device details.
     */
    @POST("device")
    fun saveDevice(@Body deviceToSave: BaseScannedDevice): Call<SaveDeviceResponse.Response>
}
