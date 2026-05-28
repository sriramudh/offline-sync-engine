package com.sriramu.syncengine.mapper

import com.sriramu.syncengine.model.SyncStatus
import com.sriramu.syncengine.model.SyncTask
import com.sriramu.syncengine.model.SyncType
import com.sriramu.syncengine.storage.db.entity.SyncTaskEntity

internal object SyncMapper {

    fun toEntity(
        task: SyncTask
    ): SyncTaskEntity {

        return SyncTaskEntity(
            id = task.id,
            type = task.type.name,
            payload = task.payload,
            executorKey = task.executorKey,
            status = task.status.name,
            retryCount = task.retryCount,
            createdAt = task.createdAt,
            updatedAt = task.updatedAt
        )
    }

    fun toDomain(
        entity: SyncTaskEntity
    ): SyncTask {

        return SyncTask(
            id = entity.id,
            type = SyncType.valueOf(entity.type),
            payload = entity.payload,
            executorKey = entity.executorKey,
            status = SyncStatus.valueOf(entity.status),
            retryCount = entity.retryCount,
            createdAt = entity.createdAt,
            updatedAt = entity.updatedAt
        )
    }

    fun toDomainList(
        entities: List<SyncTaskEntity>
    ): List<SyncTask> {

        return entities.map(::toDomain)
    }
}