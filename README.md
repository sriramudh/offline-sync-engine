# Offline Sync Engine

<p align="center">
  <b>Production-ready offline sync engine for Android</b><br/>
  Queue API operations offline and automatically sync when connectivity returns.
</p>

<p align="center">
  <img src="https://img.shields.io/maven-central/v/io.github.sriramudh/offline-sync-engine" />
  <img src="https://img.shields.io/badge/platform-Android-brightgreen" />
  <img src="https://img.shields.io/badge/minSdk-29-blue" />
  <img src="https://img.shields.io/badge/Kotlin-2.x-orange" />
  <img src="https://img.shields.io/badge/license-MIT-green" />
</p>

---

## Why Offline Sync Engine?

Many Android apps fail when internet connectivity is unstable.

Examples:

* Order delivery updates
* Cart operations
* Form submissions
* Attendance systems
* Healthcare field updates
* CRM updates
* Inventory changes

Instead of failing immediately, **Offline Sync Engine** queues operations locally and syncs automatically once connectivity becomes available.

### What this solves

Without Offline Sync Engine:

```text
User clicks action
↓
Network unavailable
↓
API fails
↓
Bad UX
```

With Offline Sync Engine:

```text
User clicks action
↓
Operation stored locally
↓
Internet available
↓
Automatic sync
```

---

## Features

✅ Offline-first API queueing
✅ Automatic sync on internet reconnect
✅ WorkManager-powered background processing
✅ Retry configuration support
✅ Wi-Fi only sync option
✅ Charging-only sync option
✅ Sync callbacks (queued, started, success, failure)
✅ Type-safe payload enqueueing
✅ Lightweight integration
✅ Production-friendly architecture

---

## Installation

### Gradle

Add dependency:

```kotlin
implementation("io.github.sriramudh:offline-sync-engine:1.1.0")
```

---

## Required permissions

Add permissions to your `AndroidManifest.xml`.

```xml
<uses-permission
    android:name="android.permission.INTERNET"/>

<uses-permission
    android:name="android.permission.ACCESS_NETWORK_STATE"/>
```

---

## Quick Start (2 Minutes)

### 1. Initialize SyncEngine

Create an `Application` class.

```kotlin
class App : Application() {

    override fun onCreate() {

        super.onCreate()

        SyncEngine.initialize(
            context = applicationContext,
            config = SyncConfig(
                maxRetries = 3,
                retryDelayMillis = 5_000L,
                autoSyncOnNetwork = true,
                requireWifiOnly = false,
                requireCharging = false,
                listener =
                    object : SyncListener {

                        override fun onTaskQueued(
                            taskId: String
                        ) {
                            Log.d( "SyncEngine","Queued: $taskId"  )
                        }

                        override fun onTaskStarted(
                            taskId: String
                        ) {
                            Log.d(   "SyncEngine","Started: $taskId" )
                        }

                        override fun onTaskSuccess(
                            taskId: String
                        ) {
                            Log.d(   "SyncEngine",  "Success: $taskId" )
                        }

                        override fun onTaskFailed(
                            taskId: String,
                            reason: String?
                        ) {
                            Log.d( "SyncEngine", "Failed: $taskId" )
                        }
                    }
            )
        )
    }
}
```

---

### 2. Register an executor

Executors define **how queued data is synced to your backend API** once connectivity becomes available.

You typically register executors during app startup (usually inside your `Application` class) because they act as the sync handlers for queued operations such as orders, cart updates, attendance, or form submissions.

```kotlin
SyncEngine.registerExecutor(
    key = "orders_api",

    executor =
        object : SyncExecutor {

            override suspend fun sync(
                payload: String
            ): SyncResult {

                return runCatching {

                    val request =
                        Json.decodeFromString<OrderSyncPayload>( payload  )

                    DemoApi.service
                        .updateCart(
                            cartId = request.cartId
                        )

                    SyncResult.Success

                }.getOrElse {

                    SyncResult.Retry(
                        reason =  it.message
                    )
                }
            }
        }
)
```

---

### 3. Queue an operation

Queue operations when a user performs an action that should be synced later — for example marking an order as delivered, updating a cart, or submitting a form.

This is usually triggered from a `ViewModel`, `UseCase`, or repository layer where business actions happen, so failed network requests can safely sync later instead of breaking the user experience.

Create payload model or use your existing one.

```kotlin
@Serializable
data class OrderSyncPayload(

    val cartId: Int,
    val userId: Int,
    val status: String
)
```

Queue sync:

```kotlin
SyncEngine.enqueue(
    payload =
        OrderSyncPayload(
            cartId = 10,
            userId = 1,
            status = "DELIVERED"
        ),

    type =  SyncType.UPDATE,
    executorKey = "orders_api"
)
```

Done.

Offline Sync Engine automatically handles:

* Queueing
* Retry
* Background sync
* Network reconnect sync
* Constraints
* Callbacks

---

## Configuration

Example configuration:

```kotlin
SyncConfig(

    maxRetries = 3,

    retryDelayMillis = 5000L,

    autoSyncOnNetwork = true,

    requireWifiOnly =false,

    requireCharging = false
)
```

### Options

| Property          | Description                     |
| ----------------- | ------------------------------- |
| maxRetries        | Maximum retry attempts          |
| retryDelayMillis  | Delay before retry              |
| autoSyncOnNetwork | Auto sync on internet reconnect |
| requireWifiOnly   | Restrict sync to Wi-Fi          |
| requireCharging   | Sync only while charging        |

---

## Sync Callbacks

Listen to sync lifecycle events.

```kotlin
listener =
    object : SyncListener {

        override fun onTaskQueued(
            taskId: String
        ) {}

        override fun onTaskStarted(
            taskId: String
        ) {}

        override fun onTaskSuccess(
            taskId: String
        ) {}

        override fun onTaskFailed(
            taskId: String,
            reason: String?
        ) {}
    }
```

---

## Architecture

```text
User Action
    ↓
Queue Task
    ↓
Room Storage
    ↓
WorkManager
    ↓
Executor
    ↓
API Sync
```

---

## Example Use Cases

* E-commerce order updates
* Cart sync
* Inventory systems
* Healthcare data sync
* CRM updates
* Attendance systems
* Offline field data collection

---

## Why this library?

* Lightweight
* Minimal setup
* Offline-first design
* Production-oriented sync flow
* Clean API
* Customizable retry behavior

---

## Roadmap

* [ ] Exponential backoff retry
* [ ] Better sync analytics
* [ ] Multi-queue support
* [ ] Conflict resolution strategies
* [ ] Custom database provider support

---

## Contributing

Contributions are welcome.
Feel free to open issues or submit pull requests.

---

## License
MIT License
