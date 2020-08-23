package com.mobile.gympraaktis.data.repository

import android.app.Application
import com.mobile.gympraaktis.data.api.CommonsService
import com.mobile.gympraaktis.domain.common.Constants.createService
import com.mobile.gympraaktis.domain.entities.CountryItemDTO
import io.reactivex.Single

interface CommonsServiceRepository {

    fun getCountries(): Single<List<CountryItemDTO>>

    fun getServerName(): Single<Map<String, String>>

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

        override fun getServerName(): Single<Map<String, String>> {
            return commonsService.getServerName()
        }
    }

}