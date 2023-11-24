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
package com.github.workspace.googlemeetclone.ui.meetinglobby

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.workspace.googlemeetclone.ui.theme.GoogleMeetTheme
import io.getstream.video.android.compose.permission.rememberCallPermissionsState
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.avatar.UserAvatar
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleCameraAction
import io.getstream.video.android.compose.ui.components.call.controls.actions.ToggleMicrophoneAction
import io.getstream.video.android.compose.ui.components.video.VideoRenderer
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.ParticipantState
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.model.VideoTrack
import io.getstream.video.android.mock.StreamMockUtils
import io.getstream.video.android.mock.mockCall
import io.getstream.video.android.model.User

@Composable
fun MeetingLobbyScreen(
    meetingLobbyViewModel: MeetingLobbyViewModel = viewModel(),
    navigateUp: () -> Unit,
    navigateToMeeting: (String) -> Unit,
) {
    val call by remember { mutableStateOf(meetingLobbyViewModel.call) }

    val permissions = rememberCallPermissionsState(call = call)
    LaunchedEffect(key1 = permissions) {
        permissions.launchPermissionRequest()
    }

    MeetingLobbyScreen(
        call = call,
        onNavigationIconClick = navigateUp,
        onJoinButtonClick = { navigateToMeeting(call.cid) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeetingLobbyScreen(
    modifier: Modifier = Modifier,
    call: Call,
    onNavigationIconClick: () -> Unit,
    onJoinButtonClick: () -> Unit,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(Icons.Outlined.Close, "close")
                    }
                },
                title = {},
            )
        },
        bottomBar = {
            MeetingLobbyFooter(onJoinButtonClick = onJoinButtonClick)
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(it)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = call.id, style = MaterialTheme.typography.headlineLarge)
            MeetingPreview(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .size(180.dp, 320.dp),
                call = call,
            )
        }
    }
}

@Composable
private fun MeetingPreview(
    modifier: Modifier = Modifier,
    call: Call,
    user: User = StreamVideo.instance().user,
) {
    val isCameraEnabled by if (LocalInspectionMode.current) {
        remember { mutableStateOf(true) }
    } else {
        call.camera.isEnabled.collectAsStateWithLifecycle()
    }
    val isMicrophoneEnabled by if (LocalInspectionMode.current) {
        remember { mutableStateOf(true) }
    } else {
        call.microphone.isEnabled.collectAsStateWithLifecycle()
    }

    Box(modifier = modifier) {
        VideoRenderer(
            modifier = Modifier.fillMaxSize(),
            call = call,
            video = ParticipantState.Video(
                sessionId = call.sessionId,
                track = VideoTrack(
                    streamId = call.sessionId,
                    video = if (LocalInspectionMode.current) {
                        org.webrtc.VideoTrack(1000L)
                    } else {
                        call.camera.mediaManager.videoTrack
                    },
                ),
                enabled = isCameraEnabled,
            ),
            videoFallbackContent = {
                Box(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.inverseSurface)
                        .fillMaxSize(),
                ) {
                    UserAvatar(
                        modifier = Modifier
                            .size(VideoTheme.dimens.callAvatarSize)
                            .align(Alignment.Center),
                        userImage = user.image,
                        userName = user.name.ifBlank { user.id },
                    )
                }
            },
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            ToggleCameraAction(
                modifier = Modifier.size(48.dp),
                isCameraEnabled = isCameraEnabled,
                onCallAction = { (enabled) ->
                    call.camera.setEnabled(enabled)
                },
            )
            ToggleMicrophoneAction(
                modifier = Modifier.size(48.dp),
                isMicrophoneEnabled = isMicrophoneEnabled,
                onCallAction = { (enabled) ->
                    call.microphone.setEnabled(enabled)
                },
            )
        }
    }
}

@Composable
private fun MeetingLobbyFooter(
    modifier: Modifier = Modifier,
    user: User = StreamVideo.instance().user,
    onJoinButtonClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Button(
                shape = MaterialTheme.shapes.medium,
                onClick = onJoinButtonClick,
            ) {
                Icon(imageVector = Icons.Outlined.Videocam, contentDescription = "Join")
                Text(text = "Join")
            }
            Text(text = "Joining as")
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                UserAvatar(
                    modifier = Modifier.size(24.dp),
                    userName = user.name,
                    userImage = user.image,
                )
                Text(text = user.id)
            }
        }
    }
}

@Preview
@Composable
fun MeetingLobbyScreenPreview() {
    StreamMockUtils.initializeStreamVideo(LocalContext.current)

    GoogleMeetTheme {
        MeetingLobbyScreen(
            call = mockCall,
            onJoinButtonClick = {},
            onNavigationIconClick = {},
        )
    }
}
