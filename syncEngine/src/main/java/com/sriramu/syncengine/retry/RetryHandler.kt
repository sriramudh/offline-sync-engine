package com.sriramu.syncengine.retry

internal class RetryHandler(
    private val retryPolicy: RetryPolicy
) {

    fun canRetry(
        retryCount: Int
    ): Boolean {

        return retryCount <
                retryPolicy.maxRetries
    }

    fun retryDelay(): Long {

        return retryPolicy
            .retryDelayMillis
    }
}