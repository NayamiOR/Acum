package com.example.acum

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class ClickDataManager(context: Context) {
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("click_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        private const val CLICK_RECORDS_KEY = "click_records"
    }
    
    fun saveClickRecord(record: ClickRecord) {
        val records = getClickRecords().toMutableList()
        records.add(record)
        val json = gson.toJson(records)
        sharedPreferences.edit().putString(CLICK_RECORDS_KEY, json).apply()
    }
    
    fun deleteClickRecord(record: ClickRecord) {
        val records = getClickRecords().toMutableList()
        records.removeAll { it.timestamp == record.timestamp }
        val json = gson.toJson(records)
        sharedPreferences.edit().putString(CLICK_RECORDS_KEY, json).apply()
    }
    
    fun deleteClickRecords(recordsToDelete: List<ClickRecord>) {
        val records = getClickRecords().toMutableList()
        val timestampsToDelete = recordsToDelete.map { it.timestamp }.toSet()
        records.removeAll { it.timestamp in timestampsToDelete }
        val json = gson.toJson(records)
        sharedPreferences.edit().putString(CLICK_RECORDS_KEY, json).apply()
    }
    
    fun clearAllRecords() {
        sharedPreferences.edit().remove(CLICK_RECORDS_KEY).apply()
    }
    
    fun getClickRecords(): List<ClickRecord> {
        val json = sharedPreferences.getString(CLICK_RECORDS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<ClickRecord>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
    
    fun getTodayClickCount(): Int {
        val now = Calendar.getInstance()
        val startOfDay = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        return getClickRecords().count { record ->
            record.timestamp >= startOfDay.timeInMillis
        }
    }
    
    fun getThisWeekClickCount(): Int {
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - (7 * 24 * 60 * 60 * 1000L) // 最近7个24小时
        
        return getClickRecords().count { record ->
            record.timestamp >= sevenDaysAgo
        }
    }
    
    fun getThisMonthClickCount(): Int {
        val now = Calendar.getInstance()
        val startOfMonth = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_MONTH, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        return getClickRecords().count { record ->
            record.timestamp >= startOfMonth.timeInMillis
        }
    }
    
    fun getThisYearClickCount(): Int {
        val now = Calendar.getInstance()
        val startOfYear = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_YEAR, 1)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        
        return getClickRecords().count { record ->
            record.timestamp >= startOfYear.timeInMillis
        }
    }
    
    fun getLastClickTimeAgo(): String {
        val records = getClickRecords()
        if (records.isEmpty()) {
            return "还未点击过"
        }
        
        val lastClick = records.maxByOrNull { it.timestamp }
        if (lastClick == null) {
            return "还未点击过"
        }
        
        val now = System.currentTimeMillis()
        val diff = now - lastClick.timestamp
        
        val totalSeconds = diff / 1000
        val totalMinutes = totalSeconds / 60
        val totalHours = totalMinutes / 60
        val totalDays = totalHours / 24
        val totalMonths = totalDays / 30 // 简化计算，按30天一个月
        
        // 计算各个单位的剩余值
        val months = totalMonths
        val days = totalDays % 30
        val hours = totalHours % 24
        val minutes = totalMinutes % 60
        val seconds = totalSeconds % 60
        
        val parts = mutableListOf<String>()
        
        // 只有满一月才显示月
        if (months >= 1) {
            parts.add("${months}个月")
        }
        
        // 只有满一日才显示日
        if (totalDays >= 1) {
            parts.add("${days}天")
        }
        
        // 总是显示时分秒
        parts.add("${hours}小时")
        parts.add("${minutes}分钟")
        parts.add("${seconds}秒")
        
        val result = parts.joinToString("") + "前"
        
        // 满一日时，必须在括号中显示总天数
        return if (totalDays >= 1) {
            "$result（共计${totalDays}天）"
        } else {
            result
        }
    }
} 