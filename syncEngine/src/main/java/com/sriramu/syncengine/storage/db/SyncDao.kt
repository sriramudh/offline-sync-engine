package com.sriramu.syncengine.storage.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sriramu.syncengine.storage.db.entity.SyncTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(
        task: SyncTaskEntity
    )

    @Query(
        """
        SELECT * 
        FROM sync_tasks
        WHERE status IN ('PENDING', 'FAILED')
        ORDER BY createdAt ASC
        """
    )
    suspend fun getPendingTasks(): List<SyncTaskEntity>

    @Query(
        """
        UPDATE sync_tasks
        SET status = :status,
            updatedAt = :updatedAt,
            retryCount = :retryCount
        WHERE id = :taskId
        """
    )
    suspend fun updateTaskState(
        taskId: String,
        status: String,
        retryCount: Int,
        updatedAt: Long
    )

    @Query(
        """
        DELETE FROM sync_tasks
        WHERE id = :taskId
        """
    )
    suspend fun deleteTask(
        taskId: String
    )

    @Query(
        """
        SELECT *
        FROM sync_tasks
        ORDER BY createdAt DESC
        """
    )
    fun observeTasks(): Flow<List<SyncTaskEntity>>
}