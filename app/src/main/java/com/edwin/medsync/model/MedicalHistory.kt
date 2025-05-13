package com.edwin.medsync.model

data class MedicalHistory(
    val historyEntryId: String = "", // Unique ID for the history entry
    val date: Long = System.currentTimeMillis(), // Timestamp for the entry
    val description: String = "", // Description of the diagnosis or treatment
    val doctorId: String = "", // Doctor's ID who made the entry
    val updatedBy: String = "doctor" // Who updated the history (should be doctor only)
)
