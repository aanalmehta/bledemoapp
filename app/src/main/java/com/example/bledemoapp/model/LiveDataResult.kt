package com.example.bledemoapp.model

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

/**
 * Model class to use with [MutableLiveData]
 * this class supports 3 states LOADING,SUCCESS and ERROR and also can hold data,message string and errorcode
 *
 * App mostly uses this class in conjunction with [LiveData] and [MutableLiveData] to observe observables
 * @author Aanal Shah
 */
sealed class LiveDataResult<T>(
    val data: T? = null,
    val message: String? = null,
    val errorCode: Int = 401
) {
    class Success<T>(data: T, message: String = "") : LiveDataResult<T>(data, message)
    class Loading<T>(data: T? = null) : LiveDataResult<T>(data)
    class Error<T>(message: String, data: T? = null, errorCode: Int = 401) : LiveDataResult<T>(data, message, errorCode)
}
