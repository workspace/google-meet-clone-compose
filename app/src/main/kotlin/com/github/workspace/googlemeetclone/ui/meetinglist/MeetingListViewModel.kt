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

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.workspace.googlemeetclone.utils.StreamVideoHelper
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.model.SortField
import io.getstream.video.android.core.subscribeFor
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.openapitools.client.models.CallCreatedEvent

class MeetingListViewModel : ViewModel() {
    private val dataStore = StreamUserDataStore.instance()
    private val streamVideo = StreamVideo.instance()

    private val _uiState: MutableStateFlow<MeetingListUiState> =
        MutableStateFlow(MeetingListUiState())
    val uiState: StateFlow<MeetingListUiState> = _uiState

    init {
        streamVideo.subscribeFor<CallCreatedEvent> { event ->
            if (event.call.createdBy.id == streamVideo.userId) {
                load()
            }
        }

        load()

        viewModelScope.launch {
            dataStore.user.collect { user ->
                _uiState.update { it.copy(currentUser = user) }
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            StreamVideoHelper.signOut()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val userId = dataStore.user.firstOrNull()?.id ?: return@launch
            val result = streamVideo.queryCalls(
                filters = mapOf("created_by_user_id" to userId),
                sort = listOf(SortField.Desc("created_at")),
            )
            result.onSuccess { (calls) ->
                _uiState.update { it.copy(calls = calls) }
            }
            _uiState.update { it.copy(isLoading = false) }
        }
    }
}
