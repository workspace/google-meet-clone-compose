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
package com.github.workspace.googlemeetclone.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.github.workspace.googlemeetclone.STREAM_SDK_ENVIRONMENT
import com.github.workspace.googlemeetclone.data.StreamService
import io.getstream.log.Priority
import io.getstream.video.android.core.StreamVideo
import io.getstream.video.android.core.StreamVideoBuilder
import io.getstream.video.android.core.logging.LoggingLevel
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import io.getstream.video.android.model.User
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull

// https://github.com/GetStream/stream-video-android/blob/develop/dogfooding/src/main/kotlin/io/getstream/video/android/util/StreamVideoInitHelper.kt
@SuppressLint("StaticFieldLeak")
object StreamVideoHelper {
    private var isInitialising = false
    private lateinit var context: Context

    fun init(appContext: Context) {
        context = appContext.applicationContext
    }

    suspend fun signIn(dataStore: StreamUserDataStore, user: User? = null) {
        if (StreamVideo.isInstalled) {
            Log.w("StreamVideoInitHelper", "[initStreamVideo] StreamVideo is already initialised.")
            return
        }

        if (isInitialising) {
            Log.d("StreamVideoInitHelper", "[initStreamVideo] StreamVideo is already initialising")
            return
        }

        isInitialising = true

        val userToLogin = user ?: dataStore.user.firstOrNull()

        if (userToLogin != null) {
            val authData = StreamService.instance.getAuthData(
                environment = STREAM_SDK_ENVIRONMENT,
                userId = userToLogin.id,
            )
            initializeStreamVideo(
                context = context,
                user = userToLogin,
                token = authData.token,
                apiKey = authData.apiKey,
            )
            dataStore.updateUser(user)
            dataStore.updateUserToken(authData.token)
        }
        isInitialising = false
    }

    suspend fun signOut() {
        StreamUserDataStore.instance().clear()
        StreamVideo.instance().logOut()
        delay(200)
        StreamVideo.removeClient()
    }

    private suspend fun initializeStreamVideo(
        context: Context,
        user: User,
        token: String,
        apiKey: String,
    ) {
        StreamVideoBuilder(
            context = context,
            user = user,
            token = token,
            apiKey = apiKey,
            loggingLevel = LoggingLevel(priority = Priority.VERBOSE),
            ensureSingleInstance = false,
            tokenProvider = {
                val authData = StreamService.instance.getAuthData(
                    environment = STREAM_SDK_ENVIRONMENT,
                    userId = user.id,
                )
                authData.token
            },
        ).build()
    }
}
