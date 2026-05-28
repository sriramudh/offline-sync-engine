package com.sriramu.syncengine.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sriramu.syncengine.di.ServiceLocator
import com.sriramu.syncengine.executor.ExecutorRegistry
import com.sriramu.syncengine.model.SyncResult
import com.sriramu.syncengine.model.SyncStatus
import com.sriramu.syncengine.retry.RetryHandler
import com.sriramu.syncengine.storage.repository.SyncRepository
import kotlinx.coroutines.delay

class SyncWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(
    context,
    params
) {

    private val repository =
        ServiceLocator.provideRepository(
            context
        )

    private val retryHandler =
        ServiceLocator.provideRetryHandler()

    override suspend fun doWork(): Result {

        val tasks =
            repository.getPendingTasks()

        tasks.forEach { task ->

            repository.updateTaskStatus(
                taskId = task.id,
                status = SyncStatus.SYNCING,
                retryCount = task.retryCount
            )

            val executor =
                ExecutorRegistry.getExecutor(
                    task.executorKey
                )

            if (executor == null) {

                repository.updateTaskStatus(
                    taskId = task.id,
                    status = SyncStatus.FAILED,
                    retryCount = task.retryCount
                )

                return@forEach
            }

            when (
                val result =
                    executor.sync(
                        task.payload
                    )
            ) {

                SyncResult.Success -> {

                    repository.deleteTask(
                        task.id
                    )
                }

                is SyncResult.Retry -> {

                    handleRetry(
                        taskId = task.id,
                        retryCount =
                            task.retryCount
                    )
                }

                is SyncResult.Failure -> {

                    repository.updateTaskStatus(
                        taskId = task.id,
                        status =
                            SyncStatus.FAILED,
                        retryCount =
                            task.retryCount
                    )
                }
            }
        }

        return Result.success()
    }

    private suspend fun handleRetry(
        taskId: String,
        retryCount: Int
    ) {

        val nextRetryCount =
            retryCount + 1

        if (
            retryHandler.canRetry(
                nextRetryCount
            )
        ) {

            delay(
                retryHandler.retryDelay()
            )

            repository.updateTaskStatus(
                taskId = taskId,
                status = SyncStatus.PENDING,
                retryCount = nextRetryCount
            )

        } else {

            repository.updateTaskStatus(
                taskId = taskId,
                status = SyncStatus.FAILED,
                retryCount = nextRetryCount
            )
        }
    }
}