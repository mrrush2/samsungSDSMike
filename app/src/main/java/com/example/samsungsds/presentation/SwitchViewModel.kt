package com.example.samsungsds.presentation

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.samsungsds.domain.PreferencesRepository
import com.example.samsungsds.service.MyForegroundService
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SwitchViewModel @Inject constructor(
    private val preferencesRepository: PreferencesRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    val switchState: LiveData<Boolean> = preferencesRepository.switchFlow.asLiveData()

    fun saveSwitchState(isOn: Boolean) {
        viewModelScope.launch {
            preferencesRepository.saveSwitchState(isOn)
            val serviceIntent = Intent(context, MyForegroundService::class.java)
            if (isOn) {
                context.startForegroundService(serviceIntent)
            } else {
                context.stopService(serviceIntent)
            }
        }
    }
}