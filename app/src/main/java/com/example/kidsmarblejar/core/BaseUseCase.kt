package com.example.kidsmarblejar.core

import kotlinx.coroutines.*

abstract class BaseUseCase<out Type, in Params> where Type: Any? {
    var enableTesting: Boolean = false
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
    private val mainDispatcher: CoroutineDispatcher = Dispatchers.Main

    abstract suspend fun run(params: Params): Result<Type, Exception>

    open operator fun invoke(
        scope: CoroutineScope,
        params: Params,
        onResult: (Result<Type, Exception>) -> Unit = {}
    ) {
        scope.launch {
            val job = if(enableTesting) withContext(mainDispatcher) { run(params) }
            else withContext(ioDispatcher) { run(params) }
            withContext(scope.coroutineContext) { onResult(job) }
        }
    }

}