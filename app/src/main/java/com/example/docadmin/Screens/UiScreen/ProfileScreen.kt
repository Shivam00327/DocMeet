package com.example.docadmin.ProfileScreen
import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import coil3.compose.AsyncImage
import com.example.docadmin.R
import com.example.docadmin.ViewModel.AuthState
import com.example.docadmin.ViewModel.AuthViewModel
import com.example.docadmin.ViewModel.UserViewModel
import com.example.docadmin.navigation.Routes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(modifier: Modifier, navController: NavHostController, authViewModel: AuthViewModel,userViewModel: UserViewModel) {
    val context= LocalContext.current
    val currentUser = authViewModel.getCurrentUserEmail() // Assume this method exists
    val userProfile by userViewModel.userProfile.collectAsState()
    val authState=authViewModel.authState.observeAsState()




        // Initial fetch of user profile
    LaunchedEffect(currentUser) {
        currentUser?.let { userViewModel.fetchUserProfile(it) }
    }

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var qualification by remember { mutableStateOf("") }
    val rating = "4.5"
    val patientsAttended = "100"

    // Update local states when profile is fetched
    LaunchedEffect(userProfile) {
        userProfile?.let {
            name = it.name
            email = it.email
            age = it.age
            qualification = it.qualification
        }
    }
    LaunchedEffect (authState.value){
        when(authState.value){
//            is AuthState.Authenticated -> navController.navigate(Routes.HOME)
            is AuthState.Unauthenticated -> {
                Toast.makeText(context,"Unauthenticated", Toast.LENGTH_SHORT).show()
                navController.navigate(Routes.LOGIN)
            }
            else ->Unit

        }

    }

    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var hasPermission by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            imageUri = it
            currentUser?.let { email ->
                userViewModel.updateProfilePicture(email, uri, context)
            }
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

                    EditableProfileField("Name", name) { newName ->
                        name = newName
                        currentUser?.let {
                            userViewModel.updateUserProfile(
                                email = it,
                                name = newName,
                                context = context
                            )
                        }
                    }
                    EditableProfileField("Email", email) { newEmail ->
                        email = newEmail
                        currentUser?.let {
                            userViewModel.updateUserProfile(
                                email = it,
                                context = context
                            )
                        }
                    }
                    EditableProfileField("Age", age) { newAge ->
                        age = newAge
                        currentUser?.let {
                            userViewModel.updateUserProfile(
                                email = it,
                                age = newAge,
                                context = context
                            )
                        }
                    }
                    EditableProfileField("Qualification", qualification) { newQualification ->
                        qualification = newQualification
                        currentUser?.let {
                            userViewModel.updateUserProfile(
                                email = it,
                                qualification = newQualification,
                                context = context
                            )
                        }
                    }
                    NonEditableProfileField("Rating", rating)
                    NonEditableProfileField("Patients Attended", patientsAttended)
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
