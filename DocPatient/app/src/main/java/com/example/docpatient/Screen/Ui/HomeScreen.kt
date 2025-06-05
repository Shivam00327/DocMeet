package com.example.docpatient.Screen.Ui

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.docpatient.Repository.DoctorListState
import com.example.docpatient.Screen.MainBottomNavScreen
import com.example.docpatient.dataClass.Appointment
import com.example.docpatient.dataClass.AppointmentRequest
import com.example.docpatient.dataClass.AppointmentRequestState
import com.example.docpatient.dataClass.DoctorDetails
import com.example.docpatient.navigation.Routes.DOCTORDETAILS
import com.example.docpatient.viewModel.AuthViewModel
import com.example.docpatient.viewModel.DoctorViewModel
import com.example.docpatient.viewModel.PatientViewModel

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    patientViewModel: PatientViewModel,
    doctorViewModel: DoctorViewModel

) {
    val context = LocalContext.current
    //val doctorViewModel: DoctorViewModel= DoctorViewModel(DoctorRepository())
    val doctorListState by doctorViewModel.doctorListState.collectAsState()
    val patientProfile by patientViewModel.patientProfile.collectAsState()
    val doctorProfile by doctorViewModel.doctorProfile.collectAsState()
    val appointments by doctorViewModel.patientAppointments.collectAsState()
    val patientEmail = authViewModel.getCurrentUserEmail()

    LaunchedEffect(patientEmail) {
        if (patientEmail != null) {
            doctorViewModel.fetchPatientAppointments(patientEmail)
        }
    }





    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var disease by remember { mutableStateOf("") }


    LaunchedEffect(patientProfile) {
        patientProfile?.let {
            name = it.patientName
            email = it.email
            age = it.age
            disease = it.disease
        }
    }




    Scaffold(
        bottomBar = { MainBottomNavScreen(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // Welcome Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Welcome Back,",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .clickable {
                            patientViewModel.deletePatient(
                                email = authViewModel.getCurrentUserEmail()!!,
                                context,
                                navController,
                                authViewModel
                            )
                        }
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "Profile",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Doctors Section Title with count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Available Doctors",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                if (doctorListState is DoctorListState.Success) {
                    val count = (doctorListState as DoctorListState.Success).doctors.size
                    Text(
                        "Total: $count",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Doctors List with loading and error states
            when (doctorListState) {
                is DoctorListState.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                is DoctorListState.Success -> {
                    val doctors = (doctorListState as DoctorListState.Success).doctors

                    if (doctors.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "No doctors available",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(doctors) { doctor ->
                                DoctorCard(
                                    context = context,
                                    doctor = doctor,
                                    modifier = Modifier.clickable {
                                        // Handle doctor selection
                                        // You can navigate to doctor details screen here
                                        // navController.navigate("doctor_details/${doctor.email}")
                                    },
                                    doctorViewModel,
                                    patientViewModel,
                                    navController,
                                    appointments
                                )
                            }
                        }
                    }
                }

                is DoctorListState.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                (doctorListState as DoctorListState.Error).message,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Button(
                                onClick = { doctorViewModel.loadDoctors() }
                            ) {
                                Text("Retry")
                            }
                        }
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorCard(
    context: Context,
    doctor: DoctorDetails,
    modifier: Modifier = Modifier,
    doctorViewModel: DoctorViewModel,
    patientViewModel: PatientViewModel,
    navController: NavHostController,
    appointments: List<Appointment>
) {
    val scope = rememberCoroutineScope()

    var showBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )


    // Get current request state
    val requestState by patientViewModel.appointmentRequestState.collectAsState()

    // Handle request state changes
    LaunchedEffect(requestState) {
        when (requestState) {
            is AppointmentRequestState.Success -> {
                Toast.makeText(
                    context,
                    "Appointment request sent to Dr. ${doctor.name}",
                    Toast.LENGTH_SHORT
                ).show()
                patientViewModel.resetAppointmentRequestState()
                // Close bottom sheet if it's open
                if (showBottomSheet) {
                    showBottomSheet = false
                }
            }
            is AppointmentRequestState.Error -> {
                Toast.makeText(
                    context,
                    (requestState as AppointmentRequestState.Error).message,
                    Toast.LENGTH_SHORT
                ).show()
                patientViewModel.resetAppointmentRequestState()
            }
            else -> {} // Handle other states if needed
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                navController.navigate("doctorDetails/${doctor.email}")
            },

        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)

    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Doctor image and info
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Doctor image/avatar
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                    ) {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "Doctor",
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Doctor information
                    Column {
                        Text(
                            text = doctor.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = doctor.qualification,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                Icons.Default.Star,
                                contentDescription = "Rating",
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = String.format("%.1f", doctor.rating),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = " • ${doctor.patientsAttended} patients",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Book Appointment Button
            Button(
                onClick = {
                    // Show bottom sheet for confirmation
                    showBottomSheet = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    Icons.Default.DateRange,
                    contentDescription = "Book Appointment",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Book Appointment")
            }
        }
    }

    // Appointment request confirmation bottom sheet
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = bottomSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Request Appointment",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Send an appointment request to Dr. ${doctor.name}. The doctor will review your request and assign you a suitable time slot.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Optional message field
                var message by remember { mutableStateOf("") }

                OutlinedTextField(
                    value = message,
                    onValueChange = { message = it },
                    label = { Text("Any specific requirements? (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedButton(
                        onClick = { showBottomSheet = false },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }

                    Button(
                        onClick = {
                            val patientEmail = patientViewModel.patientProfile.value?.email
                            if (patientEmail != null) {
                                patientViewModel.requestAppointment(
                                    doctorEmail = doctor.email,
                                    patientEmail = patientEmail,
                                    message = message
                                )
                            } else {
                                Toast.makeText(
                                    context,
                                    "Patient profile not found. Please try again.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                showBottomSheet = false
                            }
                        },
                        modifier = Modifier.weight(1f),
                        enabled = requestState != AppointmentRequestState.Loading
                    ) {
                        if (requestState == AppointmentRequestState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Send Request")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}











