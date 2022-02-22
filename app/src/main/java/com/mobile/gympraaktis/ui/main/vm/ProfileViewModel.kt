package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.mobile.gympraaktis.PraaktisApp
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.repository.AuthSeriviceRepository
import com.mobile.gympraaktis.domain.common.LiveEvent
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import com.mobile.gympraaktis.domain.entities.CountryItemDTO
import com.mobile.gympraaktis.domain.entities.UserDTO
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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

    fun updateProfile(user: UserDTO, userImageUri: Uri?) {
        viewModelScope.launch {
            val file = viewModelScope.runCatching {
                var tempFile: File? = null
                if (userImageUri != null) {
                    tempFile = File.createTempFile(
                        "TempFile",
                        ".jpg",
                        getApplication<PraaktisApp>().externalCacheDir
                    )
                    val inputStream: InputStream? =
                        getApplication<PraaktisApp>().contentResolver.openInputStream(userImageUri)
                    if (inputStream != null) {
                        FileOutputStream(tempFile, false).use { outputStream ->
                            var read: Int
                            val bytes = ByteArray(DEFAULT_BUFFER_SIZE)
                            while (inputStream.read(bytes).also { read = it } != -1) {
                                outputStream.write(bytes, 0, read)
                            }
                            outputStream.close()
                        }
                        inputStream.close()
                    }
                }
                tempFile
            }

            repo.updateProfile(user, file.getOrNull())
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