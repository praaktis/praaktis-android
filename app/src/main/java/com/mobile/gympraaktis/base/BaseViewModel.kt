package com.mobile.gympraaktis.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.mobile.gympraaktis.domain.common.AnyLV
import com.mobile.gympraaktis.domain.common.BoolLV

open class BaseViewModel(app: Application) : AndroidViewModel(app) {

    val errorMessage = AnyLV()
    val showHideEvent = BoolLV()
    val logoutEvent = BoolLV()

}