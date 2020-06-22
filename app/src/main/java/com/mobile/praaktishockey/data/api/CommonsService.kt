package com.mobile.praaktishockey.data.api

import com.mobile.praaktishockey.domain.entities.CountryItemDTO
import io.reactivex.Single
import retrofit2.http.GET

interface CommonsService {

    @GET("getCountries/")
    fun getCountries(): Single<List<CountryItemDTO>>

    @GET("serverName/")
    fun getServerName(): Single<Map<String, String>>

}