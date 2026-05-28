package com.sriramu.syncengine.storage.repository

import com.sriramu.syncengine.mapper.SyncMapper
import com.sriramu.syncengine.model.SyncStatus
import com.sriramu.syncengine.model.SyncTask
import com.sriramu.syncengine.storage.db.SyncDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class SyncRepository(
    private val syncDao: SyncDao
) {

    suspend fun insertTask(
        task: SyncTask
    ) {

        syncDao.insertTask(
            SyncMapper.toEntity(task)
        )
    }

    suspend fun getPendingTasks():
            List<SyncTask> {

        return SyncMapper.toDomainList(
            syncDao.getPendingTasks()
        )
    }

    suspend fun updateTaskStatus(
        taskId: String,
        status: SyncStatus,
        retryCount: Int
    ) {

        syncDao.updateTaskState(
            taskId = taskId,
            status = status.name,
            retryCount = retryCount,
            updatedAt = System.currentTimeMillis()
        )
    }

    suspend fun deleteTask(
        taskId: String
    ) {

        syncDao.deleteTask(taskId)
    }

    fun observeTasks():
            Flow<List<SyncTask>> {

        return syncDao.observeTasks()
            .map(
                SyncMapper::toDomainList
            )
    }
}