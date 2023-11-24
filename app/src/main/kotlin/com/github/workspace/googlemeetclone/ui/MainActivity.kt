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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.github.workspace.googlemeetclone.ui.common.LoadingIndicator
import com.github.workspace.googlemeetclone.ui.theme.GoogleMeetTheme

class MainActivity : ComponentActivity() {

    private val appViewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            GoogleMeetTheme {
                val uiState by appViewModel.uiState.collectAsStateWithLifecycle()

                if (uiState.loginStatus != null) {
                    AppNavHost(
                        startDestination = when (uiState.loginStatus) {
                            LoginStatus.Authenticated -> AppRoute.MeetingList.route
                            LoginStatus.NotAuthenticated -> AppRoute.Login.route
                            null -> "" // never reachable
                        },
                    )
                } else {
                    LoadingIndicator(modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}
