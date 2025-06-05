package com.example.docadmin.ViewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docadmin.DataModel.Appointment
import com.example.docadmin.DataModel.AppointmentRequest
import com.example.docadmin.DataModel.doctorDetails
import com.example.docadmin.Repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class UserViewModel(private val userRepository: UserRepository) : ViewModel() {
    private val _userProfile = MutableStateFlow<doctorDetails?>(null)
    val userProfile: StateFlow<doctorDetails?> = _userProfile.asStateFlow()

    // Add new StateFlows for appointment requests and appointments
    private val _pendingRequests = MutableStateFlow<List<AppointmentRequest>>(emptyList())
    val pendingRequests: StateFlow<List<AppointmentRequest>> = _pendingRequests.asStateFlow()

    private val _doctorAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val doctorAppointments: StateFlow<List<Appointment>> = _doctorAppointments.asStateFlow()

    // Add loading states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Add result state
    private val _operationResult = MutableStateFlow<String?>(null)
    val operationResult: StateFlow<String?> = _operationResult.asStateFlow()

    fun createUser(name: String, age: String, phoneNumber: String, email: String,
                   password: String, context: Context
    ) {
        userRepository.addUser(
            name, age, phoneNumber, email, password,
            onSuccess = {
                Toast.makeText(
                    context,
                    "User added to Firebase Firestore",
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

    fun fetchUserProfile(email: String) {
        userRepository.getUserByEmail(
            email,
            onSuccess = { user ->
                _userProfile.value = user
            },
            onFailure = {
                // Handle error
            }
        )
    }

    fun updateUserProfile(
        email: String,
        name: String? = null,
        age: String? = null,
        qualification: String? = null,
        context: Context
    ) {
        val updatedFields = mutableMapOf<String, Any>()
        name?.let { updatedFields["name"] = it }
        age?.let { updatedFields["age"] = it }
        qualification?.let { updatedFields["qualification"] = it }

        userRepository.updateUserProfile(
            email,
            updatedFields,
            onSuccess = {
                Toast.makeText(context, "Profile Updated", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(context, "Update Failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun updateProfilePicture(email: String, imageUri: Uri, context: Context) {
        userRepository.updateProfilePicture(
            email,
            imageUri,
            onSuccess = { url ->
                Toast.makeText(context, "Profile picture updated", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    fun deleteUser(email: String, context: Context) {
        userRepository.deleteUser(
            email,
            onSuccess = {
                Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
            },
            onFailure = { e ->
                Toast.makeText(context, "Delete failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // New functions for appointment handling

    fun fetchPendingRequests(doctorEmail: String) {

        viewModelScope.launch {

            _isLoading.value = true
            try {
                val requests = userRepository.getDoctorPendingRequests(doctorEmail)

                _pendingRequests.value = requests
                Log.d("UserViewModel", "Pending requests: $requests")
            } catch (e: Exception) {
                Log.e("UserViewModel", "Error fetching requests: ${e.message}", e)
                _operationResult.value = "Failed to load requests: ${e.message}"
            } finally {
                _isLoading.value = false

            }
        }
    }

    // for now even after adding the appointment this isnot working
    fun fetchDoctorAppointments(doctorEmail: String) {

        viewModelScope.launch {

            _isLoading.value = true
            try {
                Log.d("UserViewModel", "Fetching appointments for doctor: $doctorEmail")
                val appointments = userRepository.getDoctorAppointments(doctorEmail)
                _doctorAppointments.value = appointments
                Log.d("UserViewModel", "Fetched appointments: $appointments")
            } catch (e: Exception) {
                _operationResult.value = "Failed to load appointments: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun handleAppointmentRequest(
        requestId: String,
        doctorEmail: String,
        response: String, // "ACCEPTED", "REJECTED"
        appointmentTime: Long? = null,
        context: Context,
        meetingId:String
    ) {

        viewModelScope.launch {
            _isLoading.value = true
            try {
                Log.d("UserViewModel", "Handling request: $response")
                userRepository.receiveAppointmentRequest(
                    requestId = requestId,
                    doctorEmail = doctorEmail,
                    response = response,
                    appointmentTime = appointmentTime,
                    meetingId=meetingId,
                    onSuccess = {
                        val message = if (response == "ACCEPTED")
                            "Appointment scheduled successfully"
                        else
                            "Appointment request declined"

                        _operationResult.value = message
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()

                        // Refresh the pending requests
                        fetchPendingRequests(doctorEmail)

                        // If accepted, also refresh appointments
                        Log.d("UserViewModel", "Response: $response")
                        if (response == "ACCEPTED") {
                            fetchDoctorAppointments(doctorEmail)
                        }
                    },
                    onFailure = { e ->
                        val errorMsg = "Failed to process request: ${e.message}"
                        _operationResult.value = errorMsg
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    }
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Reset operation result (call after handling the result in UI)
    fun resetOperationResult() {
        _operationResult.value = null
    }
}
