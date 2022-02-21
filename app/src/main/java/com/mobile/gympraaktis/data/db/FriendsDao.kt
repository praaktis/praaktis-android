package com.mobile.gympraaktis.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mobile.gympraaktis.data.entities.FriendEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FriendsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFriends(list: List<FriendEntity>)

    @Query("DELETE FROM friends")
    fun removeAllFriends()

    @Query("SELECT * FROM friends ORDER BY firstName DESC")
    fun getAllFriends(): Flow<List<FriendEntity>>

    @Query("SELECT * FROM friends WHERE status='Confirmed' ORDER BY firstName DESC")
    fun getConfirmedFriends(): Flow<List<FriendEntity>>

}