package com.mobile.gympraaktis.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.mobile.gympraaktis.data.db.converter.DoubleListConverter
import com.mobile.gympraaktis.data.db.converter.IntListConverter
import com.mobile.gympraaktis.data.db.converter.StringListConverter
import com.mobile.gympraaktis.data.entities.*
import com.mobile.gympraaktis.domain.common.Constants.DATABASE_HOCKEY_VERSION
import com.mobile.gympraaktis.domain.entities.CountryItemDTO

@Database(
    entities = [
        UserData::class,
        CountryItemDTO::class,
        DashboardEntity::class,
        AnalysisEntity::class,
        AttemptChartDataEntity::class,
        ChartDataEntity::class,
        ScoreAnalysisEntity::class,
        TimelineEntity::class
    ],
    version = DATABASE_HOCKEY_VERSION,
    exportSchema = false
)
@TypeConverters(
    StringListConverter::class,
    DoubleListConverter::class,
    IntListConverter::class
)
abstract class PraaktisDatabase : RoomDatabase() {
    companion object {
        @Volatile
        private var INSTANCE: PraaktisDatabase? = null

        fun getInstance(context: Context): PraaktisDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): PraaktisDatabase {
            return Room.databaseBuilder(context, PraaktisDatabase::class.java, "praaktis_db")
                .fallbackToDestructiveMigration()
                .build()
        }
    }

    abstract fun getHockeyDao(): PraaktisDao

    abstract fun getCountriesDao(): CountriesDao

    abstract fun getDashboardDao(): DashboardDao

    abstract fun getTimelineDao(): TimelineDao

}