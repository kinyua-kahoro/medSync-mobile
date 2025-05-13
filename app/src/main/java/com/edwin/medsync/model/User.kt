package com.edwin.medsync.model

// Extending the User model for doctors
class User {
    var email: String = ""
    var password: String = ""
    var userid: String = ""
    var fullName: String = ""
    var age: Int = 0
    var gender: String = ""
    var role: String = ""  // New property for the user role

    // Doctor-specific fields
    var specialty: String = ""
    var availability: List<String> = listOf()

    // Constructor for regular user
    constructor(email: String, password: String, userid: String, role: String) {
        this.email = email
        this.password = password
        this.userid = userid
        this.role = role
    }

    // Constructor for doctor
    constructor(email: String, password: String, userid: String, fullName: String, age: Int, gender: String, role: String, specialty: String, availability: List<String>) {
        this.email = email
        this.password = password
        this.userid = userid
        this.fullName = fullName
        this.age = age
        this.gender = gender
        this.role = role
        this.specialty = specialty
        this.availability = availability
    }

    // Default constructor
    constructor()

    // Optional methods for checking the user's role
    fun isAdmin(): Boolean {
        return role == "Admin"
    }

    fun isDoctor(): Boolean {
        return role == "Doctor"
    }

    fun isPatient(): Boolean {
        return role == "Patient"
    }
}
