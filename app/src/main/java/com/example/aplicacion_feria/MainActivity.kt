package com.example.aplicacion_feria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.aplicacion_feria.ui.screens.CompraScreen
import com.example.aplicacion_feria.ui.screens.CompraViewModel
import com.example.aplicacion_feria.ui.theme.Aplicacion_FeriaTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Aplicacion_FeriaTheme {
                val viewModel: CompraViewModel = viewModel()
                CompraScreen(viewModel = viewModel)
            }
        }
    }
}