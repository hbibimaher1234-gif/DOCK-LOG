package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.data.LogisticsDatabase
import com.example.data.LogisticsRepository
import com.example.ui.MainLogisticsApp
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.LogisticsViewModel
import com.example.viewmodel.LogisticsViewModelFactory

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize local database and MVVM layer components
        val database = LogisticsDatabase.getDatabase(this)
        val repository = LogisticsRepository(database.logisticsDao())
        
        val viewModel: LogisticsViewModel by viewModels {
            LogisticsViewModelFactory(repository)
        }

        enableEdgeToEdge()
        
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainLogisticsApp(viewModel = viewModel)
                }
            }
        }
    }
}
