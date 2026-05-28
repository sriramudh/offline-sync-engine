package com.sriramu.syncengine.storage.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sync_tasks")
data class SyncTaskEntity(

    @PrimaryKey
    val id: String,

    val type: String,

    val payload: String,

    val executorKey: String,

    val status: String,

    val retryCount: Int,

    val createdAt: Long,

    val updatedAt: Long
)