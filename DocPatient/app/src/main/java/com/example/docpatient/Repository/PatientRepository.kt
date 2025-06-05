package com.example.docpatient.Repository

import android.util.Log
import com.example.docpatient.dataClass.AppointmentRequest
import com.example.docpatient.dataClass.PatientDetails
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class PatientRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val patientCollection: CollectionReference = db.collection("patients")

    fun addPatient(
        name: String,
        age: String,
        phoneNumber: String,
        disease: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = PatientDetails(
            patientName = name,
            age = age,
            phoneNumber = phoneNumber,
            disease = disease,
            email = email,
            password = password

        )

        patientCollection.add(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    fun deletePatient(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        patientCollection.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                documents.firstOrNull()?.reference?.delete()
                    ?.addOnSuccessListener { onSuccess() }
                    ?.addOnFailureListener(onFailure)
            }
            .addOnFailureListener(onFailure)
    }

    fun getPatientByEmail(email: String, onSuccess: (PatientDetails?) -> Unit, onFailure: (Exception) -> Unit) {
        patientCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val user = querySnapshot.documents.firstOrNull()?.toObject(PatientDetails::class.java)
                onSuccess(user)
            }
            .addOnFailureListener { onFailure(it) }
    }
    suspend fun sendAppointmentRequest(
        doctorEmail: String,
        patientEmail: String,
        message: String,
        timestamp: Long
    ): String {
        return suspendCoroutine { continuation ->
            // Create a unique ID for the request
            val requestId = db.collection("appointmentRequests").document().id

            val appointmentRequest = hashMapOf(
                "requestId" to requestId,
                "doctorEmail" to doctorEmail,
                "patientEmail" to patientEmail,
                "message" to message,
                "status" to "PENDING", // Initial status
                "timestamp" to timestamp,
                "notificationSent" to true, // Mark that notification needs to be sent
                "notificationRead" to false // Doctor hasn't read it yet
            )

            db.collection("appointmentRequests")
                .document(requestId)
                .set(appointmentRequest)
                .addOnSuccessListener {
                    // Also add to doctor's notifications collection
                    val notification = hashMapOf(
                        "type" to "APPOINTMENT_REQUEST",
                        "requestId" to requestId,
                        "patientEmail" to patientEmail,
                        "timestamp" to timestamp,
                        "read" to false
                    )

                    db.collection("doctors")
                        .document(doctorEmail)
                        .collection("notifications")
                        .document(requestId)
                        .set(notification)
                        .addOnSuccessListener {
                            continuation.resume(requestId)
                        }
                        .addOnFailureListener { e ->
                            // Even if notification fails, the request was created
                            Log.e("PatientRepository", "Failed to create notification", e)
                            continuation.resume(requestId)
                        }
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun getPendingAppointmentRequests(patientEmail: String): List<AppointmentRequest> {
        return suspendCoroutine { continuation ->
            db.collection("appointmentRequests")
                .whereEqualTo("patientEmail", patientEmail)
                .whereEqualTo("status", "PENDING")
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val requests = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(AppointmentRequest::class.java)
                    }
                    continuation.resume(requests)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }

    suspend fun cancelAppointmentRequest(requestId: String) {
        return suspendCoroutine { continuation ->
            db.collection("appointmentRequests")
                .document(requestId)
                .update("status", "CANCELLED")
                .addOnSuccessListener {
                    continuation.resume(Unit)
                }
                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }
}