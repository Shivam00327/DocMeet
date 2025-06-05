package com.example.docpatient.viewModel

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.docpatient.Repository.DoctorListState

import com.example.docpatient.Repository.DoctorRepository
import com.example.docpatient.dataClass.Appointment
import com.example.docpatient.dataClass.DoctorDetails
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class DoctorViewModel(private val doctorRepository: DoctorRepository) : ViewModel() {
    private val _doctorProfile = MutableStateFlow<DoctorDetails?>(null)
    val doctorProfile: StateFlow<DoctorDetails?> = _doctorProfile.asStateFlow()

    private val _patientAppointments = MutableStateFlow<List<Appointment>>(emptyList())
    val patientAppointments: StateFlow<List<Appointment>> = _patientAppointments.asStateFlow()

    private val _isLoadingAppointments = MutableStateFlow(false)
    val isLoadingAppointments: StateFlow<Boolean> = _isLoadingAppointments.asStateFlow()

    private val _appointmentsError = MutableStateFlow<String?>(null)

    private val _doctorListState = MutableStateFlow<DoctorListState>(DoctorListState.Loading)
    val doctorListState: StateFlow<DoctorListState> = _doctorListState.asStateFlow()


    // For pagination
    private var lastVisibleDoctor: DocumentSnapshot? = null
    private var isLoadingMore = false
    private var hasMoreDoctors = true

    init {
        loadDoctors()
    }


    fun fetchDoctorProfile(email: String) {
        doctorRepository.getUserByEmail(
            email,
            onSuccess = { user ->
                _doctorProfile.value = user
            },
            onFailure = {
                // Handle error
            }
        )
    }




    fun loadDoctors() {
        _doctorListState.value = DoctorListState.Loading

        doctorRepository.getAllDoctors(
            onSuccess = { doctors ->
                _doctorListState.value = DoctorListState.Success(doctors)
            },
            onFailure = { exception ->
                _doctorListState.value = DoctorListState.Error(
                    exception.message ?: "Failed to load doctors"
                )
            }
        )
    }

    // Optional: Load doctors with pagination
    fun loadMoreDoctors() {
        if (isLoadingMore || !hasMoreDoctors) return

        isLoadingMore = true

        doctorRepository.getDoctorsWithPagination(
            lastVisibleDoctor = lastVisibleDoctor,
            pageSize = 10,
            onSuccess = { doctors, lastVisible ->
                isLoadingMore = false
                lastVisibleDoctor = lastVisible
                hasMoreDoctors = doctors.isNotEmpty() && lastVisible != null

                // Update the current list with new doctors
                val currentDoctors = (_doctorListState.value as? DoctorListState.Success)?.doctors ?: emptyList()
                _doctorListState.value = DoctorListState.Success(currentDoctors + doctors)
            },
            onFailure = { exception ->
                isLoadingMore = false
                _doctorListState.value = DoctorListState.Error(
                    exception.message ?: "Failed to load more doctors"
                )
            }
        )
    }

    fun fetchPatientAppointments(patientEmail: String) {
        viewModelScope.launch {
            _isLoadingAppointments.value = true
            _appointmentsError.value = null
            try {
                val appointments = doctorRepository.getPatientAppointments(patientEmail)
                _patientAppointments.value = appointments

            } catch (e: Exception) {
                _appointmentsError.value = "Failed to load appointments: ${e.message}"
            } finally {
                _isLoadingAppointments.value = false
            }
        }
    }



    fun refreshDoctors() {
        lastVisibleDoctor = null
        hasMoreDoctors = true
        loadDoctors()
    }


}

