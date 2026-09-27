package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.data.local.AppDatabase
import com.example.data.local.DatabaseInitializer
import com.example.data.repository.RadGuideRepository
import com.example.ui.RadGuideApp
import com.example.ui.theme.RadGuideTheme
import com.example.viewmodel.RadGuideViewModel
import com.example.viewmodel.RadGuideViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: RadGuideRepository
    private lateinit var viewModel: RadGuideViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        database = AppDatabase.getDatabase(applicationContext)
        repository = RadGuideRepository(database)

        // Prepopulate database with realistic Parirenyatwa and Mpilo data if first run
        lifecycleScope.launch {
            DatabaseInitializer.populateInitialData(database)
        }

        val factory = RadGuideViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[RadGuideViewModel::class.java]

        setContent {
            RadGuideTheme {
                RadGuideApp(viewModel = viewModel)
            }
        }
    }
}
