package com.edwin.medsync.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.edwin.medsync.data.AuthViewModel
import com.edwin.medsync.model.Appointment
import com.edwin.medsync.ui.theme.screens.admin.Admin_Screen
import com.edwin.medsync.ui.theme.screens.dashboard.Dashboard_Screen
import com.edwin.medsync.ui.theme.screens.doctor.AppointmentManagementScreen
import com.edwin.medsync.ui.theme.screens.doctor.DoctorProfileUpdate
import com.edwin.medsync.ui.theme.screens.doctor.Doctor_Screen
import com.edwin.medsync.ui.theme.screens.doctor.MedicalHistoryUpdateScreen
import com.edwin.medsync.ui.theme.screens.doctor.PatientProfileScreen
import com.edwin.medsync.ui.theme.screens.home.Home_Screen
import com.edwin.medsync.ui.theme.screens.login.Login_Screen
import com.edwin.medsync.ui.theme.screens.medication.Medication_Screen
import com.edwin.medsync.ui.theme.screens.patient.AppointmentFormScreen
import com.edwin.medsync.ui.theme.screens.patient.BookAppointmentScreen
import com.edwin.medsync.ui.theme.screens.patient.MedicalHistoryScreen
import com.edwin.medsync.ui.theme.screens.patient.PatientProfileUpdate
import com.edwin.medsync.ui.theme.screens.patient.Patient_Screen
import com.edwin.medsync.ui.theme.screens.profile.ProfileScreen
import com.edwin.medsync.ui.theme.screens.register.Register_Screen
import com.edwin.medsync.utils.FirebaseService
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(modifier: Modifier = Modifier,
               navController: NavHostController= rememberNavController(),
               viewModel: AuthViewModel,
               startDestination: String=ROUTE_HOME) {
    val patientId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val historyEntryId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val appointmentId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    NavHost(modifier = modifier,
        navController = navController,
        startDestination = startDestination){
        composable(ROUTE_LOGIN){
            Login_Screen(navController)
        }
        composable(ROUTE_REGISTER){
            Register_Screen(navController)
        }
        composable (ROUTE_HOME){
            Home_Screen(navController)
        }
        composable (ROUTE_DASHBOARD){
            Dashboard_Screen(navController)
        }
        composable (ROUTE_PROFILE){
            ProfileScreen(navController,viewModel=viewModel)
        }
        composable (ROUTE_MEDICATION){
            Medication_Screen(navController)
        }
        composable (ROUTE_ADMIN){
            Admin_Screen(navController)
        }
        composable (ROUTE_DOCTOR){
            Doctor_Screen(navController, firebaseService = FirebaseService(), appointment = Appointment())
        }
        composable (ROUTE_PATIENT){
            Patient_Screen(navController, firebaseService = FirebaseService())
        }
        composable(
            route = "book_appointment/{patientId}",
            arguments = listOf(
                navArgument("patientId") { type = NavType.StringType },
            )
        ) { backStackEntry ->
            BookAppointmentScreen(
                patientId = patientId,
                navController = navController
            )
        }
        composable (ROUTE_DOCTOR_PROFILE){
            DoctorProfileUpdate(firebaseService = FirebaseService(),navController=navController)
        }
        composable("book_appointment/{doctorId}/{patientId}")
        { backStackEntry ->
            val doctorId = backStackEntry.arguments?.getString("doctorId") ?: ""
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            AppointmentFormScreen(doctorId, patientId, navController)
        }
        composable (ROUTE_APPOINTMENTS){
            AppointmentManagementScreen(navController)
        }
        composable (ROUTE_PATIENT_PROFILE){
            PatientProfileUpdate(firebaseService = FirebaseService(),navController=navController)
        }
        composable("medicalHistoryUpdateScreen/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            MedicalHistoryUpdateScreen(patientId = patientId, navController=navController, historyEntryId = historyEntryId, appointmentId = appointmentId) {
                navController.popBackStack() // Pop the back stack after submitting
            }
        }
        composable("patientProfileScreen/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            PatientProfileScreen(patientId = patientId, navController = navController)
        }
        composable("medical_history/{patientId}") { backStackEntry ->
            val patientId = backStackEntry.arguments?.getString("patientId") ?: ""
            MedicalHistoryScreen(patientId = patientId, navController = navController)
        }
    }
}