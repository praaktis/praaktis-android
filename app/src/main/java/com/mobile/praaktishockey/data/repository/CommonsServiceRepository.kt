package com.mobile.praaktishockey.data.repository

import android.app.Application
import com.mobile.praaktishockey.data.api.CommonsService
import com.mobile.praaktishockey.domain.common.Constants.createService
import com.mobile.praaktishockey.domain.entities.CountryItemDTO
import io.reactivex.Single

interface CommonsServiceRepository {

    fun getCountries(): Single<List<CountryItemDTO>>

    class CommonsServiceRepositoryImpl(val app: Application) : CommonsServiceRepository {

        companion object {
            var INSTANCE: CommonsServiceRepository? = null
            fun getInstance(app: Application): CommonsServiceRepository {
                if (INSTANCE == null) INSTANCE =
                    CommonsServiceRepositoryImpl(app)
                return INSTANCE!!
            }
        }

        var commonsService: CommonsService = createService()
//        val database = PraaktisDatabase.getDatabase(app)

        override fun getCountries(): Single<List<CountryItemDTO>> {
            return commonsService.getCountries()
        }
    }

}