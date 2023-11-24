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
package com.github.workspace.googlemeetclone.ui.meetinglobby

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.model.StreamCallId
import kotlinx.coroutines.launch

class MeetingLobbyViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val cid: String = checkNotNull(savedStateHandle["cid"])
    private val callId: StreamCallId = StreamCallId.fromCallCid(cid)

    val call: Call by lazy {
        val streamVideo = StreamVideo.instance()
        val call = streamVideo.call(type = callId.type, id = callId.id)
        viewModelScope.launch {
            call.create()
        }
        call
    }
}
