package com.mobile.gympraaktis.data.api

import com.mobile.gympraaktis.domain.entities.CreateUserDTO
import com.mobile.gympraaktis.domain.entities.LoginDTO
import com.mobile.gympraaktis.domain.entities.RegisterDeviceDTO
import com.mobile.gympraaktis.domain.entities.UserDTO
import io.reactivex.Single
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface AuthService {

    @POST("user/api-token-auth/")
    fun login(
        @Body loginDTO: LoginDTO
    ): Single<ResponseBody>

    @POST("user/createUser/")
    fun createUser(
        @Body createUserDTO: CreateUserDTO
    ): Single<ResponseBody>

    @POST("user/updateProfile/")
    fun updateProfile(
        @Body user: UserDTO
    ): Single<ResponseBody>

    @Multipart
    @POST("user/updateProfile/")
    fun updateProfile(
        @Part user: Array<MultipartBody.Part>
    ): Single<ResponseBody>

    @GET("user/getProfile/")
    fun getProfile(): Single<UserDTO>

    @GET("user/getProfileImage/")
    @Streaming
    fun getProfileImage(): Single<ResponseBody>

    @POST("user/acceptTerms/")
    fun acceptTerms(): Single<ResponseBody>

    @POST("user/forgotPassword/")
    fun forgotPassword(@Body forgotPasswordMap: HashMap<String, String>): Single<ResponseBody>

    @GET("getTerms")
    fun getAcceptTerms(): Single<ResponseBody>

    @POST("user/registerDevice/")
    fun registerDevice(
        @Body registerDeviceDTO: RegisterDeviceDTO
    ): Single<ResponseBody>

    @POST("user/resendActivation/")
    fun resendActivation(): Single<ResponseBody>

}