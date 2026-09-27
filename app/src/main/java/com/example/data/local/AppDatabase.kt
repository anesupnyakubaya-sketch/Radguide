package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.DowntimeLogDao
import com.example.data.local.dao.EquipmentDao
import com.example.data.local.dao.MaintenanceTaskDao
import com.example.data.local.dao.TechnicianAlertDao
import com.example.data.local.dao.TechnicianDao
import com.example.data.local.entity.DowntimeLogEntity
import com.example.data.local.entity.EquipmentEntity
import com.example.data.local.entity.MaintenanceTaskEntity
import com.example.data.local.entity.TechnicianAlertEntity
import com.example.data.local.entity.TechnicianEntity

@Database(
    entities = [
        EquipmentEntity::class,
        MaintenanceTaskEntity::class,
        DowntimeLogEntity::class,
        TechnicianAlertEntity::class,
        TechnicianEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun equipmentDao(): EquipmentDao
    abstract fun maintenanceTaskDao(): MaintenanceTaskDao
    abstract fun downtimeLogDao(): DowntimeLogDao
    abstract fun technicianAlertDao(): TechnicianAlertDao
    abstract fun technicianDao(): TechnicianDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "radguide_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
