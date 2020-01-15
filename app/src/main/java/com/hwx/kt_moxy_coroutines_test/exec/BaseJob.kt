package com.hwx.kt_moxy_coroutines_test.exec

import kotlinx.coroutines.*
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

abstract class BaseJob<T> {

    private var parentJob: Job = Job()
    private var backgroundContext: CoroutineContext = Dispatchers.IO
    private var foregroundContext: CoroutineContext = Dispatchers.Main

    abstract suspend fun executeInBackground(url: String): T

    fun execute(behavior: ExecutingBehavior<T>.() -> Unit) {
        val behaviorObject =  ExecutingBehavior<T>().apply(behavior)
        unsubscribe()
        parentJob = Job()
        CoroutineScope(foregroundContext + parentJob).launch {
            try {
                val value = withContext(backgroundContext) {
                    executeInBackground(behaviorObject.getUrl())
                }
                behaviorObject(value)
            } catch (e: Exception) {
                behaviorObject(e)
            }
        }
    }

    private fun unsubscribe() {
        parentJob.apply {
            cancelChildren()
            cancel()
        }
    }




}