package com.edwin.medsync

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.edwin.medsync.data.AuthViewModel
import com.edwin.medsync.navigation.AppNavHost
import com.edwin.medsync.ui.theme.MedSyncTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MedSyncTheme {
                val navController = rememberNavController()
                val viewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

                AppNavHost(
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    }
}
