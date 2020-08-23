package com.mobile.gympraaktis.domain.common

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import com.google.gson.Gson
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object Constants {
    const val DATABASE_HOCKEY_VERSION: Int = 2
    const val TIMELINE = "TIMELINE"
    const val TIMELINE_CHALLENGE_ITEM = "TIMELINE_CHALLENGE_ITEM"

    var retrofit: Retrofit? = null

    inline fun <reified S> createService(): S {
        return getRetrofit("https://api.praaktis.fuzzydigital.com/api/").create(S::class.java)
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

    fun createImageUri(context: Context): Uri? {
        val contentResolver = context.contentResolver
        val cv = ContentValues()
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        cv.put(MediaStore.Images.Media.TITLE, timeStamp)
        return contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
    }

    fun getImageUri(): Uri? {
        var m_imgUri: Uri? = null
        try {
            val cacheDir = Environment.getExternalStorageDirectory()
            val imageFile = File.createTempFile("img", ".jpg", cacheDir)
            m_imgUri = Uri.fromFile(imageFile)
        } catch (ignored: Exception) {
        }

        return m_imgUri
    }

}
typealias BoolLV = LiveEvent<Boolean>
typealias StringLV = LiveEvent<String>
typealias AnyLV = LiveEvent<Any>
