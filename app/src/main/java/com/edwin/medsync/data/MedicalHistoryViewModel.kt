package com.edwin.medsync.data

import android.util.Log
import com.edwin.medsync.model.MedicalHistory
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MedicalHistoryViewModel : ViewModel() {

    private val _medicalHistory = MutableStateFlow<List<MedicalHistory>>(emptyList())
    val medicalHistory: StateFlow<List<MedicalHistory>> = _medicalHistory

    // Fetch medical history from Firebase and update the state
    fun fetchMedicalHistory(patientId: String) {
        viewModelScope.launch {
            try {
                val db = FirebaseDatabase.getInstance().reference
                db.child("patients").child(patientId).child("medical_history")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val medicalHistoryList = snapshot.children.mapNotNull {
                            it.getValue(MedicalHistory::class.java)
                        }
                        // Update the state with the fetched medical history
                        _medicalHistory.value = medicalHistoryList
                    }
                    .addOnFailureListener { exception ->
                        Log.e("MedicalHistoryViewModel", "Failed to fetch medical history", exception)
                    }
            } catch (e: Exception) {
                Log.e("MedicalHistoryViewModel", "Error fetching medical history", e)
            }
        }
    }

    // Add a new medical history entry to Firebase
    fun addMedicalHistoryEntry(patientId: String, medicalHistory: MedicalHistory) {
        val db = FirebaseDatabase.getInstance().reference
        val medicalHistoryRef = db.child("patients").child(patientId).child("medical_history").push()

        medicalHistoryRef.setValue(medicalHistory)
            .addOnSuccessListener {
                Log.d("MedicalHistoryViewModel", "Medical history added successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("MedicalHistoryViewModel", "Failed to add medical history", exception)
            }
    }
}