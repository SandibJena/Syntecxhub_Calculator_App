package com.noguts.calcy.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CalculationHistoryDao {

    @Query("SELECT * FROM calculation_history ORDER BY createdAt DESC")
    fun observeHistory(): Flow<List<CalculationHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: CalculationHistoryEntity)

    @Query("DELETE FROM calculation_history")
    suspend fun clearAll()
}
