package com.edwin.medsync.data

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import com.edwin.medsync.model.User
import com.edwin.medsync.navigation.ROUTE_HOME
import com.edwin.medsync.navigation.ROUTE_LOGIN
import com.edwin.medsync.navigation.ROUTE_REGISTER


class AuthViewModel : ViewModel() {
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signup(
        email: String,
        password: String,
        confirmPassword: String,
        role: String,
        context: Context,
        navController: NavHostController
    ) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            Toast.makeText(context, "Email and password cannot be blank", Toast.LENGTH_LONG).show()
            return
        } else if (password != confirmPassword) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_LONG).show()
            return
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    val uid = mAuth.currentUser!!.uid
                    val userdata = User(email, password, uid, role)
                    val regRef = FirebaseDatabase.getInstance().getReference("Users/$uid")

                    regRef.setValue(userdata).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Registered Successfully", Toast.LENGTH_LONG).show()

                            // Check if the user is a doctor and save doctor-specific data
                            if (role == "Doctor") {
                                val doctor = User(
                                    email = email,
                                    password = password,
                                    userid = uid,
                                    role = "Doctor",
                                    )
                                // Save doctor data to the "doctors" node in Firebase
                                FirebaseDatabase.getInstance().getReference("doctors/$uid").setValue(doctor)
                                    .addOnCompleteListener { doctorTask ->
                                        if (doctorTask.isSuccessful) {
                                            Toast.makeText(context, "Doctor data saved", Toast.LENGTH_LONG).show()
                                        } else {
                                            Toast.makeText(context, "Failed to save doctor data", Toast.LENGTH_LONG).show()
                                        }
                                    }
                            }

                            // Fetch the role after successful registration
                            regRef.child("role").addListenerForSingleValueEvent(object : ValueEventListener {
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    val savedRole = snapshot.getValue(String::class.java) ?: "Patient"
                                    navigateByRole(savedRole, navController)
                                }

                                override fun onCancelled(error: DatabaseError) {
                                    Toast.makeText(context, "Failed to retrieve role", Toast.LENGTH_LONG).show()
                                    navController.navigate(ROUTE_LOGIN)
                                }
                            })
                        } else {
                            Toast.makeText(context, "${task.exception!!.message}", Toast.LENGTH_LONG).show()
                            navController.navigate(ROUTE_REGISTER)
                        }
                    }
                } else {
                    Toast.makeText(context, "${it.exception!!.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }


    fun login(email: String, password: String, context: Context, navController: NavHostController) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val userId = mAuth.currentUser?.uid

                if (userId != null) {
                    val database = FirebaseDatabase.getInstance().reference
                    database.child("Users").child(userId).child("role")
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val role = snapshot.getValue(String::class.java) ?: "Patient"
                                navigateByRole(role, navController)
                                Toast.makeText(context, "Successfully Logged in", Toast.LENGTH_LONG).show()
                            }

                            override fun onCancelled(error: DatabaseError) {
                                Toast.makeText(context, "Failed to retrieve role", Toast.LENGTH_LONG).show()
                            }
                        })
                }
            } else {
                Toast.makeText(context, "Login failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                navController.navigate(ROUTE_LOGIN)
            }
        }
    }

    fun logout(navController: NavHostController) {
        mAuth.signOut()
        navController.navigate(ROUTE_LOGIN)
    }

    fun isLoggedIn(): Boolean {
        return mAuth.currentUser != null
    }

    fun updateProfile(context: Context, navController: NavHostController, fullName: String, age: Int, gender: String) {
        val uid = mAuth.currentUser?.uid
        if (uid != null) {
            val profileUpdates = mapOf(
                "fullName" to fullName,
                "age" to age,
                "gender" to gender
            )
            val regRef = FirebaseDatabase.getInstance().getReference("Users/$uid")
            regRef.updateChildren(profileUpdates).addOnCompleteListener {
                if (it.isSuccessful) {
                    Toast.makeText(context, "Profile updated", Toast.LENGTH_LONG).show()
                    navController.navigate(ROUTE_HOME)
                } else {
                    Toast.makeText(context, "Update failed: ${it.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun navigateByRole(role: String, navController: NavHostController) {
        when (role) {
            "Patient" -> navController.navigate("patient")
            "Doctor" -> navController.navigate("doctor")
            "Admin" -> navController.navigate("admin")
            else -> navController.navigate(ROUTE_HOME)
        }
    }
}
