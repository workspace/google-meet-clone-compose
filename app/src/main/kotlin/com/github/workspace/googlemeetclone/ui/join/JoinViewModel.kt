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

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.result.onErrorSuspend
import io.getstream.result.onSuccessSuspend
import io.getstream.video.android.core.StreamVideo
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@Stable
data class JoinUiState(
    val isLoading: Boolean = false,
)

sealed interface JoinSideEffect {
    data class ShowToast(val message: String) : JoinSideEffect
    data class NavigateToLobby(val cid: String) : JoinSideEffect
}

class JoinViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(JoinUiState())
    val uiState: StateFlow<JoinUiState> = _uiState

    private val _sideEffect = Channel<JoinSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    fun join(code: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val call = StreamVideo.instance().call("default", code)
            val result = call.get()
            result
                .onSuccessSuspend {
                    _sideEffect.send(JoinSideEffect.NavigateToLobby(it.call.cid))
                }
                .onErrorSuspend { error ->
                    _sideEffect.send(JoinSideEffect.ShowToast(error.message))
                }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    override fun onCleared() {
        _sideEffect.close()
    }
}
