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
package com.groupon.grox

/**
 * Internal representation of the middle ware [Store.Middleware.Chain]. Its main role is to
 * ensure that middle wares are executed in order and that each of the intercept methods call [ ][.proceed] once and exactly once.
 *
 * @param <STATE> the class of the state.
</STATE> */
internal class RealMiddlewareChain<STATE>(
        private val store: Store<STATE>,
        private val action: Action<STATE>,
        private val middlewares: List<Store.Middleware<STATE>>,
        private val index: Int) : Store.Middleware.Chain<STATE> {

    /** Number of calls to the proceed method for the current chain / current middle ware.  */
    private var calls: Int = 0

    override fun action(): Action<STATE> {
        return action
    }

    override fun state(): STATE {
        return store.state
    }

    override fun proceed(action: Action<STATE>) {
        calls++

        // If we already have a stream, confirm that this is the only call to chain.proceed().
        if (calls > 1) {
            throw IllegalStateException(
                    "middleware " + middlewares[index - 1] + " must call proceed() exactly once")
        }

        // Call the next middleware in the chain.
        val next = RealMiddlewareChain(store, action, middlewares, index + 1)
        val middleware = middlewares[index]
        middleware.intercept(next)

        // Confirm that the next middleware made its required call to chain.proceed().
        if (index + 1 < middlewares.size && next.calls != 1) {
            throw IllegalStateException(
                    "middleware $middleware must call proceed() exactly once")
        }
    }
}
