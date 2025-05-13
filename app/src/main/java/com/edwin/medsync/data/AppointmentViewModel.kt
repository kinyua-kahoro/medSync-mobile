package com.edwin.medsync.data

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.edwin.medsync.model.Appointment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AppointmentViewModel : ViewModel() {

    private val database = FirebaseDatabase.getInstance().getReference("appointments")

    var appointmentsList = mutableStateListOf<Appointment>()
        private set

    fun bookAppointment(
        appointment: Appointment,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val database = FirebaseDatabase.getInstance().getReference("appointments")
        val key = FirebaseDatabase.getInstance().getReference("appointments").push().key
        appointment.appointmentId = key // This will set the generated key as the appointment ID

        if (key == null) {
            onFailure("Failed to generate appointment ID")
            return
        }

        appointment.appointmentId = key

        database.child(key).setValue(appointment)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it.message ?: "Error saving appointment") }
    }

    private val _appointments = MutableStateFlow<List<Appointment>>(emptyList())
    val appointments: StateFlow<List<Appointment>> = _appointments

    // Fetch appointments for the current doctor
    fun fetchAppointmentsForCurrentDoctor() {
        val doctorId = FirebaseAuth.getInstance().currentUser?.uid
        if (doctorId == null) {
            Log.e("Firebase", "Doctor is not authenticated")
            return
        }
        val ref = FirebaseDatabase.getInstance().getReference("appointments")
        ref.orderByChild("doctorId").equalTo(doctorId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("Firebase", "DataSnapshot: ${snapshot.value}")
                    val list = snapshot.children.mapNotNull { snap ->
                        val appointment = snap.getValue(Appointment::class.java)
                        appointment?.copy(appointmentId = snap.key)
                    }
                    _appointments.value = list
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Failed to load appointments: ${error.message}")
                }
            })
    }



    // Update appointment status (approve/reject)
    fun updateAppointmentStatus(appointmentId: String, status: String) {
        val ref = FirebaseDatabase.getInstance().getReference("appointments").child(appointmentId)
        ref.child("status").setValue(status)
            .addOnSuccessListener {
                Log.d("Firebase", "Appointment status updated successfully")
            }
            .addOnFailureListener { error ->
                Log.e("Firebase", "Failed to update appointment status: ${error.message}")
            }
    }
}
