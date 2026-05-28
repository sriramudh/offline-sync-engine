package com.sriramu.syncengine.storage.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sriramu.syncengine.storage.db.entity.SyncTaskEntity

@Database(
    entities = [SyncTaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SyncDatabase : RoomDatabase() {

    abstract fun syncDao(): SyncDao
}