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

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AppViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState

    init {
        viewModelScope.launch {
            StreamUserDataStore.instance().user.collect { user ->
                val isLoggedIn = user != null
                _uiState.update {
                    it.copy(
                        loginStatus = if (isLoggedIn) {
                            LoginStatus.Authenticated
                        } else {
                            LoginStatus.NotAuthenticated
                        },
                    )
                }
            }
        }
    }
}

@Stable
data class AppUiState(
    val loginStatus: LoginStatus? = null,
)

sealed interface LoginStatus {
    val appRoute: AppRoute
    data object Authenticated : LoginStatus {
        override val appRoute: AppRoute = AppRoute.MeetingList
    }

    data object NotAuthenticated : LoginStatus {
        override val appRoute: AppRoute = AppRoute.Login
    }
}
