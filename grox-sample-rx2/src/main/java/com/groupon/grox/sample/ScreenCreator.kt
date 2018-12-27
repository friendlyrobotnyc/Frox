package com.groupon.grox.sample

import com.groupon.grox.Store
import com.groupon.grox.rxjava2.RxStores
import com.groupon.grox.sample.action.ShowingAction
import io.reactivex.android.schedulers.AndroidSchedulers

class ScreenCreator(val screenContainer: ScreenContainer,
                    val layouts: @JvmSuppressWildcards Map<Class<*>, Int>,
                    val inflatedViews: MutableSet<Int> = HashSet(),
                    val store: Store<State>
) {

    init {
        RxStores.states(store)
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.creating }
                .filter { it !is Screen.Empty }
                .distinctUntilChanged()
                .subscribe { createScreen(it) }
    }

    fun createScreen(screen: Screen) {
        layouts.get(screen.javaClass)?.let {
            if (!inflatedViews.contains(it)) {
                inflatedViews.add(it)
                screenContainer.inflateAndAdd(it)
                store.dispatch(ShowingAction(screen))
            }
        }
    }
}