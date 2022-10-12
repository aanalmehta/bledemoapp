package com.example.bledemoapp.api.model

import com.google.gson.annotations.SerializedName

/**
 * Model class to Hold data of Success response
 *
 * @author Aanal Shah
 */
data class BaseSuccessModel(
    @field:SerializedName("code")
    val code: Int? = null,
    @field:SerializedName("message") val message: String = ""
)
