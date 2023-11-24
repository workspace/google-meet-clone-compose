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
package com.github.workspace.googlemeetclone.ui.meeting

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.workspace.googlemeetclone.ui.meeting.MeetingActivity.Companion.EXTRA_CID
import io.getstream.result.onErrorSuspend
import io.getstream.video.android.core.Call
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.model.StreamCallId
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MeetingViewModel(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {
    private val cid: StreamCallId = checkNotNull(savedStateHandle[EXTRA_CID])

    private val _sideEffect = Channel<MeetingSideEffect>()
    val sideEffect = _sideEffect.receiveAsFlow()

    val call: Call by lazy {
        val streamVideo = StreamVideo.instance()
        val call = streamVideo.call(type = cid.type, id = cid.id)
        viewModelScope.launch {
            call.join(create = true)
                .onErrorSuspend { error ->
                    _sideEffect.send(
                        MeetingSideEffect.ShowToast(
                            "Failed to join call (${error.message})",
                        ),
                    )
                    _sideEffect.send(MeetingSideEffect.Finish)
                }
        }
        call
    }
}

sealed interface MeetingSideEffect {
    data class ShowToast(val message: String) : MeetingSideEffect
    data object Finish : MeetingSideEffect
}
