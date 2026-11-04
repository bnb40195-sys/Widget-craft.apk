package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface WidgetDao {
    @Query("SELECT * FROM widgets ORDER BY updatedAt DESC")
    fun getAllWidgetsFlow(): Flow<List<WidgetEntity>>

    @Query("SELECT * FROM widgets WHERE id = :id LIMIT 1")
    suspend fun getWidgetById(id: String): WidgetEntity?

    @Query("SELECT * FROM widgets WHERE isActiveOnLauncher = 1 LIMIT 1")
    suspend fun getActiveLauncherWidget(): WidgetEntity?

    @Query("SELECT * FROM widgets WHERE isActiveOnLauncher = 1 LIMIT 1")
    fun getActiveLauncherWidgetFlow(): Flow<WidgetEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidget(widget: WidgetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWidgets(widgets: List<WidgetEntity>)

    @Update
    suspend fun updateWidget(widget: WidgetEntity)

    @Query("UPDATE widgets SET isActiveOnLauncher = 0")
    suspend fun clearActiveLauncherWidgets()

    @Query("UPDATE widgets SET isActiveOnLauncher = 1 WHERE id = :id")
    suspend fun setActiveLauncherWidget(id: String)

    @Query("UPDATE widgets SET counterValue = counterValue + counterStep WHERE id = :id")
    suspend fun incrementCounter(id: String)

    @Query("UPDATE widgets SET isFavorite = NOT isFavorite WHERE id = :id")
    suspend fun toggleFavorite(id: String)

    @Delete
    suspend fun deleteWidget(widget: WidgetEntity)

    @Query("DELETE FROM widgets WHERE id = :id")
    suspend fun deleteWidgetById(id: String)

    @Query("SELECT COUNT(*) FROM widgets")
    suspend fun getCount(): Int
}
