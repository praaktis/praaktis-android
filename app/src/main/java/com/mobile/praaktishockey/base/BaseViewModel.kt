package com.mobile.praaktishockey.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.gson.JsonParseException
import com.mobile.praaktishockey.R
import com.mobile.praaktishockey.data.repository.CommonsServiceRepository
import com.mobile.praaktishockey.data.repository.UserServiceRepository
import com.mobile.praaktishockey.domain.common.AnyLV
import com.mobile.praaktishockey.domain.common.BoolLV
import com.mobile.praaktishockey.domain.common.LiveEvent
import com.mobile.praaktishockey.domain.common.pref.SettingsStorage
import com.mobile.praaktishockey.domain.entities.CountryItemDTO
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.json.JSONObject
import retrofit2.HttpException
import java.io.InterruptedIOException

open class BaseViewModel(app: Application) : AndroidViewModel(app) {

    val errorMessage = AnyLV()
    val showHideEvent = BoolLV()
    val logoutEvent = BoolLV()

    fun getLogin() : String = settingsStorage.login
    fun getPassword() : String = settingsStorage.password

    fun onLogoutSuccess() {
        UserServiceRepository.UserServiceRepositoryImpl.INSTANCE = null
        logoutEvent.postValue(false)
    }

    val commonsRepo by lazy { CommonsServiceRepository.CommonsServiceRepositoryImpl.getInstance(app) }
    val settingsStorage by lazy { SettingsStorage.instance }

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    protected fun addDisposable(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    private fun clearDisposables() {
        compositeDisposable.clear()
    }

    override fun onCleared() {
        clearDisposables()
    }

    fun onDestroy() {
        compositeDisposable.clear()
    }

    // analyzing error
    protected open fun onError(throwable: Throwable) {
        throwable.printStackTrace()
        if (throwable is HttpException) {
            val message: String = when (throwable.code()) {
                500 -> "Internal sever error!"
                401 -> {
                    logoutEvent.postValue(true)
                    val temp = throwable.response()?.errorBody()?.string()
                    if (temp != null) {
                        val messages: MutableList<String> = mutableListOf()
                        try {
                            val json = JSONObject(temp)

                            json.keys().forEach {
                                messages.add(json.getString(it))
                            }
                        } catch (ex: JsonParseException) {
                            temp.toString()
                        }
                        messages.joinToString()
                    }
                    temp.toString()
                }
                else -> {
                    val temp = throwable.response()?.errorBody()?.string()
                    if (temp != null)
                        try {
                            val json = JSONObject(temp)

                            val messages: MutableList<String> = mutableListOf()
                            json.keys().forEach {
                                messages.add(json.getString(it))
                            }
                            messages.joinToString()
                        } catch (ex: JsonParseException) {
                            temp.toString()
                        }
                    else
                        "Error"
//                    json.getString("error_message")
                }
            }
            errorMessage.postValue(message)
        } else if (throwable !is InterruptedIOException) {
            errorMessage.postValue(R.string.error)
        }
    }


    val countriesEvent: LiveEvent<List<CountryItemDTO>> = LiveEvent()

    fun getCountries() {
        commonsRepo.getCountries().subscribe({
            countriesEvent.postValue(it)
        }, ::onError)
    }

}