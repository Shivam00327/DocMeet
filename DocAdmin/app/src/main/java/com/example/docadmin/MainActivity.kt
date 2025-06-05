package com.example.docadmin

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.rememberNavController
import com.example.docadmin.Repository.UserRepository
import com.example.docadmin.ViewModel.AuthViewModel
import com.example.docadmin.ViewModel.UserViewModel
import com.example.docadmin.ui.theme.DocAdminTheme
import com.example.docadmin.navigation.SetupNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val authViewModel:AuthViewModel by viewModels()
        val userRepository = UserRepository() // Create repository first
        val userViewModel: UserViewModel by viewModels {
            UserViewModelFactory(userRepository)
        }
        setContent {
            DocAdminTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding->
                    SetupNavGraph(modifier = Modifier.padding(innerPadding), navController = navController, authViewModel,userViewModel)
                }
            }
        }
    }
}
class UserViewModelFactory(private val userRepository: UserRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(UserViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return UserViewModel(userRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}// Create a factory for UserViewModel


