package com.example.docadmin.Screens

import androidx.compose.material3.Icon

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

import androidx.compose.ui.res.painterResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.docadmin.R
import com.example.docadmin.navigation.Routes

data class BottomNavItem(
    val route: String,
    val icon: Int,
    val label: String
)

@Composable
fun MainBottomNavScreen(
    navController: NavHostController
) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem(Routes.HOME, R.drawable.profile, "Home"),
        BottomNavItem(Routes.MEETINGS, R.drawable.meeting_room, "Meetings"),
        BottomNavItem(Routes.PROFILE, R.drawable.profile, "Profile")
    )

    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {

                            popUpTo(Routes.HOME) {
                                saveState = true
                            }

                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}