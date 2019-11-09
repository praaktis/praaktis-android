package com.mobile.praaktishockey.data.db

import androidx.room.*
import com.mobile.praaktishockey.data.entities.UserData
import io.reactivex.Single

@Dao
interface HockeyDao {
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
}
