@file:JvmName(name = "CoroutineUtils")

package com.example.kidsmarblejar.core.utils

import com.example.kidsmarblejar.core.BaseUseCase
import com.example.kidsmarblejar.core.EmptyParamsUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

/**
 * helper methods when you need just simple blocking return value from useCase
 */
fun <Type, Params> BaseUseCase<Type, Params>.justValue(params: Params, isOnWorkerThread: Boolean = true): Type where Type : Any? {
    val dispatcher = if (isOnWorkerThread) Dispatchers.IO else Dispatchers.Main
    return runBlocking(dispatcher) {

        val returnValue = CompletableDeferred<Type>()
        invoke(this, params) { result ->
            result.result(
                onSuccess = { returnValue.complete(it) },
                onFailure = { returnValue.completeExceptionally(it) }
            )
        }

        returnValue.await()
    }
}

fun <Type, Params> BaseUseCase<Type, Params>.justValueOrDefault(params: Params, isOnWorkerThread: Boolean = true, default: Type? = null): Type? where Type : Any {
    val dispatcher = if (isOnWorkerThread) Dispatchers.IO else Dispatchers.Main
    return runBlocking(dispatcher) {

        val returnValue = CompletableDeferred<Type?>()
        invoke(this, params) { result ->
            result.result(
                onSuccess = { returnValue.complete(it) },
                onFailure = { returnValue.complete(default) }
            )
        }

        returnValue.await()
    }
}

fun <Type> EmptyParamsUseCase<Type>.justValue(isOnWorkerThread: Boolean = true): Type {
    val dispatcher = if (isOnWorkerThread) Dispatchers.IO else Dispatchers.Main
    return runBlocking(dispatcher) {
        val returnValue = CompletableDeferred<Type>()
        invoke(this) {
            returnValue.complete(it)
        }
        returnValue.await()
    }
}

/**
 * helper function for getting suspend functions from kotlin to java
 */
fun <T> getBlocking(block: suspend () -> T) = runBlocking(Dispatchers.IO) {
    block()
}