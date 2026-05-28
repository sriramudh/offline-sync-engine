# Offline Sync Engine

A lightweight Android offline-first sync engine for automatically queueing, persisting, retrying, and syncing data when network becomes available.

Designed for apps that need reliable offline operations such as:

* Profile updates
* Form submissions
* Healthcare records
* Chat drafts
* Task synchronization
* Background API retries

## Features

✅ Offline-first queueing
✅ Persistent storage using Room
✅ Automatic retry support
✅ WorkManager background execution
✅ Network restoration auto-sync
✅ Generic payload support (JSON)
✅ Custom sync executors
✅ Lightweight and dependency-friendly

## Installation

### Maven Central

```gradle
implementation("io.github.YOUR_USERNAME:offline-sync-engine:1.0.0")
```

## Quick Start

### Initialize

```kotlin
SyncEngine.initialize(this)
```

### Register Executor

```kotlin
SyncEngine.registerExecutor(
    key = "user_update",
    executor = UserSyncExecutor()
)
```

### Enqueue Sync Task

```kotlin
val payload =
    Json.encodeToString(user)

SyncEngine.enqueue(
    payload = payload,
    type = SyncType.UPDATE,
    executorKey = "user_update"
)
```

## Example Executor

```kotlin
class UserSyncExecutor :
    SyncExecutor {

    override suspend fun sync(
        payload: String
    ): SyncResult {

        return try {

            val user =
                Json.decodeFromString<User>(
                    payload
                )

            api.updateUser(user)

            SyncResult.Success

        } catch (e: Exception) {

            SyncResult.Retry(
                e.message
            )
        }
    }
}
```

## How It Works

```text
Offline Action
      ↓
Queued Locally
      ↓
Persisted in Room
      ↓
Internet Available
      ↓
Automatic Sync
      ↓
Success / Retry / Failure
```

## Roadmap

* [ ] Generic enqueue<T>()
* [ ] Exponential retry backoff
* [ ] Sync conflict resolution
* [ ] Batch sync
* [ ] Logging hooks
* [ ] Sync analytics

---

## License
MIT License
