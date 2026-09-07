package com.example.aplicacion_feria

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.aplicacion_feria.ui.DestinoNav
import com.example.aplicacion_feria.ui.screens.CompraScreen
import com.example.aplicacion_feria.ui.screens.CompraViewModel
import com.example.aplicacion_feria.ui.screens.HistorialScreen
import com.example.aplicacion_feria.ui.screens.HistorialViewModel
import com.example.aplicacion_feria.ui.theme.Aplicacion_FeriaTheme

class MainActivity : ComponentActivity() {
    private val compraViewModel: CompraViewModel by viewModels()
    private val historialViewModel: HistorialViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Aplicacion_FeriaTheme {
                MainApp(compraViewModel, historialViewModel)
            }
        }
    }
}

@Composable
fun MainApp(compraViewModel: CompraViewModel, historialViewModel: HistorialViewModel) {
    val navController = rememberNavController()
    val pantallas = listOf(DestinoNav.Compra, DestinoNav.Historial)

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val rutaActual = navBackStackEntry?.destination?.route

                pantallas.forEach { pantalla ->
                    NavigationBarItem(
                        icon = { Icon(pantalla.icono, contentDescription = pantalla.titulo) },
                        label = { Text(pantalla.titulo) },
                        selected = rutaActual == pantalla.ruta,
                        onClick = {
                            navController.navigate(pantalla.ruta) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = DestinoNav.Compra.ruta,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(DestinoNav.Compra.ruta) {
                CompraScreen(viewModel = compraViewModel)
            }
            composable(DestinoNav.Historial.ruta) {
                HistorialScreen(viewModel = historialViewModel)
            }
        }
    }
}