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



class DoctorViewModel : ViewModel() {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().reference
    private val _doctors = mutableStateOf<List<User>>(emptyList())
    val doctors: State<List<User>> = _doctors

    // Function to save a doctor to Firebase
    fun saveDoctorToFirebase(doctor: User) {
        val database = FirebaseDatabase.getInstance().reference
        val doctorRef = database.child("doctors").child(doctor.userid)  // Unique ID for each doctor

        doctorRef.setValue(doctor)
            .addOnSuccessListener {
                Log.d("Firebase", "Doctor saved successfully!")
            }
            .addOnFailureListener {
                Log.e("Firebase", "Failed to save doctor: ${it.message}")
            }
    }

    // Function to fetch doctors once from Firebase
    fun fetchDoctors() {
        println("Fetching doctors...")  // Debugging
        FirebaseDatabase.getInstance().reference.child("doctors").get().addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val doctorsList = snapshot.children.map {
                    it.getValue(User::class.java)!!
                }
                _doctors.value = doctorsList
                println("Doctors fetched from Firebase: ${_doctors.value}")  // Debugging
            } else {
                println("No doctors found.")
            }
        }.addOnFailureListener {
            println("Error fetching doctors: ${it.message}")
        }
    }
    // Function to fetch doctors in real-time
    fun getDoctors() {
        val doctorRef = database.child("doctors")  // Assuming doctors are under a "doctors" node in Firebase
        doctorRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val doctorsList = mutableListOf<User>()
                for (doctorSnapshot in snapshot.children) {
                    val doctor = doctorSnapshot.getValue(User::class.java)
                    if (doctor != null) {
                        doctorsList.add(doctor)
                    }
                }
                _doctors.value = doctorsList
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase", "Error fetching doctors: ${error.message}")
            }
        })
    }
}