package com.sriramu.syncengine.executor

internal object ExecutorRegistry {

    private val executors =
        mutableMapOf<String, SyncExecutor>()

    fun register(
        key: String,
        executor: SyncExecutor
    ) {

        executors[key] = executor
    }

    fun getExecutor(
        key: String
    ): SyncExecutor? {

        return executors[key]
    }

    fun clear() {

        executors.clear()
    }
}