package com.mobile.praaktishockey.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import com.mobile.praaktishockey.domain.entities.CountryItemDTO
import io.reactivex.Single

@Dao
interface CountriesDao {

    @Insert(onConflict = REPLACE)
    fun insertCountries(countryList: List<CountryItemDTO>)

    @Query("SELECT * FROM countries")
    fun getCountries(): Single<List<CountryItemDTO>>
}