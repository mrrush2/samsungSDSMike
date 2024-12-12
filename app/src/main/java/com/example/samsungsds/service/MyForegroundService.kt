package com.example.samsungsds.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.samsungsds.R
import com.example.samsungsds.domain.PreferencesRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MyForegroundService : Service() {

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = buildNotification()
        startForeground(REQUEST_CODE, notification, FOREGROUND_SERVICE_TYPE_LOCATION)
        return START_STICKY
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun createNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Foreground Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel((serviceChannel))
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("My Service")
            .setContentText("Running in the foreground")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    companion object {
        const val CHANNEL_ID = "ForegroundServiceChannel"
        private const val REQUEST_CODE = 1
        private const val TAG = "MyForegroundService"
    }
}