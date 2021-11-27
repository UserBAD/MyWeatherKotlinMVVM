package com.example.myweatherkotlinmvvm.framework.ui.history

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myweatherkotlinmvvm.AppState
import com.example.myweatherkotlinmvvm.model.entities.repository.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val repository: Repository
) : ViewModel() {
    val historyLiveData: MutableLiveData<AppState> = MutableLiveData()

    fun getAllHistory() {
        historyLiveData.value = AppState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            historyLiveData.postValue(AppState.Success(repository.getAllHistory()))
        }
    }
}