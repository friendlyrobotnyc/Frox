    package com.groupon.grox.sample.action

    import com.groupon.grox.Action
    import com.groupon.grox.sample.Creating
    import com.groupon.grox.sample.Screen
    import com.groupon.grox.sample.State


    class CreatingAction(val screen: Screen) : Action<State> {
        override fun newState(oldState: State): State {
            return oldState.Creating(Screen.Search)
        }
    }