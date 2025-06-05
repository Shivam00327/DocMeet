package com.example.docpatient.dataClass

data class AppointmentRequest(
    val requestId: String = "",
    val doctorEmail: String = "",
    val patientEmail: String = "",
    val message: String = "",
    val status: String = "", // PENDING, APPROVED, REJECTED, CANCELLED
    val timestamp: Long = 0,
    val notificationSent: Boolean = false,
    val notificationRead: Boolean = false,
    // These fields will be set by the doctor when approving
    val appointmentDate: String? = null,
    val appointmentTime: String? = null
)

data class Appointment(
    val requestId: String = "",
    val doctorEmail: String = "",
    val patientEmail: String = "",
    val message: String = "",
    val appointmentTime: Long = 0,
    val meetingId: String = "",
    val status: String = "", // SCHEDULED, COMPLETED, CANCELLED
    val createdAt: Long = 0
)

sealed class AppointmentRequestState {
    object Initial : AppointmentRequestState()
    object Loading : AppointmentRequestState()
    data class Success(val requestId: String) : AppointmentRequestState()
    data class Error(val message: String) : AppointmentRequestState()
}