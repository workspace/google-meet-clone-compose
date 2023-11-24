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
package com.github.workspace.googlemeetclone.ui.meeting

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.workspace.googlemeetclone.ui.meeting.action.FlipCameraButton
import com.github.workspace.googlemeetclone.ui.meeting.action.LeaveButton
import com.github.workspace.googlemeetclone.ui.meeting.action.MenuButton
import com.github.workspace.googlemeetclone.ui.meeting.action.ReactionButton
import com.github.workspace.googlemeetclone.ui.meeting.action.ToggleCameraButton
import com.github.workspace.googlemeetclone.ui.meeting.action.ToggleMicrophoneButton
import com.github.workspace.googlemeetclone.ui.meeting.action.ToggleSpeakerPhoneButton
import com.github.workspace.googlemeetclone.ui.meeting.participant.HandRaiseReaction
import com.github.workspace.googlemeetclone.ui.meeting.participant.ParticipantLabel
import com.github.workspace.googlemeetclone.ui.meeting.pip.PictureInPictureContent
import com.github.workspace.googlemeetclone.ui.meeting.pip.enterPictureInPicture
import com.github.workspace.googlemeetclone.ui.meeting.pip.rememberPictureInPictureMode
import com.github.workspace.googlemeetclone.utils.TextShareHelper
import io.getstream.log.StreamLog
import io.getstream.video.android.compose.lifecycle.CallLifecycle
import io.getstream.video.android.compose.lifecycle.MediaPiPLifecycle
import io.getstream.video.android.compose.permission.rememberCallPermissionsState
import io.getstream.video.android.compose.ui.components.call.CallAppBar
import io.getstream.video.android.compose.ui.components.call.controls.ControlActions
import io.getstream.video.android.compose.ui.components.call.controls.actions.DefaultOnCallActionHandler
import io.getstream.video.android.compose.ui.components.call.renderer.FloatingParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantsLayout
import io.getstream.video.android.compose.ui.components.call.renderer.RegularVideoRendererStyle
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.RealtimeConnection
import io.getstream.video.android.core.call.state.LeaveCall

@Composable
fun MeetingScreen(call: Call, onCallDisconnected: () -> Unit, onUserLeaveCall: () -> Unit) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isInPictureInPicture by rememberPictureInPictureMode()
    val callState by call.state.connection.collectAsStateWithLifecycle()
    val videoPermission = rememberCallPermissionsState(call = call)

    LaunchedEffect(key1 = callState) {
        if (callState == RealtimeConnection.Disconnected) {
            onCallDisconnected.invoke()
        } else if (callState is RealtimeConnection.Failed) {
            Toast.makeText(
                context,
                "Call connection failed (${(callState as RealtimeConnection.Failed).error}",
                Toast.LENGTH_LONG,
            ).show()
            onCallDisconnected.invoke()
        }
    }

    LaunchedEffect(key1 = videoPermission) {
        videoPermission.launchPermissionRequest()
    }

    MediaPiPLifecycle(
        call = call,
        enableInPictureInPicture = true,
    )

    CallLifecycle(
        call = call,
        enableInPictureInPicture = true,
    )

    BackHandler {
        try {
            enterPictureInPicture(context = context, call = call)
        } catch (e: Exception) {
            StreamLog.e(tag = "CallContent") { e.stackTraceToString() }
            call.leave()
        }
    }

    if (isInPictureInPicture) {
        PictureInPictureContent(call)
    } else {
        Scaffold(
            topBar = {
                CallAppBar(
                    modifier = Modifier.statusBarsPadding(),
                    call = call,
                    leadingContent = {
                        IconButton(
                            onUserLeaveCall,
                            colors = IconButtonDefaults.iconButtonColors(),
                        ) {
                            Icon(Icons.Default.ArrowBack, "back")
                        }
                    },
                    title = call.id,
                    trailingContent = {
                        ToggleSpeakerPhoneButton(call)
                        FlipCameraButton(call)
                    },
                )
            },
            bottomBar = {
                ControlActions(
                    modifier = Modifier.navigationBarsPadding(),
                    call = call,
                    onCallAction = { action ->
                        when (action) {
                            is LeaveCall -> {
                                onUserLeaveCall()
                            }

                            else -> DefaultOnCallActionHandler.onCallAction(call, action)
                        }
                    },
                    actions = listOf(
                        { LeaveButton(onUserLeaveCall) },
                        { ToggleCameraButton(call) },
                        { ToggleMicrophoneButton(call) },
                        { ReactionButton(call) },
                        { MenuButton() },
                    ),
                )
            },
        ) {
            val totalParticipants by call.state.totalParticipants.collectAsStateWithLifecycle()

            when (totalParticipants) {
                1 -> {
                    val currentLocal by call.state.me.collectAsStateWithLifecycle()
                    var parentSize: IntSize by remember { mutableStateOf(IntSize(0, 0)) }

                    if (currentLocal != null) {
                        Box(
                            modifier = Modifier
                                .padding(it)
                                .onSizeChanged { size -> parentSize = size }
                                .fillMaxSize(),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp)
                                    .align(Alignment.Center),
                                verticalArrangement = Arrangement.spacedBy(16.dp),
                            ) {
                                Text(text = "You're the only one here")
                                Text(
                                    text = "Share this meeting link with others you want in the meeting",
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(MaterialTheme.shapes.small)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                        .padding(8.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        modifier = Modifier.weight(1F),
                                        text = "meet.google.com/${call.id}",
                                    )
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(
                                                AnnotatedString(call.id),
                                            )
                                        },
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.ContentCopy,
                                            contentDescription = "Copy",
                                        )
                                    }
                                }
                                Button(
                                    onClick = { TextShareHelper.share(context, call.id) },
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Share,
                                        contentDescription = "Share Invite",
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(text = "Share Invite")
                                }
                            }
                            FloatingParticipantVideo(
                                call = call,
                                participant = currentLocal!!,
                                style = RegularVideoRendererStyle(
                                    isShowingConnectionQualityIndicator = false,
                                ),
                                parentBounds = parentSize,
                            )
                        }
                    }
                }
                else -> {
                    ParticipantsLayout(
                        call = call,
                        modifier = Modifier
                            .padding(it)
                            .fillMaxSize(),
                        style = RegularVideoRendererStyle(reactionPosition = Alignment.Center),
                        videoRenderer = { videoModifier, videoCall, videoParticipant, videoStyle ->
                            ParticipantVideo(
                                modifier = videoModifier,
                                call = videoCall,
                                participant = videoParticipant,
                                style = videoStyle,
                                reactionContent = { participant -> HandRaiseReaction(participant) },
                                labelContent = { participant -> ParticipantLabel(participant) },
                                connectionIndicatorContent = {},
                            )
                        },
                    )
                }
            }
        }
    }
}
