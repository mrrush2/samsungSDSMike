package com.example.samsungsds

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.samsungsds.presentation.SwitchViewModel
import com.example.samsungsds.ui.theme.SamsungSDSTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.runtime.livedata.observeAsState
import androidx.core.content.ContextCompat
import com.example.samsungsds.domain.PreferencesRepository
import com.example.samsungsds.service.MyForegroundService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<Array<String>>

    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions: Map<String, Boolean> ->
                if (permissions.all { it.value}) {
                    val serviceIntent = Intent(this, MyForegroundService::class.java)
                    ContextCompat.startForegroundService(this, serviceIntent)
                }
            }
        requestPermissions()

        enableEdgeToEdge()
        setContent {
            SamsungSDSTheme {
                HomeScreen()
            }
        }
    }

    override fun onResume() {
        Log.d(TAG, "onResume: ")
        super.onResume()
        CoroutineScope(Dispatchers.IO).launch {
            val switchState = preferencesRepository.switchFlow.first()
            if (switchState) {
                val serviceIntent = Intent(this@MainActivity, MyForegroundService::class.java)
                ContextCompat.startForegroundService(this@MainActivity, serviceIntent) } } }

    private fun requestPermissions() {
        val permissions = mutableListOf(
            Manifest.permission.FOREGROUND_SERVICE,
            Manifest.permission.FOREGROUND_SERVICE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (missingPermissions.isNotEmpty()) {
            requestPermissionLauncher.launch(missingPermissions.toTypedArray())
        } else startForegroundServiceIfNeeded()
    }

    private fun startForegroundServiceIfNeeded() {
        val serviceIntent = Intent(this, MyForegroundService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent) }

    companion object {
        const val TAG = "MainActivity"
    }

}

@Composable
fun HomeScreen(viewModel: SwitchViewModel = hiltViewModel()) {
    val switchState by viewModel.switchState.observeAsState(false)

    Column(
       modifier = Modifier
           .fillMaxSize()
           .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Switch is ${if (switchState) "ON" else "OFF"}")
        Switch( checked = switchState,
            onCheckedChange = {
                isChecked -> viewModel.saveSwitchState(isChecked)
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    SamsungSDSTheme {
        HomeScreen()
    }
}