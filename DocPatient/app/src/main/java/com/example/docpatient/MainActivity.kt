package com.example.docpatient

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.docpatient.Repository.DoctorRepository
import com.example.docpatient.Repository.PatientRepository
import com.example.docpatient.navigation.SetupNavGraph
import com.example.docpatient.ui.theme.DocPatientTheme
import com.example.docpatient.viewModel.AuthViewModel
import com.example.docpatient.viewModel.DoctorViewModel
import com.example.docpatient.viewModel.PatientViewModel

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel: AuthViewModel by viewModels()
        val doctorRepository= DoctorRepository()
        val doctorViewModel:DoctorViewModel by viewModels {
            doctorViewModelFactory(doctorRepository)
        }
        val patientRepository = PatientRepository() // Create repository first
        val patientViewModel: PatientViewModel by viewModels {
            patientViewModelFactory(patientRepository)
        }
        setContent {
            DocPatientTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding->
                    SetupNavGraph(modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        authViewModel,
                        patientViewModel,
                        doctorViewModel
                    )
                }
            }
        }
    }
}
class patientViewModelFactory(private val patientRepository: PatientRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PatientViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PatientViewModel(patientRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class doctorViewModelFactory(private val doctorRepository: DoctorRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DoctorViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DoctorViewModel(doctorRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

