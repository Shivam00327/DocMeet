package com.example.docpatient.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import com.example.docpatient.Repository.DoctorRepository
import com.example.docpatient.Repository.PatientRepository
import com.example.docpatient.dataClass.AppointmentRequest
import com.example.docpatient.dataClass.AppointmentRequestState
import com.example.docpatient.dataClass.PatientDetails
import com.example.docpatient.navigation.Routes
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class PatientViewModel(private val patientRepository: PatientRepository) : ViewModel() {

    private val _patientProfile = MutableStateFlow<PatientDetails?>(null)
    val patientProfile: StateFlow<PatientDetails?> = _patientProfile.asStateFlow()

    private val _appointmentRequestState = MutableStateFlow<AppointmentRequestState>(AppointmentRequestState.Initial)
    val appointmentRequestState: StateFlow<AppointmentRequestState> = _appointmentRequestState

    // Patient's pending appointment requests
    private val _pendingRequests = MutableStateFlow<List<AppointmentRequest>>(emptyList())
    val pendingRequests: StateFlow<List<AppointmentRequest>> = _pendingRequests

    // Initialize if user is logged in
    init {
        val currentUser = FirebaseAuth.getInstance().currentUser
        currentUser?.email?.let { email ->
            fetchPatientProfile(email)
        }
    }

    fun createUser(name: String, age: String, phoneNumber: String, email: String,
                   password: String, disease: String, context: Context
    ) {
        patientRepository.addPatient(
            name, age, phoneNumber, disease, email, password,
            onSuccess = {
                Toast.makeText(
                    context,
                    "Patient added to Firebase Firestore",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onFailure = { e ->
                Toast.makeText(
                    context,
                    "Failed to add user: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    fun fetchPatientProfile(email: String) {
        patientRepository.getPatientByEmail(
            email,
            onSuccess = { user ->
                _patientProfile.value = user
            },
            onFailure = { e ->
                // Handle error
                Log.e("PatientViewModel", "Error fetching patient profile", e)
            }
        )
    }

    fun deletePatient(
        email: String,
        context: Context,
        navController: NavHostController,
        authViewModel: AuthViewModel
    ) {
        patientRepository.deletePatient(
            email,
            onSuccess = {
                authViewModel.deleteAuthAccount(
                    onSuccess = {
                        Toast.makeText(context, "Account fully deleted", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.HOME) { inclusive = true }
                        }
                    },
                    onFailure = { e ->
                        Toast.makeText(
                            context,
                            "Auth deletion failed: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            },
            onFailure = { e ->
                Toast.makeText(
                    context,
                    "Firestore deletion failed: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }

    fun updatePatientProfile(
        email: String,
        name: String? = null,
        age: String? = null,
        disease: String? = null,
        context: Context
    ) {
        // Implement update logic when needed
        // This is just a placeholder based on your commented code
        Toast.makeText(context, "Update Profile functionality not implemented yet", Toast.LENGTH_SHORT).show()
    }

    // Use coroutines to request an appointment with doctor notification
    fun requestAppointment(doctorEmail: String, patientEmail: String, message: String = "") {
        viewModelScope.launch {
            _appointmentRequestState.value = AppointmentRequestState.Loading

            try {
                // This will create the appointment request and send notification to the doctor
                // using your existing repository implementation
                val requestId = patientRepository.sendAppointmentRequest(
                    doctorEmail = doctorEmail,
                    patientEmail = patientEmail,
                    message = message,
                    timestamp = System.currentTimeMillis()
                )

                _appointmentRequestState.value = AppointmentRequestState.Success(requestId)

                // Refresh pending requests
                loadPendingRequests(patientEmail)

            } catch (e: Exception) {
                _appointmentRequestState.value = AppointmentRequestState.Error(
                    e.message ?: "Failed to send appointment request"
                )
                Log.e("PatientViewModel", "Error requesting appointment", e)
            }
        }
    }

    fun loadPendingRequests(patientEmail: String) {
        viewModelScope.launch {
            try {
                val requests = patientRepository.getPendingAppointmentRequests(patientEmail)
                _pendingRequests.value = requests
            } catch (e: Exception) {
                // Handle error
                Log.e("PatientViewModel", "Error loading pending requests", e)
            }
        }
    }



    fun cancelAppointmentRequest(requestId: String) {
        viewModelScope.launch {
            try {
                patientRepository.cancelAppointmentRequest(requestId)
                // Refresh pending requests
                _patientProfile.value?.email?.let { email ->
                    loadPendingRequests(email)
                }
            } catch (e: Exception) {
                // Handle error
                Log.e("PatientViewModel", "Error canceling request", e)
            }
        }
    }

    fun resetAppointmentRequestState() {
        _appointmentRequestState.value = AppointmentRequestState.Initial
    }
}