/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.erginersoy.codelabworkmanager

// Notification Channel constants

// Name of Notification Channel for verbose notifications of background work
val VERBOSE_NOTIFICATION_CHANNEL_NAME: CharSequence = "Verbose WorkManager Notifications"
var VERBOSE_NOTIFICATION_CHANNEL_DESCRIPTION = "Shows notifications whenever work starts"
val NOTIFICATION_TITLE: CharSequence = "WorkRequest Starting"
val CHANNEL_ID = "VERBOSE_NOTIFICATION"
val NOTIFICATION_ID = 1

// The name of the image manipulation work
internal val IMAGE_MANIPULATION_WORK_NAME = "image_manipulation_work"

// Other keys
val OUTPUT_PATH = "blur_filter_outputs"
val KEY_IMAGE_URI = "KEY_IMAGE_URI"
internal val TAG_OUTPUT = "OUTPUT"

val DELAY_TIME_MILLIS: Long = 3000