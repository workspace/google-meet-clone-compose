/*
 * Copyright 2023 workspace
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.workspace.googlemeetclone.ui

import android.content.Intent
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.github.workspace.googlemeetclone.ui.join.JoinScreen
import com.github.workspace.googlemeetclone.ui.login.LoginScreen
import com.github.workspace.googlemeetclone.ui.meeting.MeetingActivity
import com.github.workspace.googlemeetclone.ui.meetinglist.MeetingListScreen
import com.github.workspace.googlemeetclone.ui.meetinglobby.MeetingLobbyScreen
import com.github.workspace.googlemeetclone.ui.newmeeting.NewMeetingScreen
import io.getstream.video.android.model.StreamCallId

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String,
) {
    val context = LocalContext.current
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(AppRoute.Login.route) {
            LoginScreen()
        }
        composable(AppRoute.MeetingList.route) {
            MeetingListScreen(
                navigateToNewMeeting = {
                    navController.navigate(AppRoute.NewMeeting.route)
                },
                navigateToJoinMeeting = {
                    navController.navigate(AppRoute.JoinMeeting.route)
                },
                navigateToMeetingLobby = { cid ->
                    navController.navigate("${AppRoute.MeetingLobby.route}/$cid")
                },
            )
        }
        slideInFromBottom(AppRoute.NewMeeting.route) {
            NewMeetingScreen(
                navigateUp = {
                    navController.navigateUp()
                },
                navigateToMeetingLobby = { cid ->
                    navController.navigate("${AppRoute.MeetingLobby.route}/$cid") {
                        popUpTo(AppRoute.MeetingList.route)
                    }
                },
            )
        }
        slideInFromBottom(
            route = "${AppRoute.MeetingLobby.route}/{cid}",
            arguments = listOf(
                navArgument("cid") {
                    type = NavType.StringType
                },
            ),
        ) {
            MeetingLobbyScreen(
                navigateUp = navController::navigateUp,
                navigateToMeeting = { cid ->
                    val intent = MeetingActivity.createIntent(
                        context = context,
                        callId = StreamCallId.fromCallCid(cid),
                    ).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    context.startActivity(intent)
                },
            )
        }
        slideInFromBottom(
            route = AppRoute.JoinMeeting.route,
        ) {
            JoinScreen(
                navigateUp = navController::navigateUp,
                navigateToMeetingLobby = { cid ->
                    navController.navigate("${AppRoute.MeetingLobby.route}/$cid") {
                        popUpTo(AppRoute.MeetingList.route)
                    }
                },
            )
        }
    }
}

private fun NavGraphBuilder.slideInFromBottom(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit,
) {
    composable(
        route = route,
        arguments = arguments,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Up,
                animationSpec = tween(),
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Down,
                animationSpec = tween(),
            )
        },
        content = content,
    )
}

enum class AppRoute(
    val route: String,
) {
    Login("login"),
    MeetingList("meeting-list"),
    JoinMeeting("join-meeting"),
    NewMeeting("new-meeting"),
    MeetingLobby("meeting-lobby"),
}
