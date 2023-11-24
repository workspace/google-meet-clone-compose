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
package com.github.workspace.googlemeetclone.ui.meeting.participant

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.getstream.video.android.core.ParticipantState

@Composable
fun BoxScope.ParticipantLabel(participant: ParticipantState) {
    val name by participant.name.collectAsStateWithLifecycle()
    val isMicrophoneEnabled by participant.audioEnabled.collectAsStateWithLifecycle()

    Text(
        modifier = Modifier
            .padding(8.dp)
            .align(Alignment.BottomStart),
        text = name,
    )
    if (!isMicrophoneEnabled) {
        Surface(
            modifier = Modifier
                .padding(8.dp)
                .size(32.dp)
                .padding(4.dp)
                .align(Alignment.TopEnd),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.5F),
        ) {
            Icon(
                imageVector = Icons.Outlined.MicOff,
                contentDescription = "mic off",
            )
        }
    }
}
