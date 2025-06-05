package com.example.docpatient.Screen.Ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.docpatient.viewModel.DoctorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorDetailScreen(navController: NavHostController, doctorViewModel: DoctorViewModel,doctorEmail: String) {
    val doctorProfile by doctorViewModel.doctorProfile.collectAsState()

    // Fetch doctor details when screen opens
    LaunchedEffect(doctorEmail) {
        doctorViewModel.fetchDoctorProfile(doctorEmail)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Doctor Details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Doctor",
                modifier = Modifier.size(100.dp),
                tint = Color(0xFF007AFF)
            )

            Spacer(modifier = Modifier.height(16.dp))

            doctorProfile?.let { profile ->
                Text(text = "Name: ${profile.name}", style = MaterialTheme.typography.headlineMedium)
                Text(text = "Specialization: ${profile.qualification}", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Email: $doctorEmail", style = MaterialTheme.typography.bodyLarge)
                Text(text = "Experience: ${profile.rating} years", style = MaterialTheme.typography.bodyMedium)
                //Text(text = "Location: ${profile.email}", style = MaterialTheme.typography.bodyMedium)
            } ?: CircularProgressIndicator()
        }
    }
}
