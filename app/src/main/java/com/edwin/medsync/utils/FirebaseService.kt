package com.edwin.medsync.utils

import android.util.Log
import com.edwin.medsync.model.Appointment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.edwin.medsync.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class FirebaseService {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val doctorsReference: DatabaseReference = database.reference.child("doctors")
    private val patientsReference: DatabaseReference = database.reference.child("patients")

    // Function to update doctor's profile in Firebase
    fun UpdateDoctorProfile(doctor: User, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val doctorRef = doctorsReference.child(userId)

        // Save updated doctor data
        doctorRef.setValue(doctor).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }
    fun UpdatePatientProfile(patient: User, onComplete: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val patientRef = patientsReference.child(userId)

        // Save updated patient data
        patientRef.setValue(patient).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onComplete(true)
            } else {
                onComplete(false)
            }
        }
    }
    fun getPatientProfile(userId: String, onResult: (User) -> Unit) {
        val db = FirebaseDatabase.getInstance().reference
        db.child("patients").child(userId).get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                onResult(user)
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    fun getDoctorProfile(userId: String, onResult: (User) -> Unit) {
        val db = FirebaseDatabase.getInstance().reference
        db.child("doctors").child(userId).get().addOnSuccessListener { snapshot ->
            val user = snapshot.getValue(User::class.java)
            if (user != null) {
                onResult(user)
            }
        }.addOnFailureListener {
            it.printStackTrace()
        }
    }

    fun getAppointmentsForPatient(patientId: String, onResult: (List<Appointment>) -> Unit) {
        val ref = FirebaseDatabase.getInstance().getReference("appointments")
        ref.orderByChild("patientId").equalTo(patientId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list = mutableListOf<Appointment>()
                    for (child in snapshot.children) {
                        val appt = child.getValue(Appointment::class.java)
                        appt?.let { list.add(it) }
                    }
                    onResult(list)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(emptyList())
                }
            })
    }

    fun getAppointmentsForDoctor(doctorId: String, callback: (List<Appointment>) -> Unit) {
        val ref = database.getReference("appointments")
        ref.orderByChild("doctorId").equalTo(doctorId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val appointments = snapshot.children.mapNotNull { it.getValue(Appointment::class.java) }
                    callback(appointments)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(emptyList())
                }
            })
    }

}