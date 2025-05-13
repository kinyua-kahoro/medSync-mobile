package com.edwin.medsync.ui.theme.screens.medication

import com.edwin.medsync.ui.theme.screens.dashboard.SampleContent
import android.R.attr.content
import android.R.id.content
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.edwin.medsync.R
import com.edwin.medsync.navigation.ROUTE_DASHBOARD
import com.edwin.medsync.navigation.ROUTE_HOME
import com.edwin.medsync.navigation.ROUTE_PROFILE
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Medication_Screen(navController: NavHostController) {
    var presses by remember { mutableIntStateOf(0) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(24.dp))

                    // Header section
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.favicon),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "MedSync",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider(thickness = 1.dp)

                    Spacer(Modifier.height(12.dp))

                    // Navigation items
                    val navItems = listOf(
                        Triple("Home", Icons.Default.Home, ROUTE_DASHBOARD),
                        Triple("Medical History", Icons.Outlined.Star, "medical_history"),
                        Triple("My Profile", Icons.Outlined.AccountCircle, ROUTE_PROFILE),
                        Triple("Medication", Icons.Outlined.FavoriteBorder, "medication")
                    )

                    navItems.forEach { (label, icon, route) ->
                        NavigationDrawerItem(
                            label = { Text(label) },
                            selected = false,
                            icon = { Icon(icon, contentDescription = label) },
                            onClick = {
                                scope.launch { drawerState.close() } // Close the drawer first

                                // Navigate only if not already on the destination
                                if (navController.currentDestination?.route != route) {
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    HorizontalDivider(thickness = 1.dp)

                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        label = { Text("Settings") },
                        selected = false,
                        icon = { Icon(Icons.Outlined.Settings, contentDescription = "Settings") },
                        onClick = { /* Handle click */ }
                    )
                    Spacer(Modifier.height(24.dp))
                }
            }
        },
        drawerState = drawerState
    )
    {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = "MedSync",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color.White
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) {
                                    drawerState.open()
                                } else {
                                    drawerState.close()
                                }
                            }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu",
                                tint = Color.White
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* TODO: Add profile or settings action */ }) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = "Profile",
                                tint = Color.White
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.shadow(4.dp)
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Bottom app bar",
                    )
                }
            },
//            floatingActionButton = {
//                FloatingActionButton(onClick = { presses++ }) {
//                    Icon(Icons.Default.Add, contentDescription = "Add")
//                }
//            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .background(Color.Cyan)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                SampleMedicine()
            }
        }
    }
}

@Preview
@Composable
private fun Medication_Preview() {
    Medication_Screen(rememberNavController())
}

@Composable
fun SampleMedicine(modifier: Modifier = Modifier) {
    Text(text = "Kuna dawa!")
}
