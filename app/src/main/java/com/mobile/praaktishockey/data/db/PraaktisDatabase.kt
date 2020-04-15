package com.mobile.praaktishockey.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mobile.praaktishockey.data.entities.UserData
import com.mobile.praaktishockey.domain.common.Constants.DATABASE_HOCKEY_VERSION
import com.mobile.praaktishockey.domain.entities.CountryItemDTO

@Database(
    entities = [
        UserData::class,
        CountryItemDTO::class
    ],
    version = DATABASE_HOCKEY_VERSION,
    exportSchema = false
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


}
