/*
 * Copyright (c) 2017, Groupon, Inc.
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
package com.groupon.grox.sample

val INVALID_COLOR = -1


data class State(val color: Int = INVALID_COLOR,
                 val error: String = "",
                 val isRefreshing: Boolean = false,
                 val showing: Screen = Screen.Empty) {
}

sealed class Screen {
    object Empty : Screen()
    object Search : Screen()
}

fun EmptyState(): State = State()
fun State.RefreshingState(): State = this.copy(isRefreshing = true, showing = this.showing)
fun State.Success(color: Int): State = this.copy(isRefreshing = false, color = color)
fun State.Error(error: String): State = this.copy(color = INVALID_COLOR, error = error)
fun State.Showing(screen: Screen): State = this.copy(showing = screen)
