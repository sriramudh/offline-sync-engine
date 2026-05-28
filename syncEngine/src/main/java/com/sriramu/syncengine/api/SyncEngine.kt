package com.sriramu.syncengine.api

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.work.Configuration
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.sriramu.syncengine.di.ServiceLocator
import com.sriramu.syncengine.executor.ExecutorRegistry
import com.sriramu.syncengine.executor.SyncExecutor
import com.sriramu.syncengine.model.SyncStatus
import com.sriramu.syncengine.model.SyncTask
import com.sriramu.syncengine.model.SyncType
import com.sriramu.syncengine.network.NetworkMonitor
import com.sriramu.syncengine.storage.repository.SyncRepository
import com.sriramu.syncengine.worker.SyncWorker
import com.sriramu.syncengine.worker.factory.SyncWorkerFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.UUID


object SyncEngine {

    private lateinit var appContext:
            Context

    private lateinit var repository:
            SyncRepository

    private val networkMonitor by lazy {

        NetworkMonitor(
            context = appContext
        ) {

            triggerSync()
        }
    }

    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun initialize(
        context: Context,
        config: SyncConfig =
            SyncConfig()
    ) {

        appContext =
            context.applicationContext

        WorkManager.initialize(
            appContext,
            Configuration.Builder()
                .setWorkerFactory(
                    SyncWorkerFactory()
                )
                .build()
        )

        repository =
            ServiceLocator
                .provideRepository(
                    appContext
                )

        networkMonitor.startMonitoring()
    }

    fun registerExecutor(
        key: String,
        executor: SyncExecutor
    ) {

        ExecutorRegistry.register(
            key = key,
            executor = executor
        )
    }

    fun enqueue(
        payload: String,
        type: SyncType,
        executorKey: String
    ) {

        CoroutineScope(
            Dispatchers.IO
        ).launch {

            repository.insertTask(
                SyncTask(
                    id = UUID.randomUUID()
                        .toString(),
                    type = type,
                    payload = payload,
                    executorKey =
                        executorKey,
                    status =
                        SyncStatus.PENDING,
                    retryCount = 0,
                    createdAt =
                        System.currentTimeMillis(),
                    updatedAt =
                        System.currentTimeMillis()
                )
            )

            triggerSync()
        }
    }

    fun observeTasks():
            Flow<List<SyncTask>> {

        return repository.observeTasks()
    }

    fun triggerSync() {

        val request =
            OneTimeWorkRequestBuilder<SyncWorker>()
                .build()

        WorkManager.getInstance(
            appContext
        ).enqueueUniqueWork(
            "sync_engine_work",
            ExistingWorkPolicy.KEEP,
            request
        )
    }
}