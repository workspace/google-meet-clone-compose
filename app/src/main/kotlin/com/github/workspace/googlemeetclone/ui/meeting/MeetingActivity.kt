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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import androidx.core.view.WindowCompat
import com.github.workspace.googlemeetclone.ui.MainActivity
import com.github.workspace.googlemeetclone.ui.theme.GoogleMeetTheme
import io.getstream.video.android.core.notifications.NotificationHandler
import io.getstream.video.android.model.StreamCallId

class MeetingActivity : ComponentActivity() {

    private val meetingViewModel: MeetingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            GoogleMeetTheme(darkTheme = true) {
                Surface {
                    MeetingScreen(
                        call = meetingViewModel.call,
                        onCallDisconnected = {
                            goBackToMainScreen()
                        },
                        onUserLeaveCall = {
                            meetingViewModel.call.leave()
                            goBackToMainScreen()
                        },
                    )
                }
            }
        }
    }

    private fun goBackToMainScreen() {
        if (!isFinishing) {
            val intent = Intent(this@MeetingActivity, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    companion object {
        const val EXTRA_CID: String = NotificationHandler.INTENT_EXTRA_CALL_CID

        @JvmStatic
        fun createIntent(context: Context, callId: StreamCallId): Intent {
            return Intent(context, MeetingActivity::class.java).apply {
                putExtra(EXTRA_CID, callId)
            }
        }
    }
}
