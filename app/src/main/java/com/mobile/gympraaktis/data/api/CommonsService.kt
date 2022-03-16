package com.mobile.gympraaktis.data.api

import com.mobile.gympraaktis.domain.entities.CountryItemDTO
import io.reactivex.Single
import retrofit2.http.GET

interface CommonsService {

    @GET("getCountries/")
    fun getCountries(): Single<List<CountryItemDTO>>

}