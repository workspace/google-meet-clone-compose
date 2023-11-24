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
package com.github.workspace.googlemeetclone.ui.meetinglist

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import io.getstream.video.android.compose.ui.components.avatar.Avatar
import io.getstream.video.android.core.model.CallData
import io.getstream.video.android.core.utils.initials
import io.getstream.video.android.model.User

@Composable
fun MeetingListScreen(
    meetingListViewModel: MeetingListViewModel = viewModel(),
    navigateToJoinMeeting: () -> Unit,
    navigateToNewMeeting: () -> Unit,
    navigateToMeetingLobby: (String) -> Unit,
) {
    val uiState by meetingListViewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MeetingListTopBar(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp, 4.dp)
                    .fillMaxWidth(),
                navigationIcon = {
                    IconButton(
                        onClick = {},
                    ) {
                        Icon(Icons.Outlined.Menu, "Menu")
                    }
                },
                actions = {
                    uiState.currentUser?.let { user ->
                        AccountMenu(
                            user = user,
                            onClick = meetingListViewModel::signOut,
                        )
                    }
                },
                onClick = navigateToJoinMeeting,
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = navigateToNewMeeting,
                containerColor = MaterialTheme.colorScheme.primary,
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.Videocam, "new call")
                    Text("New")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(uiState.calls) { callData ->
                    MeetingListItem(
                        modifier = Modifier.fillMaxWidth(),
                        callData = callData,
                        onClick = { navigateToMeetingLobby(callData.call.cid) },
                    )
                }
            }
            AnimatedVisibility(
                modifier = Modifier.align(Alignment.Center),
                visible = uiState.isLoading,
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun MeetingListTopBar(
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier,
        onClick = onClick,
        color = MaterialTheme.colorScheme.secondaryContainer,
        shape = CircleShape,
    ) {
        Row(
            modifier = Modifier.padding(8.dp, 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            navigationIcon()
            Text(modifier = Modifier.weight(1F), text = "Enter code")
            actions()
        }
    }
}

@Composable
private fun AccountMenu(user: User, onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Avatar(
            modifier = Modifier.size(36.dp),
            imageUrl = user.image,
            initials = user.name.initials(),
        )
    }
}

@Composable
private fun MeetingListItem(
    modifier: Modifier = Modifier,
    callData: CallData,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = modifier
            .clickable(onClick = onClick),
        leadingContent = {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primary,
            ) {
                Icon(
                    modifier = Modifier.padding(12.dp),
                    imageVector = Icons.Outlined.Link,
                    contentDescription = null,
                )
            }
        },
        headlineContent = {
            Text(callData.call.id)
        },
        supportingContent = {
            Text(callData.call.createdAt.toString())
        },
    )
}
