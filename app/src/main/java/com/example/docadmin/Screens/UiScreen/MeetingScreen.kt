package com.example.docadmin.Screens.UiScreen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.docadmin.DataModel.Appointment
import com.example.docadmin.R
import com.example.docadmin.Screens.MainBottomNavScreen
import com.example.docadmin.ViewModel.AuthViewModel
import com.example.docadmin.ViewModel.UserViewModel
import com.example.docadmin.navigation.Routes.VIDEOCALL
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
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val currentUser = authViewModel.getCurrentUserEmail()
    val userProfile by userViewModel.userProfile.collectAsState()

    val doctorAppointments by userViewModel.doctorAppointments.collectAsState()
    val isLoading by userViewModel.isLoading.collectAsState()
    val operationResult by userViewModel.operationResult.collectAsState()

    // Fetch appointments when the screen loads
    LaunchedEffect(Unit) {
        currentUser?.let { email ->
            Log.d("MeetingsScreen", "Fetching doctor appointments for user: $email")
            userViewModel.fetchDoctorAppointments(email)
        }
    }

    Scaffold(
        bottomBar = { MainBottomNavScreen(navController) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp, top = 48.dp)
        ) {
            Text(
                text = "Scheduled Appointments",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (doctorAppointments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No scheduled appointments", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn {
                    items(doctorAppointments) { appointment ->
                        AppointmentItem(appointment = appointment,navController)
                        Divider()
                    }
                }
            }

            // Show error message if fetching fails
            operationResult?.let { message ->
                if (message.isNotEmpty()) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 16.dp)
                    )
                }
            }
        }
    }
}
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppointmentItem(appointment: Appointment,navController: NavHostController) {
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
                    Text(
                        text = "Meeting ID-${appointment.meetingId}",
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
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, hh:mm a")
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
        .format(formatter)
}



