package com.example.docpatient.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable



import com.example.docpatient.Screen.Login.LoginScreen
import com.example.docpatient.Screen.SignUp.SignUpScreen
import com.example.docpatient.Screen.Ui.DoctorDetailScreen
import com.example.docpatient.Screen.Ui.HomeScreen
import com.example.docpatient.Screen.Ui.MeetingsScreen
import com.example.docpatient.Screen.Ui.ProfileScreen
import com.example.docpatient.videoCall.EasyMeetScreen
import com.example.docpatient.viewModel.AuthViewModel
import com.example.docpatient.viewModel.DoctorViewModel
import com.example.docpatient.viewModel.PatientViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SetupNavGraph(modifier: Modifier,
                  navController: NavHostController,
                  authViewModel: AuthViewModel,
                  patientViewModel: PatientViewModel,
                  doctorViewModel: DoctorViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.HOME) { HomeScreen(modifier,navController,authViewModel, patientViewModel,doctorViewModel) }
        composable(Routes.MEETINGS) { MeetingsScreen(modifier,navController,authViewModel,doctorViewModel,patientViewModel) }
        composable(Routes.PROFILE) { ProfileScreen(modifier,navController,authViewModel, patientViewModel ) }
        composable(Routes.LOGIN) { LoginScreen(modifier,navController,authViewModel) }
        composable(Routes.SIGNUP) { SignUpScreen(navController,authViewModel,patientViewModel) }
        //composable(Routes.VIDEOCALL) { EasyMeetScreen() }
//        composable(Routes.DOCTORDETAILS){ DoctorDetailScreen(navController,doctorViewModel) }



        composable("doctorDetails/{doctorEmail}") { backStackEntry ->
            val doctorEmail = backStackEntry.arguments?.getString("doctorEmail") ?: ""
            DoctorDetailScreen(navController, doctorViewModel, doctorEmail)
        }
        composable("videocall/{meetingId}") { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getString("meetingId") ?: ""
            EasyMeetScreen(meetingId)
        }
    }
}





