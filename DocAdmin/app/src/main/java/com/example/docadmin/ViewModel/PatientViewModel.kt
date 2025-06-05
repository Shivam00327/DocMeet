package com.example.docadmin.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.docadmin.DataModel.PatientDetails
import com.example.docadmin.DataModel.doctorDetails
import com.example.docadmin.Repository.PatientRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PatientViewModel(private val patientRepository: PatientRepository) :ViewModel(){

    private val _patientDetails = MutableStateFlow<PatientDetails?>(null)
    val patientDetails: StateFlow<PatientDetails?> = _patientDetails.asStateFlow()


    fun fetchUserProfile(email: String) {
        patientRepository.getPatientByEmail(
            email,
            onSuccess = { user ->
                _patientDetails.value = user
            },
            onFailure = {
                // Handle error
            }
        )
    }
}