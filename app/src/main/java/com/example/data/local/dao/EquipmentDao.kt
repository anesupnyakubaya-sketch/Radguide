package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.EquipmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EquipmentDao {
    @Query("SELECT * FROM equipment ORDER BY id ASC")
    fun getAllEquipment(): Flow<List<EquipmentEntity>>

    @Query("SELECT * FROM equipment WHERE hospitalId = :hospitalId ORDER BY id ASC")
    fun getEquipmentByHospital(hospitalId: String): Flow<List<EquipmentEntity>>

    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    suspend fun getEquipmentById(id: Long): EquipmentEntity?

    @Query("SELECT * FROM equipment WHERE id = :id LIMIT 1")
    fun getEquipmentByIdFlow(id: Long): Flow<EquipmentEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEquipment(equipment: EquipmentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllEquipment(list: List<EquipmentEntity>)

    @Update
    suspend fun updateEquipment(equipment: EquipmentEntity)

    @Query("UPDATE equipment SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("UPDATE equipment SET chillerTempCelsius = :temp, sf6PressurePsi = :pressure WHERE id = :id")
    suspend fun updateTelemetry(id: Long, temp: Double, pressure: Double)

    @Query("SELECT COUNT(*) FROM equipment")
    suspend fun getCount(): Int
}
