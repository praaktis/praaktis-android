package com.mobile.gympraaktis.domain

import android.content.Context
import com.google.gson.Gson
import com.mobile.gympraaktis.R
import com.mobile.gympraaktis.domain.model.RoutinesList

fun Context.getCurrentRoutine(): RoutinesList.Routine? {
    val routines = Gson().fromJson(
        resources.openRawResource(R.raw.routines).bufferedReader(),
        RoutinesList::class.java
    )
    val routine = routines.find {
        it.id == Constants.ROUTINE_ID
    }
    return routine
}