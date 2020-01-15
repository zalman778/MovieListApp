package com.hwx.kt_moxy_coroutines_test.exec

import java.lang.Exception
/*
    Класс хранящий поведение объекта.
 */
class ExecutingBehavior<T> {
    private var onComplete: (T) -> Unit = {}
    private var onException: (Exception) -> Unit = {}
    private var onGetUrl: () -> String = { "" }

    fun onComplete(block: (T) -> Unit) {
        onComplete = block
    }

    fun onException(block: (Exception) -> Unit) {
        onException = block
    }

    fun onGetUrl(block: () -> String) {
        onGetUrl = block
    }

    fun getUrl(): String {
        return onGetUrl.invoke()
    }

    operator fun invoke(result: T) {
        onComplete?.invoke(result)
    }

    operator fun invoke(error: Exception) {
        onException?.invoke(error)
    }
}