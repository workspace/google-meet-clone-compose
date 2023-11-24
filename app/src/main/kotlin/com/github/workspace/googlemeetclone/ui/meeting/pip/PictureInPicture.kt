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

import android.app.PictureInPictureParams
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.util.Rational
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.PictureInPictureModeChangedInfo
import androidx.core.util.Consumer
import io.getstream.video.android.core.Call

// https://github.com/GetStream/stream-video-android/blob/develop/stream-video-android-ui-compose/src/main/kotlin/io/getstream/video/android/compose/pip/PictureInPicture.kt
@Suppress("DEPRECATION")
internal fun enterPictureInPicture(context: Context, call: Call) {
    if (context.packageManager.hasSystemFeature(PackageManager.FEATURE_PICTURE_IN_PICTURE)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val currentOrientation = context.resources.configuration.orientation
            val screenSharing = call.state.screenSharingSession.value

            val aspect =
                if (currentOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT && (screenSharing == null || screenSharing.participant.isLocal)) {
                    Rational(9, 16)
                } else {
                    Rational(16, 9)
                }

            val params = PictureInPictureParams.Builder()
            params.setAspectRatio(aspect).apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    setAutoEnterEnabled(true)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    setTitle("Video Player")
                    setSeamlessResizeEnabled(true)
                }
            }

            context.findActivity()?.enterPictureInPictureMode(params.build())
        } else {
            context.findActivity()?.enterPictureInPictureMode()
        }
    }
}

internal val Context.isInPictureInPictureMode: Boolean
    get() {
        val currentActivity = findActivity()
        return currentActivity?.isInPictureInPictureMode == true
    }

private fun Context.findActivity(): ComponentActivity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is ComponentActivity) return context
        context = context.baseContext
    }
    return null
}

@Composable
fun rememberPictureInPictureMode(): State<Boolean> {
    val context = LocalContext.current
    val activity = context.findActivity()
    val isInPicturePictureMode = rememberSaveable {
        mutableStateOf(activity?.isInPictureInPictureMode == true)
    }
    val listener = remember {
        Consumer<PictureInPictureModeChangedInfo> {
            isInPicturePictureMode.value = it.isInPictureInPictureMode
        }
    }

    DisposableEffect(activity, listener) {
        activity?.addOnPictureInPictureModeChangedListener(listener)
        onDispose {
            activity?.removeOnPictureInPictureModeChangedListener(listener)
        }
    }

    return isInPicturePictureMode
}
