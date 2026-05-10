package com.example.travelloop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import com.example.traveloop.RetrofitClient
import com.example.traveloop.TraveloopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        RetrofitClient.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            TraveloopTheme(darkTheme = true) {
                AppNavigation()
            }
        }
    }
}