package com.example.bledemoapp.api.model.retrofit

import com.example.bledemoapp.BuildConfig
import com.example.bledemoapp.R
import com.example.bledemoapp.api.model.retrofit.model.ApiResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.internal.concurrent.Task
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

/**
 * object class contains methods to call REST API endpoints.
 *
 * App uses [Retrofit] as dependency to communicate with REST endpoints
 * @author Aanal Shah
 */
object RetrofitManager {
    // retrofit object for normal api call
    private lateinit var baseRetrofit: Retrofit
    const val CONNECT_TIMEOUT = 20 * 1000L
    const val WRITE_TIMEOUT = 10 * 1000L
    const val READ_TIMEOUT = 30 * 1000L

    // stores current access token of authenticated user
    private var idToken = ""

    // stores x-API key require for Some Endpoints like feature flag API and TTN Apis.
    private var xApiKey = ""

    const val WS_HEADER_AUTHORIZATION_BEARER = "Bearer"

    // initialize retrofit builder
    fun initBaseRetrofit() {
        baseRetrofit = getRetrofitBuilder()
    }

    /**
     * Prepare builder base on url
     *
     * @return Builder.
     */
    private fun getBuilder(): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .addInterceptor(getLoggingInterceptor())
            .addInterceptor(addHeaderInterceptor())
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
    }

    /**
     * Prepare retrofit builder base on url
     *
     * @return Retrofit.
     */
    private fun getRetrofitBuilder(): Retrofit {
        return Retrofit.Builder()
            .client(getBuilder().build())
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build()
    }

    /**
     * Retrofit log interceptor to view api logs.
     *
     * @return Interceptor.
     */
    private fun getLoggingInterceptor(): Interceptor {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    /**
     * Prepare retrofit interceptor with token and app version for normal api call
     *
     * @return Interceptor for normal api call.
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                .addHeader(
                    "Authorization",
                    "$WS_HEADER_AUTHORIZATION_BEARER $idToken"
                )
                .addHeader(
                    "X-API-KEY",
                    xApiKey
                ).addHeader(
                    "app-version",
                    BuildConfig.VERSION_NAME
                )
                .method(originalRequest.method, originalRequest.body)
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    private fun <T> getService(service: Class<T>): T {
        return baseRetrofit.create(service)
    }

    /**
     * retrofit service prepare for normal url
     */
    val retrofitService: ApiService by lazy {
        getService(ApiService::class.java)
    }

    /**
     * Retrofit response manage with different callback
     *
     * @param call retrofit service call.
     * @param passTokenInHeader manage token for futureflag.
     * @param apiKey api key if required.
     * @return ApiResponse api response.
     */
    suspend fun <T> executeRequest(
        call: Call<T>,
        apiKey: String = ""
    ): ApiResponse<T> {
        try {
            xApiKey = apiKey
            val response = call.execute()
            return when (response.code()) {
                in 200..300 -> {
                    if (response.isSuccessful && response.body() != null) {
                        ApiResponse.Success(response.body()!!)
                    } else {
                        getErrorMsgFromErrorBlock<T>(
                            response,
                            R.string.something_went_wrong
                        )
                    }
                }
                else -> {
                    getErrorMsgFromErrorBlock<T>(
                        response,
                        R.string.something_went_wrong
                    )
                }
            }
        } catch (error: Throwable) {
            when (error) {
                is ConnectException, is UnknownHostException -> {
                    return ApiResponse.Error(R.string.error_message_network)
                }
            }
        }
        return ApiResponse.Error(R.string.something_went_wrong)
    }

    /**
     * Retrofit error callback
     *
     * @param response api response.
     * @param errorRes default error message.
     * @return T api error response.
     */
    private fun <T> getErrorMsgFromErrorBlock(
        response: Response<T>,
        errorRes: Int = R.string.something_went_wrong
    ): ApiResponse.Error<T> {
        try {
            val errorData = response.errorBody()?.string()!!
            var errorText = ""
            if (errorData.isNotEmpty()) {
                val jsonObject = JSONObject(errorData)
                errorText = jsonObject.optString("message")
            }
            return ApiResponse.Error(
                errorRes,
                errorMsgFromAPI = errorText,
                code = response.code()
            )
        } catch (e: Exception) {
            return ApiResponse.Error(
                errorRes,
                errorMsgFromAPI = ""
            )
        }
    }
}
