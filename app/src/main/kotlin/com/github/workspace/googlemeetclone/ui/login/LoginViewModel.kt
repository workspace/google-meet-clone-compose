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

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.workspace.googlemeetclone.utils.StreamVideoHelper
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    fun selectAccount(account: User) {
        _uiState.update { it.copy(selectedAccount = account) }
    }

    fun loginWithSelectedAccount() {
        viewModelScope.launch {
            StreamVideoHelper.signIn(
                dataStore = StreamUserDataStore.instance(),
                user = _uiState.value.selectedAccount,
            )
        }
    }
}

@Stable
data class LoginUiState(
    val accounts: List<User> = listOf(
        User(id = "demo1@gmail.com", name = "Demo User 1"),
        User(id = "demo2@gmail.com", name = "Demo User 2"),
        User(id = "demo3@gmail.com", name = "Demo User 3"),
    ),
    val selectedAccount: User = accounts.first(),
)
