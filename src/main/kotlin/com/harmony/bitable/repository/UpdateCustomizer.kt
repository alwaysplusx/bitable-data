package com.harmony.bitable.repository

import com.harmony.bitable.core.UpdateBodyBuilder
import com.harmony.bitable.core.UpdateRequestBuilder

interface UpdateCustomizer<T> {

    fun customize(requestBuilder: UpdateRequestBuilder, bodyBuilder: UpdateBodyBuilder)

}