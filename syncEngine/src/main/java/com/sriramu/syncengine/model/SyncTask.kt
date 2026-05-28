package com.sriramu.syncengine.model

data class SyncTask(
    val id: String,
    val type: SyncType,
    val payload: String,
    val executorKey: String,
    val status: SyncStatus,
    val retryCount: Int,
    val createdAt: Long,
    val updatedAt: Long
)