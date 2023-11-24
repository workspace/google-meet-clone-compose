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
package com.github.workspace.googlemeetclone.ui.login

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.github.workspace.googlemeetclone.ui.theme.GoogleMeetTheme
import io.getstream.video.android.compose.ui.components.avatar.Avatar
import io.getstream.video.android.core.utils.initials
import io.getstream.video.android.model.User

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        uiState = uiState,
        onAccountClick = viewModel::selectAccount,
        onLoginButtonClick = viewModel::loginWithSelectedAccount,
    )
}

@Composable
private fun LoginScreen(
    uiState: LoginUiState,
    onAccountClick: (User) -> Unit,
    onLoginButtonClick: () -> Unit,
) {
    Scaffold(
        bottomBar = {
            MeetAccounts(
                uiState = uiState,
                onAccountClick = onAccountClick,
                onLoginButtonClick = onLoginButtonClick,
            )
        },
    ) {
        MeetIntroductions(
            modifier = Modifier
                .padding(it)
                .fillMaxSize(),
        )
    }
}

@Composable
private fun MeetAccounts(
    uiState: LoginUiState,
    onAccountClick: (User) -> Unit,
    onLoginButtonClick: () -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.extraLarge.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp),
        ),
        color = MaterialTheme.colorScheme.secondaryContainer,
    ) {
        Column(
            modifier = Modifier.padding(16.dp, 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier.clip(MaterialTheme.shapes.large),
                verticalArrangement = Arrangement.spacedBy(1.dp),
            ) {
                uiState.accounts.forEach { account ->
                    ListItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background)
                            .clickable {
                                onAccountClick(account)
                            },
                        leadingContent = {
                            Avatar(
                                modifier = Modifier.size(36.dp),
                                imageUrl = account.image,
                                initials = account.name.initials(),
                            )
                        },
                        headlineContent = { Text(text = account.name) },
                        supportingContent = { Text(text = account.id) },
                        trailingContent = if (uiState.selectedAccount.id == account.id) {
                            {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircleOutline,
                                    contentDescription = "account selected indicator",
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                            }
                        } else {
                            null
                        },
                    )
                }
            }
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Button(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onLoginButtonClick,
                ) {
                    Text("Continue as ${uiState.selectedAccount.name}")
                }
                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { /* Not Supported */ },
                ) {
                    Text("Use Meet without an account")
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MeetIntroductions(modifier: Modifier) {
    val pagerState = rememberPagerState { 3 }
    Column(
        modifier = modifier,
    ) {
        HorizontalPager(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1F),
            state = pagerState,
        ) { page ->
            MeetIntroduction(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxSize(),
                MeetIntroductionTitles[page],
                MeetIntroductionDescriptions[page],
            )
        }
        Row(
            Modifier
                .height(50.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color = if (pagerState.currentPage == iteration) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primaryContainer
                }
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(12.dp),
                )
            }
        }
    }
}

@Composable
fun MeetIntroduction(modifier: Modifier, title: String, description: String) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Text(title, style = MaterialTheme.typography.headlineLarge, textAlign = TextAlign.Center)
        Text(description, style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
    }
}

private val MeetIntroductionTitles = listOf(
    "Welcome to Google Meet",
    "High quality video calls on any device",
    "Rich video meetings for everyone to join",
)

private val MeetIntroductionDescriptions = listOf(
    "Make video calls to friends and family, or create and join meetings, all in one app",
    "Use masks, effects, messages, and family mode doodles, and capture special moments to make video calls more fun",
    "Schedule time to connect when everyone can join, and use virtual backgrounds, chat, captions, and live sharing",
)

@Preview
@Composable
private fun LoginScreenPreview() {
    GoogleMeetTheme {
        var uiState by remember {
            mutableStateOf(LoginUiState())
        }

        LoginScreen(
            uiState = uiState,
            onAccountClick = { account ->
                uiState = uiState.copy(selectedAccount = account)
            },
            onLoginButtonClick = {},
        )
    }
}
