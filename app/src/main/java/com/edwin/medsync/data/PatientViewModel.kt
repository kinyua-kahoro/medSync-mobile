package com.edwin.medsync.data

import android.util.Log
import androidx.lifecycle.ViewModel
import com.edwin.medsync.model.User
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class PatientViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val _patients = mutableStateOf<List<User>>(emptyList())
    val patients: State<List<User>> = _patients

    // Function to save a patient to Firebase
    fun savePatientToFirebase(patient: User) {
        val database = FirebaseDatabase.getInstance().reference
        val patientRef = database.child("patients").child(patient.userid)  // Unique ID for each patient

        patientRef.setValue(patient)
            .addOnSuccessListener {
                Log.d("Firebase", "Patient saved successfully!")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to save patient: ${it.message}")
            }
    }

    // Function to fetch patients once from Firebase
    fun fetchPatients() {
        println("Fetching patients...")  // Debugging
        FirebaseDatabase.getInstance().reference.child("patients").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val patientsList = snapshot.children.map {
                    it.getValue(User::class.java)!!
                }
                _patients.value = patientsList
                println("Patients fetched from Firebase: ${_patients.value}")  // Debugging
            } else {
                println("No patients found.")
            }
        }.addOnFailureListener {
            println("Error fetching patients: ${it.message}")
        }
    }
    // Function to fetch patients in real-time
    fun getPatients() {
        val patientRef = database.child("patients")  // Assuming patients are under a "patients" node in Firebase
        patientRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val patientsList = mutableListOf<User>()
                for (patientSnapshot in snapshot.children) {
                    val patient = patientSnapshot.getValue(User::class.java)
                    if (patient != null) {
                        patientsList.add(patient)
                    }
                }
                _patients.value = patientsList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching patients: ${error.message}")
            }
        })
    }
}