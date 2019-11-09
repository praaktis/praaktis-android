package com.mobile.praaktishockey.ui.details.vm

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mobile.praaktishockey.base.BaseViewModel

class DetailsViewModel(app: Application) : BaseViewModel(app) {

    private val _title = MutableLiveData<String>()
    val title: LiveData<String> get() = _title

    fun changeTitle(title: String) {
        _title.value = title
    }
}