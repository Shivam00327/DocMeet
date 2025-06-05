package com.example.docpatient.Repository

import android.util.Log
import com.example.docpatient.dataClass.Appointment
import com.example.docpatient.dataClass.AppointmentRequest
import com.example.docpatient.dataClass.DoctorDetails
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DoctorRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val userCollection: CollectionReference = db.collection("User")
    private val slotsCollection:CollectionReference = db.collection("appointment_slots")

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
        val user = DoctorDetails(
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

    fun getUserByEmail(email: String, onSuccess: (DoctorDetails?) -> Unit, onFailure: (Exception) -> Unit) {
        userCollection
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val user = querySnapshot.documents.firstOrNull()?.toObject(DoctorDetails::class.java)
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
    suspend fun getDoctorSlots(doctorEmail: String): List<AppointmentRequest> {
        return try {
            slotsCollection
                .whereEqualTo("doctorEmail", doctorEmail)
                .get()
                .await()
                .toObjects(AppointmentRequest::class.java)
        } catch (e: Exception) {
            throw e
        }
    }



    fun getAllDoctors(onSuccess: (List<DoctorDetails>) -> Unit, onFailure: (Exception) -> Unit) {
        userCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val doctorsList = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(DoctorDetails::class.java)
                }
                onSuccess(doctorsList)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    // Optional: Get doctors with pagination
    fun getDoctorsWithPagination(
        lastVisibleDoctor: DocumentSnapshot? = null,
        pageSize: Long = 10,
        onSuccess: (List<DoctorDetails>, DocumentSnapshot?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        var query = userCollection.orderBy("name").limit(pageSize)

        lastVisibleDoctor?.let {
            query = query.startAfter(it)
        }

        query.get()
            .addOnSuccessListener { querySnapshot ->
                val doctors = querySnapshot.documents.mapNotNull {
                    it.toObject(DoctorDetails::class.java)
                }
                val lastVisible = querySnapshot.documents.lastOrNull()
                onSuccess(doctors, lastVisible)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
    suspend fun getPatientAppointments(patientEmail: String): List<Appointment> {

        return suspendCoroutine { continuation ->

            db.collection("appointments")
                .whereEqualTo("patientEmail", patientEmail)
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

sealed class DoctorListState {
    object Loading : DoctorListState()
    data class Success(val doctors: List<DoctorDetails>) : DoctorListState()
    data class Error(val message: String) : DoctorListState()
}