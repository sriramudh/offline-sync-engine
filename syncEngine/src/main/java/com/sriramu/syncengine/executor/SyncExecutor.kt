package com.sriramu.syncengine.executor

import com.sriramu.syncengine.model.SyncResult

interface SyncExecutor {

    suspend fun sync(
        payload: String
    ): SyncResult
}