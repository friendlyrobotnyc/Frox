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

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.TextView
import com.groupon.grox.Store
import com.groupon.grox.rxjava2.RxStores.states
import com.groupon.grox.sample.action.CreatingAction
import com.groupon.grox.sample.action.RefreshColorRxAction
import com.groupon.grox.sample.rx2.R
import com.jakewharton.rxbinding2.view.RxView.clicks
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.disposables.CompositeDisposable

private val screenContainer = object : ScreenContainer {
    override fun inflateAndAdd(layout: Int) {

    }
}


class MainActivity : AppCompatActivity() {

    internal val label = findViewById(R.id.label) as TextView
    private val store = Store<State>(EmptyState())
    private val compositeDisposable = CompositeDisposable()
    //creator of screen based on dispatched Creating Action
    val screenCreator = ScreenCreator(screenContainer, emptyMap(), HashSet(), store)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById(R.id.button)
        //all changes
        compositeDisposable.add(
                states(store).observeOn(mainThread())
                        .distinctUntilChanged()
                        .subscribe { it }
        )

        //individual changes
        compositeDisposable.add(
                states(store).observeOn(mainThread())
                        .map { it.error }
                        .distinctUntilChanged()
                        .subscribe({ this.showError(it) }, { this.doLog(it) }))

        compositeDisposable.add(
                states(store).observeOn(mainThread())
                        .map { it.color }
                        .distinctUntilChanged()
                        .subscribe({ this.applyColor(it) }, this::doLog))

        compositeDisposable.add(
                states(store).observeOn(mainThread())
                        .map { it.isRefreshing }
                        .distinctUntilChanged()
                        .subscribe({ this.showRefreshing(it) }, this::doLog))

        //transform clicks to refresh action stream
        compositeDisposable.add(
                clicks(button)
                        .map { click -> RefreshColorRxAction() }
                        .flatMap { it.actions() }
                        .subscribe({ store.dispatch(it) }, this::doLog))

        //showing of a screen
        compositeDisposable.add(
                states(store).observeOn(mainThread())
                        .map { it.showing }
                        .distinctUntilChanged()
                        .subscribe(this::showing, this::doLog))

       //dispatch a single action
        store.dispatch(CreatingAction(Screen.Search))

    }

    private fun showing(screen: Screen) {

    }

    private fun showError(error: String) {
        label.text = error
    }

    private fun applyColor(color: Int?) {
        if (color != INVALID_COLOR) {
            label.setBackgroundColor(color!!)
        }
    }

    private fun showRefreshing(refreshing: Boolean) {
        if (refreshing) {
            label.text = "↺"
        }
    }

    private fun doLog(throwable: Throwable) {
        Log.d("Grox", "An error occurred in a Grox chain.", throwable)
    }

    override fun onDestroy() {
        compositeDisposable.dispose()
        super.onDestroy()
    }
}
