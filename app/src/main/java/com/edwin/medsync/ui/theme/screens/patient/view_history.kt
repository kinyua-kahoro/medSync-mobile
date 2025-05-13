package com.edwin.medsync.ui.theme.screens.patient

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.edwin.medsync.R
import com.edwin.medsync.data.MedicalHistoryViewModel
import com.edwin.medsync.model.Appointment
import com.edwin.medsync.model.MedicalHistory
import com.edwin.medsync.navigation.ROUTE_LOGIN
import com.edwin.medsync.navigation.ROUTE_PATIENT
import com.edwin.medsync.navigation.ROUTE_PATIENT_PROFILE
import com.edwin.medsync.ui.theme.screens.doctor.getPatientFullName
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalHistoryScreen(
    patientId: String,
    navController: NavController,
    viewModel: MedicalHistoryViewModel = viewModel()
) {
    val medicalHistoryState = viewModel.medicalHistory.collectAsState()
    val medicalHistoryList = medicalHistoryState.value

    // Assume you have a method to get appointment details for the patient
    val appointment = viewModel.getAppointmentForPatient(patientId) // Add this method in your ViewModel
    // Trigger to update medical history and mark appointment as complete
    val updatedMedicalHistory = MedicalHistory(description = "New description", date = System.currentTimeMillis())

    LaunchedEffect(patientId) {
        viewModel.fetchMedicalHistory(patientId)
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(24.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.favicon),
                            contentDescription = "App Icon",
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "MedSync",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Divider()
                    Spacer(Modifier.height(12.dp))

                    listOf(
                        Triple("Home", Icons.Default.Home, ROUTE_PATIENT),
                        Triple("Medical History", Icons.Outlined.Star, "medical_history/$patientId"),
                        Triple("My Profile", Icons.Outlined.AccountCircle, ROUTE_PATIENT_PROFILE),
                    ).forEach { (label, icon, route) ->
                        NavigationDrawerItem(
                            label = { Text(label) },
                            selected = false,
                            icon = { Icon(icon, contentDescription = label,tint = MaterialTheme.colorScheme.primary) },
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (navController.currentDestination?.route != route) {
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                    }
                                }
                            }
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 8.dp))

                    NavigationDrawerItem(
                        label = { Text("Logout", style = MaterialTheme.typography.bodyLarge) },
                        selected = false,
                        icon = { Icon(Icons.Default.ExitToApp, contentDescription = "Logout") },
                        onClick = {
                            FirebaseAuth.getInstance().signOut()
                            navController.navigate(ROUTE_LOGIN) {
                                popUpTo(0) // Clear backstack
                            }
                        },
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(Modifier.height(24.dp))
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MedSync") },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch {
                                if (drawerState.isClosed) drawerState.open()
                                else drawerState.close()
                            }
                        }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { padding ->
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(padding)) {
                if (medicalHistoryList.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    Log.d("MedicalHistory", "Fetched medical history: $medicalHistoryList")
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        items(items = medicalHistoryList) { history ->
                            // Pass both medical history and appointment to the card
                            MedicalHistoryCard(medicalHistory = history, appointment = appointment)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun MedicalHistoryCard(medicalHistory: MedicalHistory, appointment: Appointment) {
    val doctorName = appointment.doctorName ?: "Unknown Doctor"

    // Log the doctor name to verify it's being passed correctly
    Log.d("MedicalHistoryCard", "Doctor Name: $doctorName")

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(10.dp, shape = RoundedCornerShape(12.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            val formattedDate = remember(medicalHistory.date) {
                SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date(medicalHistory.date))
            }

            Text(
                text = medicalHistory.description,
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Date: $formattedDate",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                )
                Text(
                    text = "Doctor: $doctorName",
                    style = MaterialTheme.typography.bodyLarge.copy(color = Color.Gray)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Divider(modifier = Modifier.padding(vertical = 8.dp))

//               Button(
//                   onClick = {
//                       // Handle navigation to detailed view
//                   },
//                   modifier = Modifier.fillMaxWidth(),
//                   colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
//               ) {
//                   Text(
//                       text = "View Details",
//                       color = Color.White,
//                       style = MaterialTheme.typography.labelMedium
//                   )
//               }
        }
    }
}



//fun fetchDoctorName(doctorId: String, callback: (String) -> Unit) {
//    val db = FirebaseDatabase.getInstance().reference
//    db.child("doctors").child(doctorId.trim()).get()
//        .addOnSuccessListener { snapshot ->
//            Log.d("DoctorDebug", "Snapshot exists: ${snapshot.exists()}")
//            Log.d("DoctorDebug", "Children: ${snapshot.children.map { it.key }}")
//            val doctorName = snapshot.child("fullName").getValue(String::class.java)
//            Log.d("DoctorDebug", "Doctor name: $doctorName")
//            callback(doctorName ?: "Unknown Doctor")
//        }
//        .addOnFailureListener {
//            Log.e("DoctorDebug", "Failed to fetch doctor", it)
//            callback("Unknown Doctor")
//        }
//}



