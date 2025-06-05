package com.example.docadmin.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

import com.example.docadmin.ProfileScreen.ProfileScreen
import com.example.docadmin.Screens.UiScreen.MeetingsScreen

import com.example.docadmin.Screens.UiScreen.HomeScreen
import com.example.docadmin.Screens.Login.LoginScreen
import com.example.docadmin.Screens.SignUp.SignUpScreen
import com.example.docadmin.VideoCall.EasyMeetScreen
import com.example.docadmin.ViewModel.AuthViewModel
import com.example.docadmin.ViewModel.UserViewModel


@Composable
fun SetupNavGraph(modifier: Modifier, navController: NavHostController, authViewModel: AuthViewModel,userViewModel: UserViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
    ) {
        composable(Routes.HOME) { HomeScreen(modifier,navController,authViewModel,userViewModel) }
        composable(Routes.MEETINGS) { MeetingsScreen(modifier,navController,authViewModel,userViewModel) }
        composable(Routes.PROFILE) { ProfileScreen(modifier,navController,authViewModel, userViewModel ) }
        composable(Routes.LOGIN) { LoginScreen(modifier,navController,authViewModel) }
        composable(Routes.SIGNUP) { SignUpScreen(navController,authViewModel,userViewModel) }
//        composable(Routes.VIDEOCALL) { EasyMeetScreen() }

        composable("videocall/{meetingId}") { backStackEntry ->
            val meetingId = backStackEntry.arguments?.getString("meetingId") ?: ""
            EasyMeetScreen(meetingId)
        }

    }
}


