package com.example.docadmin.Repository

import com.example.docadmin.DataModel.PatientDetails
import com.example.docadmin.DataModel.doctorDetails
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore

class PatientRepository {
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
    private val patientCollection: CollectionReference = db.collection("patients")

    fun addPatient(
        name: String,
        age: String,
        disease: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        val user = PatientDetails(
            patientName = name,
            age = age,
            disease = disease

        )

        patientCollection.add(user)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
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
}