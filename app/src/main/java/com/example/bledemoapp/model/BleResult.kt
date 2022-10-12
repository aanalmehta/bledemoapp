package com.example.bledemoapp.model

sealed class BleResult<T>(
    val data: T? = null,
    val message: String? = null,
    val errorCode: Int = 401
) {
    class Success<T>(data: T, message: String = "") : BleResult<T>(data, message)
    class Loading<T>(data: T? = null) : BleResult<T>(data)
    class Error<T>(message: String, data: T? = null, errorCode: Int = 401) : BleResult<T>(data, message, errorCode)
}