package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        Magasin::class,
        ChargementArchive::class,
        DechargementArchive::class,
        ControleRemorqueArchive::class,
        ControleExportArchive::class
    ],
    version = 1,
    exportSchema = false
)
abstract class LogisticsDatabase : RoomDatabase() {
    
    abstract fun logisticsDao(): LogisticsDao

    companion object {
        @Volatile
        private var INSTANCE: LogisticsDatabase? = null

        fun getDatabase(context: Context): LogisticsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LogisticsDatabase::class.java,
                    "logistics_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
