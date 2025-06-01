package com.example.acum

import java.text.SimpleDateFormat
import java.util.*

data class ClickRecord(
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedTime(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        return dateFormat.format(Date(timestamp))
    }
} 