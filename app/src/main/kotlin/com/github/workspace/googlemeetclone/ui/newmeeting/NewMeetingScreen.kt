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
package com.github.workspace.googlemeetclone.ui.newmeeting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.workspace.googlemeetclone.utils.TextShareHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMeetingScreen(
    viewModel: NewMeetingViewModel = viewModel(),
    navigateUp: () -> Unit,
    navigateToMeetingLobby: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreatedMeetingDialog(
        uiState = uiState,
        onDismiss = viewModel::dismissNewMeetingDialog,
        onJoinClick = navigateToMeetingLobby,
    )

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(navigateUp) {
                        Icon(Icons.Outlined.Close, "close")
                    }
                },
                title = {
                    Text("New")
                },
            )
        },
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(it),
        ) {
            ListItem(
                modifier = Modifier.clickable(onClick = viewModel::createNewMeeting),
                leadingContent = {
                    Icon(
                        Icons.Outlined.Link,
                        "Create a new meeting",
                    )
                },
                headlineContent = {
                    Text(
                        "Create a new meeting",
                        style = MaterialTheme.typography.titleMedium,
                    )
                },
                colors = ListItemDefaults.colors(
                    leadingIconColor = MaterialTheme.colorScheme.primary,
                    headlineColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreatedMeetingDialog(
    uiState: NewMeetingUiState,
    onDismiss: () -> Unit,
    onJoinClick: (String) -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(),
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val scope = rememberCoroutineScope()
    val onDismissRequest: () -> Unit = {
        scope.launch {
            sheetState.hide()
            onDismiss()
        }
    }

    if (uiState.showNewMeetingDialog) {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
            windowInsets = WindowInsets(0.dp),
            dragHandle = null,
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
        ) {
            Column(
                modifier = Modifier
                    .navigationBarsPadding()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Share this joining info with people you want in the meeting",
                )

                if (uiState.isLoading || uiState.createdCallId == null) {
                    CircularProgressIndicator()
                } else {
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
                            text = "meet.google.com/${uiState.createdCallId.id}",
                            color = MaterialTheme.colorScheme.primary,
                        )
                        IconButton(
                            onClick = {
                                clipboardManager.setText(
                                    AnnotatedString(uiState.createdCallId.id),
                                )
                            },
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.ContentCopy,
                                contentDescription = "Copy",
                                tint = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        AssistChip(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Share,
                                    contentDescription = "Share",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            },
                            onClick = { TextShareHelper.share(context, uiState.createdCallId.id) },
                            label = { Text(text = "Share") },
                        )
                        AssistChip(
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.MeetingRoom,
                                    contentDescription = "Join meeting",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            },
                            onClick = { onJoinClick(uiState.createdCallId.cid) },
                            label = { Text(text = "Join meeting") },
                        )
                    }
                }

                TextButton(
                    modifier = Modifier.padding(top = 24.dp),
                    onClick = onDismissRequest,
                ) {
                    Text(text = "Dismiss")
                }
            }
        }
    }
}
