package com.example.data.local

import android.content.Context
import com.example.data.model.OfflineDataProvider
import com.example.data.model.WidgetConfig
import com.example.widget.CustomAppWidgetProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class WidgetRepository(private val context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val dao = db.widgetDao()

    val allWidgets: Flow<List<WidgetConfig>> = dao.getAllWidgetsFlow().map { entities ->
        entities.map { it.toDomain() }
    }

    val activeLauncherWidget: Flow<WidgetConfig?> = dao.getActiveLauncherWidgetFlow().map { it?.toDomain() }

    suspend fun initializeDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        val count = dao.getCount()
        if (count == 0) {
            val presets = OfflineDataProvider.preinstalledTemplates.mapIndexed { index, config ->
                // Make the first one active by default
                val updated = if (index == 0) config.copy(isActiveOnLauncher = true) else config
                WidgetEntity.fromDomain(updated)
            }
            dao.insertWidgets(presets)
            // Trigger launcher widget update
            CustomAppWidgetProvider.updateAllWidgets(context)
        }
    }

    suspend fun getWidgetById(id: String): WidgetConfig? = withContext(Dispatchers.IO) {
        dao.getWidgetById(id)?.toDomain()
    }

    suspend fun saveWidget(config: WidgetConfig) = withContext(Dispatchers.IO) {
        val entity = WidgetEntity.fromDomain(config.copy(updatedAt = System.currentTimeMillis()))
        dao.insertWidget(entity)
        if (config.isActiveOnLauncher) {
            dao.clearActiveLauncherWidgets()
            dao.setActiveLauncherWidget(config.id)
        }
        CustomAppWidgetProvider.updateAllWidgets(context)
    }

    suspend fun setActiveForLauncher(widgetId: String) = withContext(Dispatchers.IO) {
        dao.clearActiveLauncherWidgets()
        dao.setActiveLauncherWidget(widgetId)
        CustomAppWidgetProvider.updateAllWidgets(context)
    }

    suspend fun incrementCounter(widgetId: String) = withContext(Dispatchers.IO) {
        dao.incrementCounter(widgetId)
        CustomAppWidgetProvider.updateAllWidgets(context)
    }

    suspend fun toggleFavorite(widgetId: String) = withContext(Dispatchers.IO) {
        dao.toggleFavorite(widgetId)
    }

    suspend fun deleteWidget(widgetId: String) = withContext(Dispatchers.IO) {
        dao.deleteWidgetById(widgetId)
        CustomAppWidgetProvider.updateAllWidgets(context)
    }

    suspend fun duplicateWidget(widgetId: String): WidgetConfig? = withContext(Dispatchers.IO) {
        val existing = dao.getWidgetById(widgetId)?.toDomain() ?: return@withContext null
        val copy = existing.copy(
            id = java.util.UUID.randomUUID().toString(),
            name = "${existing.name} (نسخة)",
            isActiveOnLauncher = false,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
        dao.insertWidget(WidgetEntity.fromDomain(copy))
        copy
    }

    suspend fun importWidgetFromJson(jsonString: String): Result<WidgetConfig> = withContext(Dispatchers.IO) {
        val parseResult = WidgetConfig.fromJsonString(jsonString)
        if (parseResult.isSuccess) {
            val imported = parseResult.getOrThrow()
            dao.insertWidget(WidgetEntity.fromDomain(imported))
            Result.success(imported)
        } else {
            parseResult
        }
    }
}
