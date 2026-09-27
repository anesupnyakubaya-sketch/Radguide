package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.MaintenanceTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceTaskDao {
    @Query("SELECT * FROM maintenance_tasks ORDER BY isCompleted ASC, dueDate ASC")
    fun getAllTasks(): Flow<List<MaintenanceTaskEntity>>

    @Query("SELECT * FROM maintenance_tasks WHERE hospitalId = :hospitalId ORDER BY isCompleted ASC, dueDate ASC")
    fun getTasksByHospital(hospitalId: String): Flow<List<MaintenanceTaskEntity>>

    @Query("SELECT * FROM maintenance_tasks WHERE equipmentId = :equipmentId ORDER BY isCompleted ASC, dueDate ASC")
    fun getTasksByEquipment(equipmentId: Long): Flow<List<MaintenanceTaskEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: MaintenanceTaskEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllTasks(tasks: List<MaintenanceTaskEntity>)

    @Update
    suspend fun updateTask(task: MaintenanceTaskEntity)

    @Query("UPDATE maintenance_tasks SET isCompleted = 1, completedDate = :completedDate, completedBy = :completedBy, technicianNotes = :notes WHERE id = :id")
    suspend fun markTaskCompleted(id: Long, completedDate: Long, completedBy: String, notes: String)

    @Query("SELECT COUNT(*) FROM maintenance_tasks WHERE isCompleted = 0 AND dueDate < :currentTime")
    fun getOverdueCount(currentTime: Long): Flow<Int>
}
