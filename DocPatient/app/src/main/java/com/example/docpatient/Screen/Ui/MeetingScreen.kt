package com.example.docpatient.Screen.Ui

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.docpatient.R
import com.example.docpatient.Screen.MainBottomNavScreen
import com.example.docpatient.dataClass.Appointment
import com.example.docpatient.navigation.Routes.VIDEOCALL
import com.example.docpatient.viewModel.AuthViewModel
import com.example.docpatient.viewModel.DoctorViewModel
import com.example.docpatient.viewModel.PatientViewModel
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MeetingsScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    authViewModel: AuthViewModel,
    doctorViewModel: DoctorViewModel,
    patientViewModel: PatientViewModel
) {
    val appointments by doctorViewModel.patientAppointments.collectAsState()
    val isLoading by doctorViewModel.isLoadingAppointments.collectAsState()
    val patientEmail = authViewModel.getCurrentUserEmail() // Assume this method exists

    LaunchedEffect(patientEmail) {
        if (patientEmail != null) {
            doctorViewModel.fetchPatientAppointments(patientEmail)
        }
    }

    Scaffold(
        bottomBar = { MainBottomNavScreen(navController) },
        containerColor = Color(0xFF3E3E3F) // Light Gray Background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Gradient Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFFAEB1B6), Color(0xFF2B2B2D))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Scheduled Appointments",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF007AFF))
                    }
                }
                appointments.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = "No scheduled appointments",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(appointments) { appointment ->
                            AnimatedVisibility(visible = true) {
                                AppointmentCard(appointment,navController)
                            }
                        }
                    }
                }
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentCard(appointment: Appointment,navController: NavHostController) {
    val currentTime = remember { mutableStateOf(System.currentTimeMillis()) }

    // Convert appointment time to LocalDateTime
    val appointmentTime = Instant.ofEpochMilli(appointment.appointmentTime)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()

    // Calculate the valid call window (appointment time + 2 hours)
    val appointmentStart = appointmentTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
    val appointmentEnd = appointmentStart + TimeUnit.HOURS.toMillis(2)

    // Check if call button should be enabled
    val isCallEnabled = currentTime.value in appointmentStart..appointmentEnd

    // Update time every 1 minute
    LaunchedEffect(Unit) {
        while (true) {
            currentTime.value = System.currentTimeMillis()
            delay(60_000) // Update every minute
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .shadow(8.dp, shape = RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Appointment Icon
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = "Appointment",
                    tint = Color(0xFF007AFF),
                    modifier = Modifier.size(36.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "Doctor: ${appointment.doctorEmail}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black
                    )
                    Log.d("AppointmentCard", "Doctor Email: ${appointment.meetingId}")
                    Text(
                        text = "Doctor: ${appointment.meetingId}",
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                        color = Color.Black
                    )
                    Text(
                        text = "📅 ${appointment.appointmentTime.toFormattedDateTime()}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Video Call Button
            Button(
                onClick = {
                    navController.navigate("videocall/${appointment.meetingId}")
                },
                enabled = isCallEnabled,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCallEnabled) Color(0xFF007AFF) else Color.Gray
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.videocam_24),
                    contentDescription = "Join Call",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = if (isCallEnabled) "Join Video Call" else "Call Disabled")
            }
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
fun Long.toFormattedDateTime(): String {
    val formatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy • hh:mm a")
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(formatter)
}



