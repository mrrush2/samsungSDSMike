package com.example.samsungsds.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.asLiveData
import com.example.samsungsds.domain.PreferencesRepository
import com.example.samsungsds.service.MyForegroundService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {
    private val TAG = "BootReceiver"

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            if (::preferencesRepository.isInitialized) {
                Log.d(TAG, "onReceive: is init")
                CoroutineScope(Dispatchers.IO).launch {
                    val switchState = preferencesRepository.switchFlow.first()
                    if (switchState) {
                        val serviceIntent = Intent(context, MyForegroundService::class.java)
                        context?.startForegroundService(serviceIntent)
                    } else Log.d(TAG, "onReceive: else")
                }
            } else Log.d(TAG, "onReceive: preferncesRepo not init")
        }
    }
}