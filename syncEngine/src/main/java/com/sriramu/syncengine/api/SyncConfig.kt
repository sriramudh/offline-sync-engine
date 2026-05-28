package com.sriramu.syncengine.api

import com.sriramu.syncengine.retry.RetryPolicy

data class SyncConfig(
    val retryPolicy: RetryPolicy =
        RetryPolicy()
)