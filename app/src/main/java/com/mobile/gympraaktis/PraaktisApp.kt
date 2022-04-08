package com.mobile.gympraaktis

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.work.*
import com.google.android.exoplayer2.database.StandaloneDatabaseProvider
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.DefaultDataSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.upstream.HttpDataSource
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.jakewharton.threetenabp.AndroidThreeTen
import com.mobile.gympraaktis.domain.common.pref.SettingsStorage
import io.paperdb.Paper
import kotlinx.coroutines.*
import timber.log.Timber


class PraaktisApp : Application() {

    private val cacheSize: Long = 1024 * 1024 * 1024
    private lateinit var cacheEvictor: LeastRecentlyUsedCacheEvictor
    private lateinit var exoplayerDatabaseProvider: StandaloneDatabaseProvider

    override fun onCreate() {
        super.onCreate()
        Paper.init(this)
        app = this
        AndroidThreeTen.init(this)
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
        initPreferences()

        cacheEvictor = LeastRecentlyUsedCacheEvictor(cacheSize)
        exoplayerDatabaseProvider = StandaloneDatabaseProvider(this)
        cache = SimpleCache(obbDir, cacheEvictor, exoplayerDatabaseProvider)

    }

    private fun initPreferences() {
        SettingsStorage.initWith(this)
    }

    companion object {
        lateinit var cache: SimpleCache

        @JvmField
        var app: Application? = null

        @JvmStatic
        fun getApplication(): Application = app!!

    }
}

class VideoPreloadWorker(private val context: Context, workerParameters: WorkerParameters) :
    Worker(context, workerParameters) {
    private var videoCachingJob: Job? = null
    private lateinit var mHttpDataSourceFactory: HttpDataSource.Factory
    private lateinit var mDefaultDataSourceFactory: DefaultDataSource.Factory
    private lateinit var mCacheDataSource: CacheDataSource
    private val cache: SimpleCache = PraaktisApp.cache

    companion object {
        const val VIDEO_URL = "video_url"

        fun buildWorkRequest(yourParameter: String): OneTimeWorkRequest {
            val data = Data.Builder().putString(VIDEO_URL, yourParameter).build()
            return OneTimeWorkRequestBuilder<VideoPreloadWorker>().apply { setInputData(data) }
                .build()
        }
    }

    override fun doWork(): Result {
        try {
            val videoUrl: String? = inputData.getString(VIDEO_URL)

            mHttpDataSourceFactory = DefaultHttpDataSource.Factory()
                .setAllowCrossProtocolRedirects(true)

            mDefaultDataSourceFactory = DefaultDataSource.Factory(context, mHttpDataSourceFactory)

            mCacheDataSource = CacheDataSource.Factory()
                .setCache(cache)
                .setUpstreamDataSourceFactory(mHttpDataSourceFactory)
                .createDataSource()

            preCacheVideo(videoUrl)

            return Result.success()

        } catch (e: Exception) {
            return Result.failure()
        }
    }

    var percent = 0.0

    @OptIn(DelicateCoroutinesApi::class)
    private fun preCacheVideo(videoUrl: String?) {

        val videoUri = Uri.parse(videoUrl)
        val dataSpec = DataSpec(videoUri)

        val progressListener = CacheWriter.ProgressListener { requestLength, bytesCached, _ ->
            val downloadPercentage: Double = (bytesCached * 100.0 / requestLength)
            percent = downloadPercentage
//            Timber.d("PERCENT $percent")
            // Do Something
        }

        videoCachingJob = GlobalScope.launch(Dispatchers.Default) {
            cacheVideo(dataSpec, progressListener)
//            if(percent < 100) {
//                preCacheVideo(videoUrl)
//            }
        }
    }

    private fun cacheVideo(mDataSpec: DataSpec, mProgressListener: CacheWriter.ProgressListener) {
        runCatching {
            CacheWriter(mCacheDataSource, mDataSpec, null, mProgressListener).cache()
        }.onFailure {
            it.printStackTrace()
        }
    }

}