package com.example.bledemoapp.api.model.retrofit.model.device

import com.google.gson.annotations.SerializedName

class SaveDeviceResponse {

    /**
     * Response data class contain
     *
     * @property device all device details.
     */
    data class Response(
        @SerializedName("msg")
        val msg: String,

        @SerializedName("uid")
        val uid: String,
    )
}
