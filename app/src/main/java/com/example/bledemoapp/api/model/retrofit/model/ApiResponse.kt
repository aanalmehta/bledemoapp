package com.example.bledemoapp.api.model.retrofit.model

import com.example.bledemoapp.App
import com.example.bledemoapp.R
import com.example.bledemoapp.api.model.retrofit.model.ApiResponse.*

/**
 * Holds ApiResponse data. class has 3 possible types [Success], [Loading] and [Error]
 *
 * @property data Response data.
 * @property message Error/Success message if any
 * @property apiErrorCode Error code if any.
 */
sealed class ApiResponse<T>(
    val data: T? = null,
    val message: String = App.applicationContext().getString(R.string.something_went_wrong),
    val apiErrorCode: Int = 401
) {
    class Success<T>(data: T, message: Int = R.string.app_name) :
        ApiResponse<T>(data, App.applicationContext().getString(message))

    class Loading<T>(data: T? = null) : ApiResponse<T>(data)

    class Error<T>(
        message: Int = R.string.something_went_wrong,
        data: T? = null,
        errorMsgFromAPI: String = "",
        code: Int = 401
    ) :
        ApiResponse<T>(
            data,
            if (errorMsgFromAPI.isEmpty()) App.applicationContext()
                .getString(message) else errorMsgFromAPI,
            apiErrorCode = code
        )
}
