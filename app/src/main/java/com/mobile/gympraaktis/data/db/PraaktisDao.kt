package com.mobile.gympraaktis.data.db

import androidx.room.*
import com.mobile.gympraaktis.data.entities.UserData
import com.mobile.gympraaktis.domain.entities.StoreResultModel
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface PraaktisDao {
    @Query("SELECT * FROM users")
    fun getUsers(): Single<List<UserData>>

    @Query("SELECT * FROM users WHERE id=:userId")
    fun get(userId: Long): Single<UserData>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveUser(user: UserData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveAllUsers(users: List<UserData>)

    @Delete
    fun removeUser(user: UserData)

    @Query("DELETE FROM users")
    fun clear()

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun saveResult(resultModel: StoreResultModel)

    @Query("SELECT * FROM offline_exercise_result")
    fun getOfflineExerciseResults(): Flow<List<StoreResultModel>>

    @Query("DELETE FROM offline_exercise_result")
    fun clearOfflineExerciseResults()

    @Delete
    fun removeOfflineExerciseResult(storeResultModel: StoreResultModel)

}
