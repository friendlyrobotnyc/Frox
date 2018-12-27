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

import android.graphics.Color.rgb
import com.groupon.grox.Action
import com.groupon.grox.commands.rxjava2.RxAction
import com.groupon.grox.sample.State
import io.reactivex.Observable
import io.reactivex.Observable.error
import io.reactivex.Observable.just
import io.reactivex.schedulers.Schedulers.io
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Simulates a network call to obtain the color. This command will first ask to refresh the UI, then
 * emit a color change action or an error action.
 */
class RefreshColorRxAction : RxAction<State> {

    //don't forget to convert errors in actions
    override fun actions(): Observable<out Action<State>> {
        val refresh = just<Action<State>>(RefreshAction())

        //don't forget to convert errors in actions
        return refresh.concatWith(refreshColor()).onErrorReturn { ErrorAction(it) }
    }

    private fun refreshColor(): Observable<Action<State>> {
        return fetchColorFromServer().subscribeOn(io()).map { ChangeColorAction(it) }
    }

    //fake network call
    private fun fetchColorFromServer(): Observable<Int> {
        val result: Observable<Int>
        if (random.nextInt() % ERROR_RATE == 0) {
            result = error(RuntimeException(ERROR_MSG))
        } else {
            val red = random.nextInt(MAX_COLOR)
            val green = random.nextInt(MAX_COLOR)
            val blue = random.nextInt(MAX_COLOR)
            val color = rgb(red, green, blue)
            result = just(color)
        }
        return result.delaySubscription(LATENCY_IN_MS.toLong(), TimeUnit.MILLISECONDS)
    }

    companion object {
        private val SEED = 7
        private val ERROR_RATE = 5
        private val LATENCY_IN_MS = 1000
        private val random = Random(SEED.toLong())
        private val MAX_COLOR = 256
        private val ERROR_MSG = "Error. Please retry."
    }
}
