package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.WallpaperEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WallpaperDao {
    @Query("SELECT * FROM wallpapers ORDER BY timestamp DESC")
    fun getAllWallpapers(): Flow<List<WallpaperEntity>>

    @Query("SELECT * FROM wallpapers WHERE isDownloaded = 1 ORDER BY timestamp DESC")
    fun getDownloadedWallpapers(): Flow<List<WallpaperEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWallpaper(wallpaper: WallpaperEntity): Long

    @Update
    suspend fun updateWallpaper(wallpaper: WallpaperEntity)

    @Query("UPDATE wallpapers SET isCurrentWallpaper = 0")
    suspend fun clearActiveWallpapers()

    @Query("UPDATE wallpapers SET isCurrentWallpaper = 1 WHERE id = :id")
    suspend fun setActiveWallpaper(id: Long)

    @Query("DELETE FROM wallpapers WHERE id = :id")
    suspend fun deleteWallpaper(id: Long)

    @Query("SELECT COUNT(*) FROM wallpapers")
    suspend fun getWallpaperCount(): Int
}
