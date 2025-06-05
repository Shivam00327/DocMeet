package com.example.docpatient.Screen.Ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.docpatient.R
import com.example.docpatient.navigation.Routes
import com.example.docpatient.viewModel.AuthState
import com.example.docpatient.viewModel.AuthViewModel
import com.example.docpatient.viewModel.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier, navController: NavHostController, authViewModel: AuthViewModel,patientViewModel: PatientViewModel){

    val context= LocalContext.current
    val currentPatient = authViewModel.getCurrentUserEmail() // Assume this method exists
    val patientProfile by patientViewModel.patientProfile.collectAsState()
    val authState=authViewModel.authState.observeAsState()

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var disease by remember { mutableStateOf("") }
    var hasPermission by remember { mutableStateOf(false) }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(currentPatient) {
        currentPatient?.let { patientViewModel.fetchPatientProfile(it) }
    }

    LaunchedEffect(patientProfile) {
        patientProfile?.let {
            name = it.patientName
            email = it.email
            age = it.age
            disease = it.disease
        }
    }
    LaunchedEffect (authState.value){
        when(authState.value){

            is AuthState.Unauthenticated -> {
                Toast.makeText(context,"Unauthenticated", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.LOGIN)
            }
            else ->Unit

        }

    }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
//            currentPatient?.let { email ->
//                patientProfile.updateProfilePicture(email, uri, context)
//            }
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcher.launch("image/*")
        } else {
            Toast.makeText(context, "Permission needed for selecting image", Toast.LENGTH_SHORT).show()
        }
    }

    val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_IMAGES
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    LaunchedEffect(Unit) {
        hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }



    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                title = { /* Optional title */ }
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(bottom = 8.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    // Profile Image
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(2.dp, MaterialTheme.colorScheme.primary, CircleShape)
                            .clickable { permissionLauncher.launch(permission) }
                    ) {
                        AsyncImage(
                            model = R.drawable.profile,
                            contentDescription = "Profile Picture",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,

                            )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    EditableProfileField("patientName", name) { newName ->
                        name = newName
                        currentPatient?.let {
                            patientViewModel.updatePatientProfile(
                                email = it,
                                name = newName,
                                context = context
                            )
                        }
                    }
                    EditableProfileField("Email", email) { newEmail ->
                        email = newEmail
                        currentPatient?.let {
                            patientViewModel.updatePatientProfile(
                                email = it,
                                context = context
                            )
                        }
                    }
                    EditableProfileField("Age", age) { newAge ->
                        age = newAge
                        currentPatient?.let {
                            patientViewModel.updatePatientProfile(
                                email = it,
                                age = newAge,
                                context = context
                            )
                        }
                    }
                    EditableProfileField("Disease", disease) { newDisease ->
                        disease = newDisease
                        currentPatient?.let {
                            patientViewModel.updatePatientProfile(
                                email = it,
                                disease = newDisease,
                                context = context
                            )
                        }
                    }
//                    NonEditableProfileField("Rating", rating)
//                    NonEditableProfileField("Patients Attended", patientsAttended)
                }
            }

            // Logout Button above bottom nav
            Button(
                onClick = { authViewModel.signOut() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {

                Text("Logout")
            }
        }

    }
}

@Composable
fun EditableProfileField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}
@Composable
fun NonEditableProfileField(
    label: String,
    value: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = { },
        label = { Text(label) },
        readOnly = true,
        enabled = false,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
}

