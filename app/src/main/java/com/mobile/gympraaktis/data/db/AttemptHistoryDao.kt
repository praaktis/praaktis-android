package com.mobile.gympraaktis.data.db

import androidx.paging.DataSource
import androidx.room.*
import com.mobile.gympraaktis.data.entities.AttemptEntity

@Dao
interface AttemptHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttempts(list: List<AttemptEntity>)

    @Query("DELETE FROM attempt")
    suspend fun removeAttemptHistory()

    @Transaction
    suspend fun removeAndInsertAttempts(list: List<AttemptEntity>) {
        removeAttemptHistory()
        insertAttempts(list)
    }

    @Query("SELECT * FROM attempt ORDER BY attempt_id DESC")
    fun getAttempts(): DataSource.Factory<Int, AttemptEntity>

}