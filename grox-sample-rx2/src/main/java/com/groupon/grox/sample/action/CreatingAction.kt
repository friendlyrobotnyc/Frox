package com.groupon.grox.sample.action

import com.groupon.grox.Action
import com.groupon.grox.sample.Screen
import com.groupon.grox.sample.ScreenContainer
import com.groupon.grox.sample.Showing
import com.groupon.grox.sample.State


class CreatingActionFactory(val screenContainer: ScreenContainer,
                            val layouts: @JvmSuppressWildcards Map<Class<*>, Int>,
                            val inflatedViews: MutableSet<Int> = HashSet()
) {
    fun new(screen: Screen) = CreatingAction(screen, screenContainer, layouts, inflatedViews)

}

class CreatingAction(val screen: Screen,
                     val screenContainer: ScreenContainer,
                     val layouts: @JvmSuppressWildcards Map<Class<*>, Int>,
                     val inflatedViews: MutableSet<Int>) : Action<State> {
    override fun newState(oldState: State): State {
        createScreen(screen)
        return oldState.Showing(Screen.Search)
    }


    fun createScreen(screenToCreate: Screen) {
        layouts.get(screenToCreate.javaClass)?.let {
            if (!inflatedViews.contains(it)) {
                inflatedViews.add(it)
                screenContainer.inflateAndAdd(it)
            }
        }
    }
}