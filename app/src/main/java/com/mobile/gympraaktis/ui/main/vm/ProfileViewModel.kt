package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.AuthSeriviceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.domain.entities.CountryItemDTO
import com.mobile.gympraaktis.domain.entities.UserDTO
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ProfileViewModel(app: Application) : BaseViewModel(app) {

    private val loginStorage by lazy { SettingsStorage.instance }

    private val repo by lazy { AuthSeriviceRepository.AuthServiceRepositoryImpl.getInstance() }

    private val gson by lazy { Gson() }

    init {
        getProfile()
//        getProfileImage()
        getCountries()
    }

    val profileInfoEvent: LiveEvent<UserDTO> = LiveEvent()

    fun getProfile() {
        repo.getProfile()
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                profileInfoEvent.postValue(it)
            }, ::onError)
    }


    val profileImageEvent: LiveEvent<Bitmap> = LiveEvent()

    fun getProfileImage() {
        repo.getProfileImage()
            .subscribe({
                val inputStream = it.byteStream()
                profileImageEvent.postValue(BitmapFactory.decodeStream(inputStream))
            }, ::onError)
    }

    val updateProfileEvent: LiveEvent<String> = LiveEvent()

    fun updateProfile(user: UserDTO) {
        repo.updateProfile(user)
            .doOnSubscribe { showHideEvent.postValue(true) }
            .doAfterTerminate { showHideEvent.postValue(false) }
            .subscribe({
                if (!user.password.isNullOrBlank()) {
                    settingsStorage.password = user.password
                }

                val json = JSONObject(it.string())
                val message = json.getString("message")
                updateProfileEvent.postValue(message)
            }, ::onError)
    }

    fun getCountryObject(): CountryItemDTO? {
        if (profileInfoEvent.value != null) {
            val json = gson.toJson(profileInfoEvent.value?.country)
            return gson.fromJson(json, CountryItemDTO::class.java)
        }
        return null
    }

    override fun onError(throwable: Throwable) {
        if (throwable is UnknownHostException
            || throwable is SocketTimeoutException
        ) {
            if (loginStorage.getProfile() != null)
                profileInfoEvent.postValue(loginStorage.getProfile())
        }
        super.onError(throwable)
    }

}