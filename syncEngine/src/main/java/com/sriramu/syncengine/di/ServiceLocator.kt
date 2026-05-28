package com.sriramu.syncengine.di

import android.content.Context
import androidx.room.Room
import com.sriramu.syncengine.retry.RetryHandler
import com.sriramu.syncengine.retry.RetryPolicy
import com.sriramu.syncengine.storage.db.SyncDatabase
import com.sriramu.syncengine.storage.repository.SyncRepository

internal object ServiceLocator {

    @Volatile
    private var database:
            SyncDatabase? = null

    @Volatile
    private var repository:
            SyncRepository? = null

    @Volatile
    private var retryHandler:
            RetryHandler? = null

    fun provideDatabase(
        context: Context
    ): SyncDatabase {

        return database
            ?: synchronized(this) {

                database ?: Room.databaseBuilder(
                    context.applicationContext,
                    SyncDatabase::class.java,
                    "sync_engine.db"
                ).build().also {

                    database = it
                }
            }
    }

    fun provideRepository(
        context: Context
    ): SyncRepository {

        return repository
            ?: synchronized(this) {

                repository ?: SyncRepository(
                    syncDao =
                        provideDatabase(
                            context
                        ).syncDao()
                ).also {

                    repository = it
                }
            }
    }

    fun provideRetryHandler():
            RetryHandler {

        return retryHandler
            ?: synchronized(this) {

                retryHandler ?: RetryHandler(
                    retryPolicy =
                        RetryPolicy()
                ).also {

                    retryHandler = it
                }
            }
    }
}