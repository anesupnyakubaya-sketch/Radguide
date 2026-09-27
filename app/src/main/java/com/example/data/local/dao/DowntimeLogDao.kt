package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.DowntimeLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DowntimeLogDao {
    @Query("SELECT * FROM downtime_logs ORDER BY isResolved ASC, startTime DESC")
    fun getAllLogs(): Flow<List<DowntimeLogEntity>>

    @Query("SELECT * FROM downtime_logs WHERE hospitalId = :hospitalId ORDER BY isResolved ASC, startTime DESC")
    fun getLogsByHospital(hospitalId: String): Flow<List<DowntimeLogEntity>>

    @Query("SELECT * FROM downtime_logs WHERE isResolved = 0 ORDER BY startTime DESC")
    fun getActiveDowntimes(): Flow<List<DowntimeLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: DowntimeLogEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllLogs(logs: List<DowntimeLogEntity>)

    @Update
    suspend fun updateLog(log: DowntimeLogEntity)

    @Query("UPDATE downtime_logs SET isResolved = 1, resolvedTime = :resolvedTime, rootCause = :rootCause, resolutionAction = :action, resolvedByTechnician = :tech WHERE id = :id")
    suspend fun resolveDowntime(id: Long, resolvedTime: Long, rootCause: String, action: String, tech: String)
}
