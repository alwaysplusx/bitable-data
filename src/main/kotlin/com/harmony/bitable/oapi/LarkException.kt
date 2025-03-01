package com.harmony.bitable.oapi

open class LarkException(
    val code: Int,
    message: String? = null,
    cause: Throwable? = null
) : RuntimeException(message, cause)
