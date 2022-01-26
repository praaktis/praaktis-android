package com.mobile.gympraaktis.data.db

import androidx.room.*
import com.mobile.gympraaktis.data.entities.TimelineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TimelineDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTimeline(list: List<TimelineEntity>)

    @Query("DELETE FROM timeline")
    fun removeAllTimeline()

    @Transaction
    fun removeAndInsertTimeline(list: List<TimelineEntity>) {
        removeAllTimeline()
        insertTimeline(list)
    }

    @Query("SELECT * FROM timeline ORDER BY attempt_id DESC")
    fun getAllTimeline(): Flow<List<TimelineEntity>>

}