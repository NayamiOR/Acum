package com.example.acum

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.LiveData
import kotlinx.coroutines.*
import androidx.lifecycle.viewModelScope

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val dataManager = ClickDataManager(application)
    
    private val _todayCount = MutableLiveData<Int>()
    val todayCount: LiveData<Int> = _todayCount
    
    private val _weekCount = MutableLiveData<Int>()
    val weekCount: LiveData<Int> = _weekCount
    
    private val _monthCount = MutableLiveData<Int>()
    val monthCount: LiveData<Int> = _monthCount
    
    private val _yearCount = MutableLiveData<Int>()
    val yearCount: LiveData<Int> = _yearCount
    
    private val _lastClickTimeAgo = MutableLiveData<String>()
    val lastClickTimeAgo: LiveData<String> = _lastClickTimeAgo
    
    private val _allRecords = MutableLiveData<List<ClickRecord>>()
    val allRecords: LiveData<List<ClickRecord>> = _allRecords
    
    private var updateJob: Job? = null
    
    init {
        updateStats()
        startPeriodicUpdate()
    }
    
    private fun startPeriodicUpdate() {
        updateJob = viewModelScope.launch {
            while (isActive) {
                delay(1000) // 每秒更新一次
                updateLastClickTime()
            }
        }
    }
    
    fun onButtonClick() {
        val record = ClickRecord()
        dataManager.saveClickRecord(record)
        updateStats()
    }
    
    fun deleteSelectedRecords(recordsToDelete: List<ClickRecord>) {
        dataManager.deleteClickRecords(recordsToDelete)
        updateStats()
    }
    
    fun clearAllRecords() {
        dataManager.clearAllRecords()
        updateStats()
    }
    
    fun refreshAllRecords() {
        _allRecords.value = dataManager.getClickRecords().sortedByDescending { it.timestamp }
    }
    
    private fun updateStats() {
        _todayCount.value = dataManager.getTodayClickCount()
        _weekCount.value = dataManager.getThisWeekClickCount()
        _monthCount.value = dataManager.getThisMonthClickCount()
        _yearCount.value = dataManager.getThisYearClickCount()
        updateLastClickTime()
        refreshAllRecords()
    }
    
    private fun updateLastClickTime() {
        _lastClickTimeAgo.value = dataManager.getLastClickTimeAgo()
    }
    
    override fun onCleared() {
        super.onCleared()
        updateJob?.cancel()
    }
} 