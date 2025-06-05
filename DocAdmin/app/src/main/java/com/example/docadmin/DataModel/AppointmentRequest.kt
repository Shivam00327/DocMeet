package com.example.docadmin.DataModel

data class AppointmentRequest(
    val requestId: String = "",
    val doctorEmail: String = "",
    val patientEmail: String = "",
    val message: String = "",
    val status: String = "", // PENDING, ACCEPTED, REJECTED, CANCELLED
    val timestamp: Long = 0,
    val notificationSent: Boolean = false,
    val notificationRead: Boolean = false,
    val responseTimestamp: Long = 0
)

data class Appointment(
    val requestId: String = "",
    val doctorEmail: String = "",
    val patientEmail: String = "",
    val message: String = "",
    val appointmentTime: Long = 0,
    val meetingId:String="",
    val status: String = "", // SCHEDULED, COMPLETED, CANCELLED
    val createdAt: Long = 0
)