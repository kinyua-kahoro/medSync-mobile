package com.edwin.medsync.model

data class Appointment(
    var appointmentId: String? = null,
    var patientId: String? = null,
    var doctorId: String? = null,
    var fullName: String? = null,
    var date: String? = null,
    var time: String? = null,
    var reason: String? = null,
    var status: String? = "pending"
)


