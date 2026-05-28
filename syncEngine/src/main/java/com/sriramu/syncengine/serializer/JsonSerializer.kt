package com.sriramu.syncengine.serializer

import kotlinx.serialization.json.Json

internal object JsonSerializer {

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = false
        isLenient = true
        encodeDefaults = true
    }

    fun json(): Json = json
}