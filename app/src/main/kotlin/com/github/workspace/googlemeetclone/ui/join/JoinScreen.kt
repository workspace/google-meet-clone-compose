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
package com.github.workspace.googlemeetclone.ui.join

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.workspace.googlemeetclone.ui.common.collectInLaunchedEffect
import com.github.workspace.googlemeetclone.ui.theme.GoogleMeetTheme
import io.getstream.video.android.mock.StreamMockUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(
    viewModel: JoinViewModel = viewModel(),
    navigateUp: () -> Unit,
    navigateToMeetingLobby: (String) -> Unit,
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    viewModel.sideEffect.collectInLaunchedEffect { sideEffect ->
        when (sideEffect) {
            is JoinSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
            is JoinSideEffect.NavigateToLobby -> {
                navigateToMeetingLobby(sideEffect.cid)
            }
        }
    }

    JoinScreen(
        isLoading = uiState.isLoading,
        onNavigationIconClick = navigateUp,
        onJoinButtonClick = viewModel::join,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JoinScreen(
    isLoading: Boolean,
    onNavigationIconClick: () -> Unit,
    onJoinButtonClick: (String) -> Unit,
) {
    val (code, setCode) = remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onNavigationIconClick) {
                        Icon(Icons.Outlined.Close, "close")
                    }
                },
                title = { Text(text = "Join with a code") },
                actions = {
                    TextButton(onClick = { onJoinButtonClick(code) }) {
                        if (isLoading) {
                            CircularProgressIndicator()
                        } else {
                            Text(text = "Join")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                ),
            )
        },
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            val focusRequester = remember {
                FocusRequester()
            }
            LaunchedEffect(key1 = Unit) {
                focusRequester.requestFocus()
            }
            Text(text = "Enter the code provided by the meeting organizer")
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
                value = code,
                onValueChange = setCode,
                singleLine = true,
                placeholder = { Text(text = "Example: abc-mnop-xyz") },
                enabled = !isLoading,
            )
        }
    }
}

@Preview
@Composable
private fun JoinScreenPreview() {
    StreamMockUtils.initializeStreamVideo(LocalContext.current)

    GoogleMeetTheme {
        JoinScreen(
            isLoading = false,
            onNavigationIconClick = {},
            onJoinButtonClick = {},
        )
    }
}
