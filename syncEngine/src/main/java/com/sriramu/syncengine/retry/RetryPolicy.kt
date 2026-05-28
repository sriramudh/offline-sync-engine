package com.sriramu.syncengine.retry

data class RetryPolicy(
    val maxRetries: Int = 3,
    val retryDelayMillis: Long = 2000L
)