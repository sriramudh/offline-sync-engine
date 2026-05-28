package com.sriramu.syncengine.worker.factory

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.sriramu.syncengine.worker.SyncWorker

internal class SyncWorkerFactory :
    WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {

        return when (
            workerClassName
        ) {

            SyncWorker::class.java.name -> {

                SyncWorker(
                    context = appContext,
                    params = workerParameters
                )
            }

            else -> null
        }
    }
}