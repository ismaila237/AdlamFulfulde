/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.bekisma.adlamfulfulde.navigation

import com.bekisma.adlamfulfulde.R

const val UID_NAV_ARGUMENT = "uid"


enum class Screen(val route: String, val titleId: Int, val hasMenuItem: Boolean = true) {
  WelcomeScreen("welcome_screen", R.string.welcome_screen, false),
  ExerciseSessions("exercise_sessions", R.string.exercise_sessions),
  ExerciseSessionDetail("exercise_session_detail", R.string.exercise_session_detail, false),
  InputReadings("input_readings", R.string.input_readings),
  DifferentialChanges("differential_changes", R.string.differential_changes),
  PrivacyPolicy("privacy_policy", R.string.privacy_policy, false)
}
