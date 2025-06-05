package com.example.docpatient.Screen.SignUp

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.docpatient.R
import com.example.docpatient.navigation.Routes
import com.example.docpatient.ui.theme.Black
import com.example.docpatient.ui.theme.BlueGray

import com.example.docpatient.viewModel.AuthState
import com.example.docpatient.viewModel.AuthViewModel
import com.example.docpatient.viewModel.PatientViewModel


@Composable
fun SignUpScreen(navController: NavHostController, authViewModel: AuthViewModel, patientViewModel: PatientViewModel) {

    val authState=authViewModel.authState.observeAsState()
    val context= LocalContext.current

    LaunchedEffect (authState.value){
        when(authState.value){
            is AuthState.Authenticated -> navController.navigate(Routes.HOME)
            is AuthState.Unauthenticated -> Toast.makeText(context,"Unauthenticated", Toast.LENGTH_SHORT).show()
            else ->Unit

        }

    }

    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding() // Moves UI up when keyboard appears
        ) {
            TopSection() // Your header section
            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .weight(1f) // Ensures it takes only available space
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp)
                    .verticalScroll(rememberScrollState()) // Enables scrolling
            ) {

                SignUpSection(navController,patientViewModel, context = LocalContext.current,authViewModel)
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}


@Composable
private fun TopSection() {
    val uiColor = if (isSystemInDarkTheme()) Color.White else Black

    Box(
        contentAlignment = Alignment.TopCenter
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.5f),
            painter = painterResource(id = R.drawable.shape),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )


        Row(
            modifier = Modifier.padding(top = 80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Icon(
                modifier = Modifier.size(42.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = stringResource(id = R.string.app_logo),
                tint = uiColor
            )
            Spacer(modifier = Modifier.width(15.dp))
            Column {
                Text(
                    text = stringResource(id= R.string.DocPatient),
                    style = MaterialTheme.typography.headlineMedium,
                    color = uiColor
                )
                Text(
                    text = stringResource(id = R.string.find_Cure),
                    style = MaterialTheme.typography.titleMedium,
                    color = uiColor
                )
            }
        }

        Text(
            modifier = Modifier
                .padding(bottom = 10.dp)
                .align(alignment = Alignment.BottomCenter),
            text = stringResource(id = R.string.SignUP),
            style = MaterialTheme.typography.headlineLarge,
            color = uiColor
        )
    }
}


@Composable
private fun SignUpSection(navController: NavHostController, patientViewModel: PatientViewModel,context: Context,authViewModel: AuthViewModel) {
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var disease by remember { mutableStateOf("") }
    var passwordError by remember { mutableStateOf(false) }
    var isFormValid by remember { mutableStateOf(true) }

    val authState=authViewModel.authState.observeAsState()

    Column(
        modifier = Modifier
            .fillMaxWidth() // Do NOT use .fillMaxSize() here!
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Name
        SignUpTextField(
            label = "Name",
            trailing = "",
            modifier = Modifier.fillMaxWidth(),
            value = name,
            onValueChange = { name = it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Age (Numeric Input)
        SignUpTextField(
            label = "Age",
            trailing = "",
            modifier = Modifier.fillMaxWidth(),
            value = age,
            onValueChange = { if (it.all { char -> char.isDigit() }) age = it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Phone Number (Numeric Input)
        SignUpTextField(
            label = "Phone Number",
            trailing = "",
            modifier = Modifier.fillMaxWidth(),
            value = phoneNumber,
            onValueChange = { if (it.all { char -> char.isDigit() }) phoneNumber = it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        SignUpTextField(
            label = "Disease",
            trailing = "",
            modifier = Modifier.fillMaxWidth(),
            value = disease,
            onValueChange = { disease=it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Email
        SignUpTextField(
            label = "Email",
            trailing = "",
            modifier = Modifier.fillMaxWidth(),
            value = email,
            onValueChange = { email = it }
        )
        Spacer(modifier = Modifier.height(12.dp))

        // Password with Validation
        Column(modifier = Modifier.fillMaxWidth()) {
            SignUpTextField(
                label = "Password",
                trailing = "",
                modifier = Modifier.fillMaxWidth(),
                value = password,
                onValueChange = {
                    password = it
                    passwordError = !isValidPassword(password)
                },
            )
            if (passwordError) {
                Text(
                    text = "Password must be at least 8 characters, include 1 number & 1 special character",
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // SignUp Button
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            onClick = {
                isFormValid = validateForm(name, age, phoneNumber, email, password)
                if (isFormValid) {
                    authViewModel.signup(email,password)
                    patientViewModel.createUser(name, age, phoneNumber, email, password,disease,context)

                }
            }, enabled = authState.value !is AuthState.Loading,
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isSystemInDarkTheme()) BlueGray else Black,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(size = 8.dp)
        ) {
            Text(
                text = "Sign Up",
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Medium)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Navigate to Login
        Text(
            text = "Already have an account? Login",
            color = Color.Blue,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .clickable { navController.navigate(Routes.LOGIN) }
                .padding(8.dp)
        )
    }
}










// Function to validate password
fun isValidPassword(password: String): Boolean {
    val specialCharPattern = "[!@#\$%^&*(),.?\":{}|<>]".toRegex()
    val numberPattern = "[0-9]".toRegex()
    return password.length >= 8 &&
            password.contains(specialCharPattern) &&
            password.contains(numberPattern)
}

// Function to validate form fields
fun validateForm(name: String, age: String, phoneNumber: String, email: String, password: String): Boolean {
    return name.isNotEmpty() &&
            age.isNotEmpty() && age.toIntOrNull() != null &&
            phoneNumber.length == 10 && phoneNumber.all { it.isDigit() } &&
            android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() &&
            isValidPassword(password)
}