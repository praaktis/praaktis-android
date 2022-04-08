package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import androidx.lifecycle.asLiveData
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase

class NewChallengeViewModel(app: Application) : BaseViewModel(app) {

    fun observePlayers() = PraaktisDatabase.getInstance(getApplication()).getDashboardDao().getPlayers().asLiveData()

}