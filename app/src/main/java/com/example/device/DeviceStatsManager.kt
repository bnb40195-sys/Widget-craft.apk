package com.example.device

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Environment
import android.os.StatFs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class DeviceStats(
    val batteryPct: Int = 100,
    val isCharging: Boolean = false,
    val batteryHealth: String = "جيدة",
    val availableStorageGb: Double = 0.0,
    val totalStorageGb: Double = 0.0,
    val freeRamMb: Long = 0,
    val totalRamMb: Long = 0,
    val currentTimeFormatted: String = "12:00",
    val currentSeconds: String = "00",
    val isAm: Boolean = true,
    val currentDateAr: String = "",
    val currentDayNameAr: String = "",
    val currentDateEn: String = "",
    val currentDayNumber: Int = 1,
    val currentMonthAr: String = ""
)

object DeviceStatsManager {

    fun getDeviceStats(context: Context): DeviceStats {
        // Battery info
        val batteryFilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        val batteryStatus: Intent? = context.registerReceiver(null, batteryFilter)

        var batteryPct = 100
        var isCharging = false
        var batteryHealth = "ممتازة"

        if (batteryStatus != null) {
            val level: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1)
            val scale: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1)
            if (level != -1 && scale != -1) {
                batteryPct = (level * 100 / scale.toFloat()).toInt()
            }
            val status: Int = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

            val health = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, BatteryManager.BATTERY_HEALTH_GOOD)
            batteryHealth = when (health) {
                BatteryManager.BATTERY_HEALTH_GOOD -> "جيدة ومستقرة"
                BatteryManager.BATTERY_HEALTH_OVERHEAT -> "حرارة مرتفعة"
                BatteryManager.BATTERY_HEALTH_COLD -> "باردة"
                BatteryManager.BATTERY_HEALTH_DEAD -> "تالفة"
                else -> "طبيعية"
            }
        }

        // Storage info
        var freeStorage = 0.0
        var totalStorage = 0.0
        try {
            val stat = StatFs(Environment.getDataDirectory().path)
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            val totalBlocks = stat.blockCountLong
            val gb = 1024.0 * 1024.0 * 1024.0
            freeStorage = (availableBlocks * blockSize) / gb
            totalStorage = (totalBlocks * blockSize) / gb
        } catch (e: Exception) {
            // fallback
        }

        // RAM info
        var freeRam: Long = 0
        var totalRam: Long = 0
        try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val memInfo = ActivityManager.MemoryInfo()
            actManager?.getMemoryInfo(memInfo)
            freeRam = memInfo.availMem / (1024 * 1024)
            totalRam = memInfo.totalMem / (1024 * 1024)
        } catch (e: Exception) {
            // fallback
        }

        // Time and Date formatting
        val now = Date()
        val timeFormat = SimpleDateFormat("hh:mm", Locale.getDefault())
        val secondsFormat = SimpleDateFormat("ss", Locale.getDefault())
        val cal = Calendar.getInstance()
        val isAm = cal.get(Calendar.AM_PM) == Calendar.AM

        val dayFormatAr = SimpleDateFormat("EEEE", Locale("ar"))
        val dateFormatAr = SimpleDateFormat("d MMMM yyyy", Locale("ar"))
        val dateFormatEn = SimpleDateFormat("EEEE, d MMM yyyy", Locale.US)

        return DeviceStats(
            batteryPct = batteryPct,
            isCharging = isCharging,
            batteryHealth = batteryHealth,
            availableStorageGb = String.format(Locale.US, "%.1f", freeStorage).toDoubleOrNull() ?: 0.0,
            totalStorageGb = String.format(Locale.US, "%.1f", totalStorage).toDoubleOrNull() ?: 0.0,
            freeRamMb = freeRam,
            totalRamMb = totalRam,
            currentTimeFormatted = timeFormat.format(now),
            currentSeconds = secondsFormat.format(now),
            isAm = isAm,
            currentDateAr = dateFormatAr.format(now),
            currentDayNameAr = dayFormatAr.format(now),
            currentDateEn = dateFormatEn.format(now),
            currentDayNumber = cal.get(Calendar.DAY_OF_MONTH),
            currentMonthAr = SimpleDateFormat("MMMM", Locale("ar")).format(now)
        )
    }
}
