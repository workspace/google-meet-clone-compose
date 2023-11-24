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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.workspace.googlemeetclone.utils.MeetingIdGenerator
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.model.StreamCallId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NewMeetingViewModel : ViewModel() {
    private val streamVideo = StreamVideo.instance()

    private val _uiState = MutableStateFlow(NewMeetingUiState())
    val uiState: StateFlow<NewMeetingUiState> = _uiState

    fun createNewMeeting() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, showNewMeetingDialog = true) }
            val callId = StreamCallId("default", MeetingIdGenerator.generate())
            val call = streamVideo.call(callId.type, callId.id)
            val result = call.create()
            result.onSuccess {
                _uiState.update { it.copy(createdCallId = callId) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun dismissNewMeetingDialog() {
        _uiState.update {
            it.copy(
                showNewMeetingDialog = false,
                createdCallId = null,
            )
        }
    }
}
