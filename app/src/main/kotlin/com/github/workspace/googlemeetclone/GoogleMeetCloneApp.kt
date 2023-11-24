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
package com.github.workspace.googlemeetclone

import android.app.Application
import com.github.workspace.googlemeetclone.utils.StreamVideoHelper
import io.getstream.video.android.datastore.delegate.StreamUserDataStore
import kotlinx.coroutines.runBlocking

class GoogleMeetCloneApp : Application() {

    override fun onCreate() {
        super.onCreate()
        StreamUserDataStore.install(this)
        StreamVideoHelper.init(this)

        runBlocking {
            StreamVideoHelper.signIn(StreamUserDataStore.instance())
        }
    }
}

val STREAM_SDK_ENVIRONMENT = "demo"
