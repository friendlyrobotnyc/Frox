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
package com.groupon.grox.sample.action

import com.groupon.grox.Action
import com.groupon.grox.sample.RefreshingState
import com.groupon.grox.sample.Screen
import com.groupon.grox.sample.Showing
import com.groupon.grox.sample.State

class RefreshAction : Action<State> {
    override fun newState(oldState: State): State {
        return oldState.RefreshingState()
    }
}

class ShowingAction(screen: Screen) : Action<State> {
    override fun newState(oldState: State): State =
            oldState.Showing(Screen.Search)
}
