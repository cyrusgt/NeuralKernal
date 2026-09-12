package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.AgentDao
import com.example.data.local.dao.ChatDao
import com.example.data.local.dao.DocumentDao
import com.example.data.local.dao.KernelLogDao
import com.example.data.local.dao.ResearchDao
import com.example.data.local.dao.UpgradeDao
import com.example.data.local.dao.WallpaperDao
import com.example.data.local.entity.AgentEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.KernelLogEntity
import com.example.data.local.entity.ResearchLogEntity
import com.example.data.local.entity.UpgradeModuleEntity
import com.example.data.local.entity.WallpaperEntity

@Database(
    entities = [
        ChatMessageEntity::class,
        DocumentEntity::class,
        AgentEntity::class,
        ResearchLogEntity::class,
        KernelLogEntity::class,
        UpgradeModuleEntity::class,
        WallpaperEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
    abstract fun documentDao(): DocumentDao
    abstract fun agentDao(): AgentDao
    abstract fun researchDao(): ResearchDao
    abstract fun kernelLogDao(): KernelLogDao
    abstract fun upgradeDao(): UpgradeDao
    abstract fun wallpaperDao(): WallpaperDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "neural_kernel_encrypted.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
