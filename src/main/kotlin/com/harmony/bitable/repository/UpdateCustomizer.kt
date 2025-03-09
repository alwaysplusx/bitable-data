package com.harmony.bitable.repository

import com.harmony.bitable.core.UpdateBodyBuilder
import com.harmony.bitable.core.UpdateRequestBuilder

interface UpdateCustomizer<T> {

    companion object {

        fun <T> wrap(customizer: (req: UpdateRequestBuilder, body: UpdateBodyBuilder) -> Unit): UpdateCustomizer<T> {
            return object : UpdateCustomizer<T> {
                override fun customize(req: UpdateRequestBuilder, body: UpdateBodyBuilder) {
                    customizer(req, body)
                }
            }
        }

    }

    fun customize(req: UpdateRequestBuilder, body: UpdateBodyBuilder)

}