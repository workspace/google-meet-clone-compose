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
package com.github.workspace.googlemeetclone.ui.meeting.action

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.video.android.core.Call

@Composable
fun ToggleSpeakerPhoneButton(call: Call) {
    val isSpeakerphoneEnabled by call.speaker.speakerPhoneEnabled.collectAsStateWithLifecycle()
    IconButton(
        onClick = {
            call.speaker.setSpeakerPhone(!isSpeakerphoneEnabled)
        },
    ) {
        if (isSpeakerphoneEnabled) {
            Icon(imageVector = Icons.Default.VolumeUp, contentDescription = "speaker phone on")
        } else {
            Icon(imageVector = Icons.Default.VolumeOff, contentDescription = "speaker phone off")
        }
    }
}
