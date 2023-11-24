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
import androidx.compose.material.icons.outlined.FrontHand
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import io.getstream.video.android.core.Call
import kotlinx.coroutines.launch

@Composable
fun ReactionButton(call: Call) {
    val scope = rememberCoroutineScope()
    FilledActionButton(
        onClick = {
            scope.launch {
                call.sendReaction("default", ":raise-hand:")
            }
        },
    ) {
        Icon(imageVector = Icons.Outlined.FrontHand, contentDescription = "raise hand")
    }
}
