package com.mobile.gympraaktis.domain.common

import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.google.gson.Gson
import com.mobile.gympraaktis.PraaktisApp
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Constants {
    const val DATABASE_HOCKEY_VERSION: Int = 15
    const val TIMELINE = "TIMELINE"
    const val TIMELINE_CHALLENGE_ITEM = "TIMELINE_CHALLENGE_ITEM"

    var retrofit: Retrofit? = null

    inline fun <reified S> createService(): S {
        return getRetrofit("https://api.praaktis.com/api/").create(S::class.java)
    }

    fun getRetrofit(endpoint: String): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(endpoint)
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
                .client(buildClient())
                .build()
        }
        return retrofit!!
    }

    private fun buildClient(): OkHttpClient {
        val loginSettings = SettingsStorage.instance
        val builder = OkHttpClient.Builder()
        builder.addInterceptor(ChuckerInterceptor.Builder(PraaktisApp.getApplication()).build())
        builder.addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))

        builder.addInterceptor { chain ->
            if (loginSettings.token.isNotBlank()) {
                val newRequest = chain
                    .request()
                    .newBuilder()
                    .addHeader("Authorization", "Token ${loginSettings.token}")
                    .build()
                val r = chain.proceed(newRequest)
                return@addInterceptor r
            }
            chain.proceed(chain.request())
        }
        builder
            .readTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
        return builder.build()
    }

}
typealias BoolLV = LiveEvent<Boolean>
typealias StringLV = LiveEvent<String>
typealias AnyLV = LiveEvent<Any>
