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
package com.github.workspace.googlemeetclone.ui.meeting.pip

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.video.android.compose.theme.VideoTheme
import io.getstream.video.android.compose.ui.components.call.renderer.ParticipantVideo
import io.getstream.video.android.compose.ui.components.call.renderer.RegularVideoRendererStyle
import io.getstream.video.android.core.Call
import io.getstream.video.android.mock.StreamMockUtils
import io.getstream.video.android.mock.mockCall

@Composable
fun PictureInPictureContent(call: Call) {
    val activeSpeakers by call.state.activeSpeakers.collectAsStateWithLifecycle()
    val me by call.state.me.collectAsStateWithLifecycle()

    if (activeSpeakers.isNotEmpty()) {
        ParticipantVideo(
            call = call,
            participant = activeSpeakers.first(),
            style = RegularVideoRendererStyle(labelPosition = Alignment.BottomStart),
        )
    } else if (me != null) {
        ParticipantVideo(
            call = call,
            participant = me!!,
            style = RegularVideoRendererStyle(labelPosition = Alignment.BottomStart),
        )
    }
}

@Preview
@Composable
fun PictureInPictureContentPreview() {
    StreamMockUtils.initializeStreamVideo(LocalContext.current)

    VideoTheme {
        PictureInPictureContent(call = mockCall)
    }
}
