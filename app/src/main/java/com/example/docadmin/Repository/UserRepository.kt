package com.example.docadmin.Repository

import android.net.Uri
import android.util.Log
import com.example.docadmin.DataModel.Appointment
import com.example.docadmin.DataModel.AppointmentRequest
import com.example.docadmin.DataModel.doctorDetails
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class UserRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCollection: CollectionReference = db.collection("User")

    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference.child("profile_pictures")





    fun addUser(
        name: String,
        age: String,
        phoneNumber: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = doctorDetails(
            name = name,
            age = age,
            phNo = phoneNumber,
            email = email,
            password = password
        )

        userCollection.add(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun getUserByEmail(email: String, onSuccess: (doctorDetails?) -> Unit, onFailure: (Exception) -> Unit) {
        userCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val user = querySnapshot.documents.firstOrNull()?.toObject(doctorDetails::class.java)
                onSuccess(user)
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateUserProfile(
        email: String,
        updatedFields: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val document = querySnapshot.documents.firstOrNull()
                document?.let {
                    userCollection.document(it.id)
                        .update(updatedFields)
                        .addOnSuccessListener { onSuccess() }
                        .addOnFailureListener { onFailure(it) }
                } ?: onFailure(Exception("User not found"))
            }
            .addOnFailureListener { onFailure(it) }
    }
    fun deleteUser(
        email: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        userCollection.whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                documents.firstOrNull()?.reference?.delete()
                    ?.addOnSuccessListener { onSuccess() }
                    ?.addOnFailureListener(onFailure)
            }
            .addOnFailureListener(onFailure)
    }
    fun updateProfilePicture(
        email: String,
        imageUri: Uri,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val imageRef = storage.reference.child("profile_pictures/${email}_${System.currentTimeMillis()}.jpg")

        imageRef.putFile(imageUri)
            .addOnSuccessListener { taskSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    userCollection.whereEqualTo("email", email)
                        .get()
                        .addOnSuccessListener { documents ->
                            val userDoc = documents.firstOrNull()?.reference
                            userDoc?.update("profilePicture", downloadUrl.toString())
                                ?.addOnSuccessListener { onSuccess(downloadUrl.toString()) }
                                ?.addOnFailureListener(onFailure)
                        }
                        .addOnFailureListener(onFailure)
                }
            }
            .addOnFailureListener(onFailure)

    }

    suspend fun receiveAppointmentRequest(
        requestId: String,
        doctorEmail: String,
        response: String, // "ACCEPTED", "REJECTED"
        appointmentTime: Long? = null, // Timestamp for when appointment is scheduled (if accepted)
        meetingId:String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            // Get the request details first
            val requestDoc = db.collection("appointmentRequests").document(requestId).get().await()
            val request = requestDoc.toObject(AppointmentRequest::class.java)
                ?: throw Exception("Appointment request not found")

            if (request.doctorEmail != doctorEmail) {
                throw Exception("This appointment request is not for this doctor")
            }

            // Update the request status
            val updateFields = mutableMapOf<String, Any>(
                "status" to response,
                "responseTimestamp" to System.currentTimeMillis()
            )

            // If accepting, add appointment time
            if (response == "ACCEPTED" && appointmentTime != null) {
                updateFields["appointmentTime"] = appointmentTime
            }

            // Update the request document
            db.collection("appointmentRequests")
                .document(requestId)
                .update(updateFields)
                .await()

            // Mark notification as read
            db.collection("doctors")
                .document(doctorEmail)
                .collection("notifications")
                .document(requestId)
                .update("read", true)
                .await()

            // If accepted, create an appointment in a separate collection
            if (response == "ACCEPTED" && appointmentTime != null) {
                val appointment = hashMapOf(
                    "requestId" to requestId,
                    "doctorEmail" to doctorEmail,
                    "patientEmail" to request.patientEmail,
                    "message" to request.message,
                    "appointmentTime" to appointmentTime,
                    "meetingId" to meetingId,
                    "status" to "SCHEDULED",
                    "createdAt" to System.currentTimeMillis()
                )

                // Create the appointment
                db.collection("appointments")
                    .document(requestId) // Using the same ID for easy reference
                    .set(appointment)
                    .await()

                // Also notify the patient about the accepted appointment
//                val patientNotification = hashMapOf(
//                    "type" to "APPOINTMENT_ACCEPTED",
//                    "requestId" to requestId,
//                    "doctorEmail" to doctorEmail,
//                    "appointmentTime" to appointmentTime,
//                    "timestamp" to System.currentTimeMillis(),
//                    "read" to false
//                )
//
//                db.collection("patients")
//                    .document(request.patientEmail)
//                    .collection("notifications")
//                    .document(requestId)
//                    .set(patientNotification)
//                    .await()
//            } else if (response == "REJECTED") {
//                // Notify patient about rejection
//                val patientNotification = hashMapOf(
//                    "type" to "APPOINTMENT_REJECTED",
//                    "requestId" to requestId,
//                    "doctorEmail" to doctorEmail,
//                    "timestamp" to System.currentTimeMillis(),
//                    "read" to false
//                )
//
//                db.collection("patients")
//                    .document(request.patientEmail)
//                    .collection("notifications")
//                    .document(requestId)
//                    .set(patientNotification)
//                    .await()
            }

            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    suspend fun getDoctorPendingRequests(doctorEmail: String): List<AppointmentRequest> {
        return suspendCoroutine { continuation ->
            db.collection("appointmentRequests")
                .whereEqualTo("doctorEmail", doctorEmail)
                .whereEqualTo("status", "PENDING")
                .orderBy("timestamp", Query.Direction.DESCENDING)
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

    suspend fun getDoctorAppointments(doctorEmail: String): List<Appointment> {

        return suspendCoroutine { continuation ->

            db.collection("appointments")
                .whereEqualTo("doctorEmail", doctorEmail)
                .whereEqualTo("status", "SCHEDULED")
                //.orderBy("appointmentTime", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val appointments = querySnapshot.documents.mapNotNull { doc ->
                        doc.toObject(Appointment::class.java)
                    }
                    continuation.resume(appointments)
                    Log.d("mViewModel", "Fetched appointments: $appointments")
                }

                .addOnFailureListener { e ->
                    continuation.resumeWithException(e)
                }
        }
    }


}