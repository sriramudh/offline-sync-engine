package com.sriramu.syncengine.model

sealed interface SyncResult {

    data object Success : SyncResult

    data class Retry(
        val reason: String? = null
    ) : SyncResult

    data class Failure(
        val reason: String? = null
    ) : SyncResult
}