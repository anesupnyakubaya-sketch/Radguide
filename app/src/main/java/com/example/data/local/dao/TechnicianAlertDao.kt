package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TechnicianAlertEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TechnicianAlertDao {
    @Query("SELECT * FROM technician_alerts ORDER BY CASE status WHEN 'ACTIVE' THEN 1 WHEN 'ACKNOWLEDGED' THEN 2 ELSE 3 END, timestamp DESC")
    fun getAllAlerts(): Flow<List<TechnicianAlertEntity>>

    @Query("SELECT * FROM technician_alerts WHERE hospitalId = :hospitalId ORDER BY CASE status WHEN 'ACTIVE' THEN 1 WHEN 'ACKNOWLEDGED' THEN 2 ELSE 3 END, timestamp DESC")
    fun getAlertsByHospital(hospitalId: String): Flow<List<TechnicianAlertEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlert(alert: TechnicianAlertEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllAlerts(alerts: List<TechnicianAlertEntity>)

    @Update
    suspend fun updateAlert(alert: TechnicianAlertEntity)

    @Query("UPDATE technician_alerts SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE technician_alerts SET status = :status, assignedTechnicianName = :techName WHERE id = :id")
    suspend fun acknowledgeAlert(id: Long, status: String, techName: String)
}
