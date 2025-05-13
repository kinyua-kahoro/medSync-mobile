package com.edwin.medsync.ui.theme.screens.doctor

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.edwin.medsync.R
import com.edwin.medsync.model.MedicalHistory
import com.edwin.medsync.navigation.ROUTE_APPOINTMENTS
import com.edwin.medsync.navigation.ROUTE_DOCTOR
import com.edwin.medsync.navigation.ROUTE_DOCTOR_PROFILE
import com.edwin.medsync.navigation.ROUTE_LOGIN
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicalHistoryUpdateScreen(
    navController: NavController,
    patientId: String,
    onBack: () -> Unit
) {
    var diagnosis by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var additionalNotes by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // Validation check for empty fields
    val isFormValid = diagnosis.isNotEmpty() && treatment.isNotEmpty()

    // Function to handle submitting the medical history update
    fun submitMedicalHistory() {
        if (isFormValid) {
            // Create a MedicalHistory object
            val medicalHistory = MedicalHistory(
                historyEntryId = UUID.randomUUID().toString(),
                date = System.currentTimeMillis(),
                description = "Diagnosis: $diagnosis, Treatment: $treatment, Notes: $additionalNotes",
                doctorId = FirebaseAuth.getInstance().currentUser?.uid ?: "unknown", // This should be the doctor's ID from the authenticated user
                updatedBy = "doctor"
            )

            // Set the submission state
            isSubmitting = true

            // Call a function to add the medical history entry to Firebase
            addMedicalHistoryEntry(patientId, medicalHistory) {
            isSubmitting = false // Reset the isSubmitting flag after the operation
            }
        }
    }
    ModalNavigationDrawer(
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Spacer(Modifier.height(24.dp))

                    // Logo and App Name
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
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
                            text = "Update Medical History",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    HorizontalDivider()
                    Spacer(Modifier.height(12.dp))

                    val navItems = listOf(
                        Triple("Dashboard", Icons.Default.Home, ROUTE_DOCTOR),
                        Triple("Patients", Icons.Default.AccountCircle, ROUTE_APPOINTMENTS),
                        Triple("My Profile", Icons.Default.AccountCircle, ROUTE_DOCTOR_PROFILE)
                    )

                    navItems.forEach { (label, icon, route) ->
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium
                                )
                            },
                            selected = false,
                            icon = {
                                Icon(
                                    icon,
                                    contentDescription = label,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            onClick = {
                                scope.launch { drawerState.close() }
                                if (navController.currentDestination?.route != route) {
                                    navController.navigate(route) {
                                        launchSingleTop = true
                                        restoreState = true
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                    }
                                }
                            },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider()
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
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        },
        drawerState = drawerState
    ){
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
                                if (drawerState.isClosed) drawerState.open() else drawerState.close()
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
                        IconButton(onClick = { /* Future Profile Settings */ }) {
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
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Update Medical History",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        elevation = CardDefaults.cardElevation(6.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            OutlinedTextField(
                                value = diagnosis,
                                onValueChange = { diagnosis = it },
                                label = { Text("Diagnosis") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = diagnosis.isEmpty(),
                                singleLine = false
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = treatment,
                                onValueChange = { treatment = it },
                                label = { Text("Treatment") },
                                modifier = Modifier.fillMaxWidth(),
                                isError = treatment.isEmpty(),
                                singleLine = false
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = additionalNotes,
                                onValueChange = { additionalNotes = it },
                                label = { Text("Additional Notes (Optional)") },
                                modifier = Modifier.fillMaxWidth(),
                                maxLines = 3
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit button with loading state
                    Button(
                        onClick = { submitMedicalHistory() },
                        enabled = isFormValid && !isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Submitting...")
                        } else {
                            Text("Submit", fontSize = 16.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Back button as an outlined button
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Back to Patient Profile")
                    }
                }
            }
        }
    }
}

// Add medical history to Firebase
fun addMedicalHistoryEntry(patientId: String, medicalHistory: MedicalHistory, onComplete: () -> Unit) {
    val db = FirebaseDatabase.getInstance().reference
    val medicalHistoryRef = db.child("patients").child(patientId).child("medical_history").push()
    medicalHistoryRef.setValue(medicalHistory).addOnCompleteListener {
        if (it.isSuccessful) {
            // Handle success (e.g., show a Toast or navigate back)
            onComplete() // Call the callback when submission is successful
        } else {
            // Handle error
            onComplete() // Reset the submission state in case of failure
        }
    }
}
