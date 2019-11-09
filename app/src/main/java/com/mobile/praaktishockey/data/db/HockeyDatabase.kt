package com.mobile.praaktishockey.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mobile.praaktishockey.data.entities.UserData
import com.mobile.praaktishockey.domain.common.Constants.DATABASE_HOCKEY_VERSION
import com.mobile.praaktishockey.domain.entities.CountryItemDTO

@Database(
    entities = [UserData::class, CountryItemDTO::class],
    version = DATABASE_HOCKEY_VERSION,
    exportSchema = false
)
abstract class HockeyDatabase : RoomDatabase() {
    companion object {
        private var INSTANCE: HockeyDatabase? = null
        fun getDatabase(context: Context): HockeyDatabase {
            if (INSTANCE == null)
                INSTANCE = Room.databaseBuilder(
                    context,
                    HockeyDatabase::class.java, "hockey_db"
                ).build()
            return INSTANCE!!
        }
    }

    abstract fun getHockeyDao(): HockeyDao

    abstract fun getCountriesDao(): CountriesDao
}
