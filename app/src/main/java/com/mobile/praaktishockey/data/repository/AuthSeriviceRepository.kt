package com.mobile.praaktishockey.data.repository

import com.mobile.praaktishockey.data.api.AuthService
import com.mobile.praaktishockey.domain.common.ASyncTransformer
import com.mobile.praaktishockey.domain.common.Constants.createService
import com.mobile.praaktishockey.domain.common.ImageUtils
import com.mobile.praaktishockey.domain.entities.CreateUserDTO
import com.mobile.praaktishockey.domain.entities.LoginDTO
import com.mobile.praaktishockey.domain.entities.RegisterDeviceDTO
import com.mobile.praaktishockey.domain.entities.UserDTO
import io.reactivex.Single
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.ResponseBody
import java.io.File

interface AuthSeriviceRepository {

    fun login(userName: String, password: String): Single<ResponseBody>
    fun createUser(email: String, password: String): Single<ResponseBody>
    fun updateProfile(userDTO: UserDTO): Single<ResponseBody>
    fun getProfile(): Single<UserDTO>
    fun getProfileImage(): Single<ResponseBody>
    fun acceptTerms(): Single<ResponseBody>
    fun getAcceptTerms(): Single<ResponseBody>
    fun forgotPassword(email: String): Single<ResponseBody>
    fun registerDevice(token: String): Single<ResponseBody>
    fun resendActivation(): Single<ResponseBody>

    class AuthServiceRepositoryImpl : AuthSeriviceRepository {

        companion object {
            var INSTANCE: AuthSeriviceRepository? = null
            fun getInstance(): AuthSeriviceRepository {
                if (INSTANCE == null) INSTANCE =
                    AuthServiceRepositoryImpl()
                return INSTANCE!!
            }
        }

        var authService: AuthService = createService()

        //todo
        override fun login(userName: String, password: String): Single<ResponseBody> {
            val request = LoginDTO(userName, password)
            return authService.login(request).compose(ASyncTransformer<ResponseBody>())
        }

        override fun createUser(email: String, password: String): Single<ResponseBody> {
            val request = CreateUserDTO(email, password)
            return authService.createUser(request).compose(ASyncTransformer<ResponseBody>())
        }


        override fun updateProfile(userDTO: UserDTO): Single<ResponseBody> {
            val user: MutableList<MultipartBody.Part> = mutableListOf()
            userDTO.profileImage?.let {
                val file = ImageUtils.convertToBitmap2(File(it), 300, 300)
                val imagePart = MultipartBody.Part.createFormData(
                    "profileImage", file.name, file
                        .asRequestBody("multipart/form-data".toMediaTypeOrNull())
                )
                user.add(imagePart)
            }

            userDTO.firstName?.let {
                user.add(
                    MultipartBody.Part.createFormData(
                        "first_name",
                        userDTO.firstName
                    )
                )
            }
            userDTO.lastName?.let {
                user.add(
                    MultipartBody.Part.createFormData(
                        "last_name",
                        userDTO.lastName
                    )
                )
            }
            userDTO.ability?.let {
                user.add(
                    MultipartBody.Part.createFormData(
                        "ability",
                        userDTO.ability.name
                    )
                )
            }
            userDTO.dateOfBirth?.let {
                user.add(
                    MultipartBody.Part.createFormData(
                        "date_of_birth",
                        userDTO.dateOfBirth
                    )
                )
            }
            userDTO.gender?.let {
                user.add(
                    MultipartBody.Part.createFormData(
                        "gender",
                        userDTO.gender.name
                    )
                )
            }
            userDTO.nickname?.let {
                user.add(
                    MultipartBody.Part.createFormData(
                        "nickname",
                        userDTO.nickname
                    )
                )
            }
            userDTO.country?.let {
                user.add(
                    MultipartBody.Part.createFormData(
                        "country",
                        userDTO.country.toString()
                    )
                )
            }
            userDTO.password?.let {
                user.add(
                    MultipartBody.Part.createFormData(
                        "password",
                        userDTO.password
                    )
                )
            }
            userDTO.language?.let {
                user.add(
                    MultipartBody.Part.createFormData(
                        "language",
                        userDTO.language.toString()
                    )
                )
            }

            return authService.updateProfile(user.toTypedArray())
        }

        override fun getProfile(): Single<UserDTO> {
            authService = createService()
            return authService.getProfile().compose(ASyncTransformer<UserDTO>())
        }

        override fun getProfileImage(): Single<ResponseBody> {
            return authService.getProfileImage().compose(ASyncTransformer<ResponseBody>())
        }

        override fun acceptTerms(): Single<ResponseBody> {
            return authService.acceptTerms().compose(ASyncTransformer<ResponseBody>())
        }

        override fun forgotPassword(email: String): Single<ResponseBody> {
            return authService.forgotPassword(hashMapOf(Pair("email", email)))
        }

        override fun getAcceptTerms(): Single<ResponseBody> {
            return authService.getAcceptTerms().compose(ASyncTransformer<ResponseBody>())
        }

        override fun registerDevice(token: String): Single<ResponseBody> {
            val request = RegisterDeviceDTO(token)
            return authService.registerDevice(request).compose(ASyncTransformer<ResponseBody>())
        }

        override fun resendActivation(): Single<ResponseBody> {
            return authService.resendActivation().compose(ASyncTransformer<ResponseBody>())
        }
    }
}