package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.TechnicianEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TechnicianDao {
    @Query("SELECT * FROM technicians ORDER BY isOnCall DESC, name ASC")
    fun getAllTechnicians(): Flow<List<TechnicianEntity>>

    @Query("SELECT * FROM technicians WHERE hospitalId = :hospitalId ORDER BY isOnCall DESC, name ASC")
    fun getTechniciansByHospital(hospitalId: String): Flow<List<TechnicianEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(technicians: List<TechnicianEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(technician: TechnicianEntity): Long

    @Update
    suspend fun update(technician: TechnicianEntity)

    @Query("UPDATE technicians SET isOnCall = :isOnCall WHERE id = :id")
    suspend fun updateOnCall(id: Long, isOnCall: Boolean)
}
