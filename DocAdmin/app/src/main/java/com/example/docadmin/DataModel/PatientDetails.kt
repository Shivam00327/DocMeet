package com.example.docadmin.DataModel

data class PatientDetails(

    var patientName: String,
    var patientId: String="",
    var age: String,
    var disease: String,
    var lastMeetDate: String="",
    var upComingMeetDate: String=""
)