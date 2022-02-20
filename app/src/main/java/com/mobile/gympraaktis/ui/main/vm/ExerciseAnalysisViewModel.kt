package com.mobile.gympraaktis.ui.main.vm

import android.app.Application
import androidx.lifecycle.asLiveData
import com.mobile.gympraaktis.base.BaseViewModel
import com.mobile.gympraaktis.data.db.PraaktisDatabase

class ExerciseAnalysisViewModel(app: Application) : BaseViewModel(app) {

    fun observeDashboard() =
        PraaktisDatabase.getInstance(getApplication()).getDashboardDao().getDashboardData()
            .asLiveData()
}